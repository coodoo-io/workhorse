package io.coodoo.workhorse.jobengine.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.control.event.AllJobsDoneEvent;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

@SuppressWarnings("serial")
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobEngine implements Serializable {

    private static Logger log = LoggerFactory.getLogger(JobEngine.class);

    @Inject
    private JobEngineService jobEngineService;

    @Inject
    private JobEngineController jobEngineController;

    @Inject
    private JobExecutor jobExecutor;

    private Map<Long, Queue<JobExecution>> jobExecutions = new HashMap<>();
    private Map<Long, Queue<JobExecution>> priorityJobExecutions = new HashMap<>();
    private Map<Long, Set<JobExecution>> runningJobExecutions = new HashMap<>();

    private Map<Long, Integer> jobThreadCounts = new HashMap<>();

    private Map<Long, Set<JobThread>> jobThreads = new HashMap<>();

    private Map<Long, Map<Future<Long>, JobThread>> futures = new HashMap<>();

    // TODO muss das sein?!
    private Map<Long, Long> jobStartTimes = new HashMap<>();

    ReentrantLock myLock = new ReentrantLock();

    private static Map<Long, ReentrantLock> jobLocks = new ConcurrentHashMap<>();

    // TODO muss das sein?!
    @Inject
    private Event<AllJobsDoneEvent> processFinishedEvent;

    private Map<Long, Boolean> pausedJobs = new HashMap<>();

    public void initializeMemoryQueues() {

        log.info("Intitialize memory queue");

        this.jobThreads.clear();
        this.jobThreadCounts.clear();
        this.jobExecutions.clear();
        this.priorityJobExecutions.clear();
        this.runningJobExecutions.clear();
        this.futures.clear();
        for (Job job : jobEngineService.getAllJobs()) {
            this.jobThreads.put(job.getId(), new HashSet<JobThread>());
            this.jobThreadCounts.put(job.getId(), job.getThreads());
            this.jobExecutions.put(job.getId(), new ConcurrentLinkedQueue<JobExecution>());
            this.priorityJobExecutions.put(job.getId(), new ConcurrentLinkedQueue<JobExecution>());
            this.runningJobExecutions.put(job.getId(), new HashSet<>());
            this.pausedJobs.put(job.getId(), Boolean.valueOf(false));
            this.futures.put(job.getId(), new HashMap<Future<Long>, JobThread>());
        }
    }

    public ReentrantLock getLock(Job job) {
        ReentrantLock keyLock = jobLocks.get(job.getId());

        if (keyLock == null) {
            myLock.lock();
            try {
                keyLock = jobLocks.get(job.getId());
                if (keyLock == null) {
                    keyLock = new ReentrantLock();
                    jobLocks.put(job.getId(), keyLock);
                }
            } finally {
                myLock.unlock();
            }
        }
        return keyLock;
    }

    public boolean isJobActive(Job job) {
        return !jobExecutions.get(job.getId()).isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean addJobExecution(JobExecution jobExecution) {

        final Job job = jobEngineService.getJobById(jobExecution.getJobId());
        final Long jobId = job.getId();

        if ((runningJobExecutions.get(jobId).contains(jobExecution)) || (priorityJobExecutions.get(jobId).contains(jobExecution))
                        || (jobExecutions.get(jobId).contains(jobExecution))) {
            // log.info("JobExecution already exists in queue: {}", job);
            return false;
        }
        final int numberOfJobs = getNumberOfJobExecutionsInQueue(jobId);

        if (numberOfJobs == 0) {
            jobStartTimes.put(jobId, System.currentTimeMillis());
        }
        if (jobExecution.isPriority()) {
            priorityJobExecutions.get(jobId).add(jobExecution);
        } else {
            jobExecutions.get(jobId).add(jobExecution);
        }
        log.debug("Added JobExecution: {} (Current queued JobExecutions: {})", jobExecution, numberOfJobs);

        if (jobThreadCounts.get(jobId) > jobThreads.get(jobId).size()) {
            final ReentrantLock lock = getLock(job);
            try {
                lock.lock();
                for (int i = jobThreads.get(jobId).size(); i < jobThreadCounts.get(jobId); i++) {
                    startJobThread(job);
                    if (log.isTraceEnabled()) {
                        log.trace("Job thread started.");
                    }
                }
                if (log.isTraceEnabled()) {
                    log.trace("Current job thread count: {}", jobThreads.get(jobId).size());
                }
            } finally {
                lock.unlock();
            }

        }
        MDC.remove("key");
        return true;
    }

    private void startJobThread(Job job) {

        final JobThread jobThread = new JobThread() {

            boolean stopMe;
            JobExecution activeJob;

            public void run(Job job) {

                final Long jobId = job.getId();
                try {

                    final BaseJobWorker jobWorker = jobEngineController.getJobWorker(job);

                    while (true) {
                        activeJob = null;
                        if (this.stopMe) {
                            break;
                        }

                        if (JobEngine.this.pausedJobs.get(jobId)) {
                            log.info("Job thread shall be paused: {}", job);
                            while (JobEngine.this.pausedJobs.get(jobId) && !stopMe) {
                                Thread.sleep(10000L);
                                log.info("Job thread gets paused: {}", job);
                            }
                            if (stopMe) {
                                break;
                            }
                        }

                        JobExecution jobExecution;

                        ReentrantLock lock = getLock(job);
                        try {
                            lock.lock();
                            jobExecution = priorityJobExecutions.get(jobId).poll();
                            activeJob = jobExecution;

                            if (jobExecution == null) {
                                jobExecution = jobExecutions.get(jobId).poll();
                            }

                            if (jobExecution == null) {
                                log.debug("No further job execution available for {} - removing this thread", job);

                                jobThreads.get(jobId).remove(this);
                                if (log.isTraceEnabled()) {
                                    log.trace("Job thread removed. Remainder: {}", jobThreads.get(jobId).size());
                                }
                                if (jobThreads.get(jobId).isEmpty()) {
                                    log.info("All job executions done for job {}", job.getName());
                                    processFinishedEvent.fire(new AllJobsDoneEvent(job));
                                }
                                return;
                            }
                        } finally {
                            lock.unlock();
                        }

                        jobExecutionLoop: while (true) {

                            runningJobExecutions.get(jobId).add(jobExecution);

                            long millisAtStart = System.currentTimeMillis();
                            Long jobExecutionId = jobExecution.getId();

                            try {

                                jobEngineController.setJobExecutionRunning(jobExecutionId);

                                /* THIS IS WHERE THE MAGIC HAPPENS! */
                                jobWorker.doWork(jobExecution);

                                long duration = System.currentTimeMillis() - millisAtStart;
                                String jobExecutionLog = jobWorker.getJobExecutionLog();
                                jobEngineController.setJobExecutionFinished(jobExecutionId, duration, jobExecutionLog);

                                runningJobExecutions.get(jobId).remove(jobExecution);

                                if (jobExecution.getChainId() != null) {

                                    if (jobExecution.getFailRetryExecutionId() != null) {
                                        // retry failed execution in chain
                                        jobExecutionId = jobExecution.getFailRetryExecutionId();
                                    }

                                    jobExecution = jobEngineController.getNextInChain(jobExecution.getChainId(), jobExecutionId);

                                    if (jobExecution != null) {
                                        continue jobExecutionLoop;
                                    }
                                }
                                break jobExecutionLoop;

                            } catch (Exception exception) {

                                runningJobExecutions.get(jobId).remove(jobExecution);

                                long duration = System.currentTimeMillis() - millisAtStart;
                                String jobExecutionLog = jobWorker.getJobExecutionLog();
                                jobExecution = jobEngineController.handleFailedExecution(job, jobExecutionId, exception, duration, jobExecutionLog);

                                if (jobExecution == null) {
                                    break jobExecutionLoop; // no retry
                                }
                                jobExecutionId = jobExecution.getId();

                                log.info("{}. Error '{}' - next try in {} seconds", jobExecution.getFailRetry(), exception.getMessage(),
                                                job.getRetryDelay() / 1000);

                                // enforce delay before retry
                                Thread.sleep(job.getRetryDelay());
                            }
                        }
                    }
                } catch (Exception e) {

                    log.error("Error in job thread - Process gets cancelled", e);

                    jobEngineController.setJobStatus(job.getId(), JobStatus.ERROR);
                    cancelProcess(job);

                    return;
                }
                if (log.isTraceEnabled()) {
                    log.trace("Job thread removed.");
                }
                jobThreads.get(jobId).remove(this);

            }

            public void stop() {
                this.stopMe = true;
            }

            public JobExecution getActiveJobExecution() {
                return activeJob;
            }

        };

        // TODO: Verschiedene JobExecuter (EJB / Thread / ...) per CDI managen

        final Future<Long> future = jobExecutor.execute(job, jobThread);
        futures.get(job.getId()).put(future, jobThread);

        final List<Future<Long>> removableFutures = new ArrayList<Future<Long>>();
        for (Future<Long> jobFuture : futures.get(job.getId()).keySet()) {
            if (jobFuture.isDone()) {
                removableFutures.add(jobFuture);
            }
        }
        for (Future<Long> removableFuture : removableFutures) {
            futures.get(job.getId()).remove(removableFuture);
        }

        jobThreads.get(job.getId()).add(jobThread);
        if (log.isTraceEnabled())
            log.trace("Job thread started. Remainder: {}", jobThreads.get(job.getId()).size());
    }

    public void cancelProcess(Job job) {

        MDC.put("key", job.getName());
        if (log.isTraceEnabled()) {
            log.trace("Cancelling process...");
        }

        clearMemoryQueue(job);

        if (!jobThreads.get(job.getId()).isEmpty()) {
            log.info("Process cancelled. All job threads and job executions removed.");
        }
        for (JobThread jobThread : (Set<JobThread>) this.jobThreads.get(job.getId())) {
            jobThread.stop();
        }
        jobThreads.get(job.getId()).clear();

        for (Future<Long> future : futures.get(job.getId()).keySet()) {
            future.cancel(true);
        }
        this.futures.get(job.getId()).clear();

    }

    public String getInfo(Job job) {
        return getInfo(Arrays.asList(job));
    }

    public String getInfo() {
        return getInfo(jobEngineService.getAllJobs());
    }

    private String getInfo(List<Job> jobs) {

        final StringBuilder info = new StringBuilder();

        for (Job job : jobs) {

            Set<JobThread> jobThreadSet = jobThreads.get(job.getId());

            info.append("Job: ");
            info.append(job.getName());
            info.append(System.lineSeparator());

            info.append("Status: ");
            info.append(job.getStatus());
            if (this.pausedJobs.get(job.getId())) {
                info.append(" -paused-");
            }
            info.append(System.lineSeparator());

            info.append("Threads aktive: ");
            info.append(jobThreadSet.size());
            info.append("/");
            info.append(this.jobThreadCounts.get(job.getId()));
            info.append(System.lineSeparator());

            for (JobThread jobThread : jobThreadSet) {

                info.append("Active execution: ");
                JobExecution activeJobExecution = jobThread.getActiveJobExecution();
                if (activeJobExecution == null) {
                    info.append("-");
                } else {
                    info.append(activeJobExecution);// da keine description für execution gegeben ist eben toString...
                }
                info.append(System.lineSeparator());
            }

            info.append("Queued executions: ");
            info.append(getNumberOfJobExecutionsInQueue(job.getId()));
            info.append(System.lineSeparator());
            info.append(System.lineSeparator());
        }
        return info.toString();
    }

    public boolean hasNoMoreJobs(Job job) {
        return (jobExecutions.get(job.getId()).isEmpty()) && (jobThreads.get(job.getId()).isEmpty());
    }

    public int getNumberOfJobExecutionsInQueue(Long jobId) {
        return jobExecutions.get(jobId).size() + priorityJobExecutions.get(jobId).size() + runningJobExecutions.get(jobId).size();
    }

    // TODO muss das sein?!
    @Asynchronous
    public void allJobsDone(@Observes AllJobsDoneEvent event) {
        final Job job = event.getJob();

        Long startTime = jobStartTimes.get(job.getId());
        if (startTime != null) {

            long durationMillis = System.currentTimeMillis() - startTime;
            final String durationText = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                            TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));
            final String message = "Duration of all " + job.getName() + " job executions: " + durationText;

            log.info(message);
            jobStartTimes.remove(job.getId());
        }
    }

    public void clearMemoryQueue(Job job) {

        int sizeMemoryQueue = jobExecutions.get(job.getId()).size();
        int sizePriorityMemoryQueue = priorityJobExecutions.get(job.getId()).size();

        if (sizeMemoryQueue > 0 || sizePriorityMemoryQueue > 0) {

            log.info("Clearing job execution queue with {} elements and {} priority elements for job {}.", jobExecutions.get(job.getId()).size(),
                            priorityJobExecutions.get(job.getId()).size(), job.getName());

            jobExecutions.get(job.getId()).clear();
            priorityJobExecutions.get(job.getId()).clear();
        }
    }
}
