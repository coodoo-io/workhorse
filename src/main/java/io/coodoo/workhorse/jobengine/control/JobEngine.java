package io.coodoo.workhorse.jobengine.control;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import io.coodoo.workhorse.jobengine.boundary.JobContext;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.control.event.AllJobExecutionsDoneEvent;
import io.coodoo.workhorse.jobengine.control.event.JobErrorEvent;
import io.coodoo.workhorse.jobengine.entity.GroupInfo;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobEngineInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(JobEngine.class);

    @Inject
    private JobEngineService jobEngineService;

    @Inject
    private JobEngineController jobEngineController;

    @Inject
    private JobExecutor jobExecutor;

    @Inject
    private Event<AllJobExecutionsDoneEvent> allJobsDoneEvent;

    @Inject
    private Event<JobErrorEvent> jobErrorEvent;

    private Map<Long, Queue<JobExecution>> jobExecutions = new HashMap<>();
    private Map<Long, Queue<JobExecution>> priorityJobExecutions = new HashMap<>();
    private Map<Long, Set<JobExecution>> runningJobExecutions = new HashMap<>();
    private Map<Long, Integer> jobThreadCounts = new HashMap<>();
    private Map<Long, Set<JobThread>> jobThreads = new HashMap<>();
    private Map<Long, Boolean> pausedJobs = new HashMap<>();
    private Map<Long, Map<Future<Long>, JobThread>> futures = new HashMap<>();
    private Map<Long, Long> jobStartTimes = new HashMap<>();
    private static Map<Long, ReentrantLock> jobLocks = new ConcurrentHashMap<>();
    private ReentrantLock myLock = new ReentrantLock();

    public void initializeMemoryQueues() {

        logger.info("Intitialize memory queue");

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

    public ReentrantLock getLock(Long jobId) {
        ReentrantLock keyLock = jobLocks.get(jobId);

        if (keyLock == null) {
            myLock.lock();
            try {
                keyLock = jobLocks.get(jobId);
                if (keyLock == null) {
                    keyLock = new ReentrantLock();
                    jobLocks.put(jobId, keyLock);
                }
            } finally {
                myLock.unlock();
            }
        }
        return keyLock;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean addJobExecution(JobExecution jobExecution) {

        if (jobExecution == null || jobExecution.getStatus() != JobExecutionStatus.QUEUED) {
            return false;
        }

        Long jobId = jobExecution.getJobId();

        if ((runningJobExecutions.get(jobId).contains(jobExecution)) || (priorityJobExecutions.get(jobId).contains(jobExecution))
                        || (jobExecutions.get(jobId).contains(jobExecution))) {
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
        logger.debug("Added JobExecution: {} (Current queued JobExecutions: {})", jobExecution, numberOfJobs);

        if (jobThreadCounts.get(jobId) > jobThreads.get(jobId).size()) {
            final ReentrantLock lock = getLock(jobId);
            try {
                lock.lock();
                for (int i = jobThreads.get(jobId).size(); i < jobThreadCounts.get(jobId); i++) {
                    startJobThread(jobId);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Job thread started.");
                    }
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Current job thread count: {}", jobThreads.get(jobId).size());
                }
            } finally {
                lock.unlock();
            }

        }
        MDC.remove("key");
        return true;
    }

    private void startJobThread(Long jobId) {

        Job job = jobEngineService.getJobById(jobId);

        final JobThread jobThread = new JobThread() {

            boolean stopMe;
            JobExecution activeJobExecution;

            @Override
            public void run(Job job) {

                final Long jobId = job.getId();
                try {

                    final BaseJobWorker jobWorker = jobEngineController.getJobWorker(job);

                    while (true) {
                        if (this.stopMe) {
                            break;
                        }
                        if (JobEngine.this.pausedJobs.get(jobId)) {
                            logger.info("Job thread shall be paused: {}", job);
                            while (JobEngine.this.pausedJobs.get(jobId) && !stopMe) {
                                Thread.sleep(10000L);
                                logger.info("Job thread gets paused: {}", job);
                            }
                            if (stopMe) {
                                break;
                            }
                        }

                        JobExecution jobExecution;

                        ReentrantLock lock = getLock(jobId);
                        try {
                            lock.lock();
                            jobExecution = priorityJobExecutions.get(jobId).poll();
                            if (jobExecution == null) {
                                jobExecution = jobExecutions.get(jobId).poll();
                            }
                            if (jobExecution == null) {
                                logger.debug("No further job execution available for {} - removing this thread", job);

                                jobThreads.get(jobId).remove(this);
                                if (logger.isTraceEnabled()) {
                                    logger.trace("Job thread removed. Remainder: {}", jobThreads.get(jobId).size());
                                }
                                if (jobThreads.get(jobId).isEmpty()) {
                                    logger.info("All job executions done for job {}", job.getName());
                                    allJobsDoneEvent.fire(new AllJobExecutionsDoneEvent(job));
                                }
                                return;
                            }
                            activeJobExecution = jobExecution;

                        } finally {
                            lock.unlock();
                        }

                        int minMillisPerExecution = 0;
                        if (job.getMaxPerMinute() != null && job.getMaxPerMinute() > 0 && job.getMaxPerMinute() <= 60000) {
                            minMillisPerExecution = 60000 / job.getMaxPerMinute();
                        }

                        jobExecutionLoop: while (true) {

                            runningJobExecutions.get(jobId).add(jobExecution);

                            long millisAtStart = System.currentTimeMillis();
                            Long jobExecutionId = jobExecution.getId();
                            JobContext jobContext = jobWorker.getJobContext();

                            try {

                                jobEngineController.setJobExecutionRunning(jobExecutionId);

                                /* THIS IS WHERE THE MAGIC HAPPENS! */
                                jobWorker.doWork(jobExecution);

                                long duration = System.currentTimeMillis() - millisAtStart;

                                if (duration < minMillisPerExecution) {
                                    // this execution was to fast and must wait to not exceed the limit of executions per minute
                                    Thread.sleep(minMillisPerExecution - duration);
                                }

                                String jobExecutionLog = jobContext.getLog();
                                jobEngineController.setJobExecutionFinished(jobExecutionId, duration, jobExecutionLog);

                                runningJobExecutions.get(jobId).remove(jobExecution);
                                jobWorker.onFinished(jobExecutionId);

                                Long batchId = jobExecution.getBatchId();
                                if (batchId != null) {
                                    boolean batchFinished = jobEngineService.isBatchFinished(batchId);
                                    if (batchFinished) {
                                        jobWorker.onFinishedBatch(batchId, jobExecutionId);

                                        // Check if at minimum one batch execution failed and call batch fail callback
                                        GroupInfo batchInfo = jobEngineService.getJobExecutionBatchInfo(batchId);
                                        if (batchInfo.getFailed() > 0) {
                                            jobWorker.onFailedBatch(batchId, jobExecutionId);
                                        }
                                    }
                                }

                                if (jobExecution.getChainId() != null) {
                                    if (jobExecution.getFailRetryExecutionId() != null) {
                                        // retry failed execution in chain
                                        jobExecutionId = jobExecution.getFailRetryExecutionId();
                                    }
                                    JobExecution nextInChain = jobEngineController.getNextInChain(jobExecution.getChainId(), jobExecutionId);
                                    if (nextInChain != null) {
                                        jobExecution = nextInChain;
                                        activeJobExecution = jobExecution;
                                        continue jobExecutionLoop;
                                    }
                                    jobWorker.onFinishedChain(jobExecution.getChainId(), jobExecutionId);
                                }

                                break jobExecutionLoop;

                            } catch (Exception exception) {

                                runningJobExecutions.get(jobId).remove(jobExecution);

                                long duration = System.currentTimeMillis() - millisAtStart;
                                String jobExecutionLog = jobContext.getLog();
                                jobExecution = jobEngineController.handleFailedExecution(job, jobExecutionId, exception, duration, jobExecutionLog, jobWorker);
                                if (jobExecution == null) {
                                    break jobExecutionLoop; // no retry
                                }
                                activeJobExecution = jobExecution;
                                jobExecutionId = jobExecution.getId();

                                logger.info("{}. Error '{}' - next try in {} seconds", jobExecution.getFailRetry(), exception.getMessage(),
                                                job.getRetryDelay() / 1000);

                                // enforce delay before retry
                                Thread.sleep(job.getRetryDelay());
                            }
                        }
                    }
                } catch (Exception exception) {

                    logger.error("Error in job thread - Process gets cancelled", exception);

                    jobEngineController.setJobStatus(job.getId(), JobStatus.ERROR);
                    cancelProcess(job);

                    // let the JobWorker know!
                    job = jobEngineService.getJobById(job.getId());
                    jobErrorEvent.fire(new JobErrorEvent(job, exception));
                    return;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Job thread removed.");
                }
                jobThreads.get(jobId).remove(this);
            }

            @Override
            public void stop() {
                this.stopMe = true;
            }

            @Override
            public JobExecution getActiveJobExecution() {
                return activeJobExecution;
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
        if (logger.isTraceEnabled())
            logger.trace("Job thread started. Remainder: {}", jobThreads.get(job.getId()).size());
    }

    public void cancelProcess(Job job) {

        MDC.put("key", job.getName());
        if (logger.isTraceEnabled()) {
            logger.trace("Cancelling process...");
        }

        clearMemoryQueue(job);

        if (!jobThreads.get(job.getId()).isEmpty()) {
            logger.info("Process cancelled. All job threads and job executions removed.");
        }
        for (JobThread jobThread : this.jobThreads.get(job.getId())) {
            jobThread.stop();
        }
        jobThreads.get(job.getId()).clear();

        for (Future<Long> future : futures.get(job.getId()).keySet()) {
            future.cancel(true);
        }
        this.futures.get(job.getId()).clear();

    }

    public JobEngineInfo getInfo(Long jobId) {

        JobEngineInfo info = new JobEngineInfo();
        info.setJobId(jobId);

        if (jobExecutions != null && jobExecutions.get(jobId) != null) {
            info.setQueuedExecutions(jobExecutions.get(jobId).size());
        }
        if (priorityJobExecutions != null && priorityJobExecutions.get(jobId) != null) {
            info.setQueuedPriorityExecutions(priorityJobExecutions.get(jobId).size());
        }
        if (runningJobExecutions != null && runningJobExecutions.get(jobId) != null) {
            info.getRunningExecutions().addAll(runningJobExecutions.get(jobId));
        }
        if (jobThreadCounts != null && jobThreadCounts.get(jobId) != null) {
            info.setThreadCount(jobThreadCounts.get(jobId));
        }
        if (jobStartTimes != null && jobStartTimes.get(jobId) != null) {
            info.setThreadStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(jobStartTimes.get(jobId)), JobEngineConfig.TIME_ZONE));
        }
        if (pausedJobs != null && pausedJobs.get(jobId) != null) {
            info.setPaused(pausedJobs.get(jobId));
        }
        return info;
    }

    public boolean hasNoMoreJobs(Job job) {
        return (jobExecutions.get(job.getId()).isEmpty()) && (jobThreads.get(job.getId()).isEmpty());
    }

    public int getNumberOfJobExecutionsInQueue(Long jobId) {
        return jobExecutions.get(jobId).size() + priorityJobExecutions.get(jobId).size() + runningJobExecutions.get(jobId).size();
    }

    @Asynchronous
    public void allJobExecutionsDone(@Observes AllJobExecutionsDoneEvent event) {
        final Job job = event.getJob();

        Long startTime = jobStartTimes.get(job.getId());
        if (startTime != null) {

            long durationMillis = System.currentTimeMillis() - startTime;
            final String durationText = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(durationMillis),
                            TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis)));
            final String message = "Duration of all " + job.getName() + " job executions: " + durationText;

            logger.info(message);
            jobStartTimes.remove(job.getId());
        }
    }

    public void clearMemoryQueue(Job job) {

        if (!jobExecutions.containsKey(job.getId()) || !priorityJobExecutions.containsKey(job.getId())) {
            logger.warn("Job execution queue is missing for job {}", job);
            return;
        }

        int sizeMemoryQueue = jobExecutions.get(job.getId()).size();
        int sizePriorityMemoryQueue = priorityJobExecutions.get(job.getId()).size();

        if (sizeMemoryQueue > 0 || sizePriorityMemoryQueue > 0) {

            logger.info("Clearing job execution queue with {} elements and {} priority elements for job {}.", jobExecutions.get(job.getId()).size(),
                            priorityJobExecutions.get(job.getId()).size(), job.getName());

            jobExecutions.get(job.getId()).clear();
            priorityJobExecutions.get(job.getId()).clear();
        }
    }
}
