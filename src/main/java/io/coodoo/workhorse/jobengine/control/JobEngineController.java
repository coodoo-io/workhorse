package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.boundary.JobLogService;
import io.coodoo.workhorse.jobengine.boundary.JobWorkerWith;
import io.coodoo.workhorse.jobengine.boundary.annotation.InitialJobConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.job.JobExecutionCleanupWorker;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.StringListConverter;
import io.coodoo.workhorse.statistic.boundary.JobStatisticService;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineController {

    private final Logger logger = LoggerFactory.getLogger(JobEngineController.class);

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @Inject
    JobEngine jobEngine;

    @Inject
    JobScheduler jobScheduler;

    @Inject
    JobEngineService jobEngineService;

    @Inject
    JobStatisticService jobStatisticService;

    @Inject
    JobLogService jobLogService;

    @Inject
    JobExecutionCleanupWorker jobExecutionCleanupWorker;

    public void checkJobConfiguration() {

        logger.info("Checking JobWorker classes...");
        List<Class<?>> availableWorkers = JobEngineUtil.getAvailableWorkers();

        for (Class<?> availableWorker : availableWorkers) {

            Class<?> workerClass = availableWorker;
            logger.info("Found JobWorker class {}", workerClass.getSimpleName());

            Job job = jobEngineService.getJobByClassName(workerClass.getName());
            if (job == null) {
                job = createJob(workerClass);
            }
        }

        logger.info("Checking persisted jobs...");
        for (Job job : jobEngineService.getAllJobs()) {

            try {
                Class<?> workerClass = Class.forName(job.getWorkerClassName());
                if (!availableWorkers.contains(workerClass)) {
                    throw new ClassNotFoundException();
                }
                if (JobStatus.NO_WORKER == job.getStatus()) {

                    setJobStatus(job.getId(), JobStatus.INACTIVE);
                    logger.error("Found JobWorker class and put it in status INACTIVE for {}", job);
                    jobLogService.logChange(job.getId(), job.getStatus(), "status", JobStatus.NO_WORKER.name(), JobStatus.INACTIVE.name(),
                                    "Worker class found" + hostNameLogPart());

                } else {

                    String parametersClassName = getParametersClassName(job);
                    if (!Objects.equals(parametersClassName, job.getParametersClassName())) {
                        logger.warn("Parameters class name of {} changed from {} to {}", job.getWorkerClassName(), job.getParametersClassName(),
                                        parametersClassName);
                        jobLogService.logChange(job.getId(), job.getStatus(), "parametersClass", job.getParametersClassName(), parametersClassName,
                                        hostNameLogPart());
                        job.setParametersClassName(parametersClassName);
                    }
                }
            } catch (ClassNotFoundException e) {

                setJobStatus(job.getId(), JobStatus.NO_WORKER);
                logger.error("No JobWorker class found for {}", job);
                jobLogService.logException(job.getId(), job.getStatus(), e, "No JobWorker class found" + hostNameLogPart());
            } catch (Exception e) {

                setJobStatus(job.getId(), JobStatus.ERROR);
                logger.error("Can't handle JobWorker class found for {}", job, e);
                jobLogService.logException(job.getId(), job.getStatus(), e, null);
            }
        }
    }

    private Job createJob(Class<?> workerClass) {

        Job job = new Job();

        if (workerClass.isAnnotationPresent(InitialJobConfig.class)) {

            // Use initial worker informations from annotation if available
            InitialJobConfig initialJobConfig = workerClass.getAnnotation(InitialJobConfig.class);
            job.setName(initialJobConfig.name().isEmpty() ? workerClass.getSimpleName() : initialJobConfig.name());
            job.setDescription(initialJobConfig.description().isEmpty() ? null : initialJobConfig.description());

            if (!initialJobConfig.tags().isEmpty()) {
                StringListConverter stringListConverter = new StringListConverter();
                job.setTags(stringListConverter.convertToEntityAttribute(initialJobConfig.tags()));
            }
            job.setWorkerClassName(workerClass.getName());
            job.setSchedule(initialJobConfig.schedule());
            job.setStatus(initialJobConfig.status());
            job.setThreads(initialJobConfig.threads());

            if (initialJobConfig.maxPerMinute() != InitialJobConfig.JOB_CONFIG_MAX_PER_MINUTE) {
                job.setMaxPerMinute(initialJobConfig.maxPerMinute());
            }
            job.setFailRetries(initialJobConfig.failRetries());
            job.setRetryDelay(initialJobConfig.retryDelay());
            job.setDaysUntilCleanUp(initialJobConfig.daysUntilCleanUp());
            job.setUniqueInQueue(initialJobConfig.uniqueInQueue());

        } else {

            // Use initial default worker informations
            job.setName(workerClass.getSimpleName());
            job.setWorkerClassName(workerClass.getName());
            job.setStatus(JobStatus.ACTIVE);
            job.setThreads(InitialJobConfig.JOB_CONFIG_THREADS);
            job.setFailRetries(InitialJobConfig.JOB_CONFIG_FAIL_RETRIES);
            job.setRetryDelay(InitialJobConfig.JOB_CONFIG_RETRY_DELAY);
            job.setDaysUntilCleanUp(InitialJobConfig.JOB_CONFIG_DAYS_UNTIL_CLEANUP);
            job.setUniqueInQueue(InitialJobConfig.JOB_CONFIG_UNIQUE_IN_QUEUE);
        }
        try {
            job.setParametersClassName(getParametersClassName(job));
        } catch (Exception e) {
            logger.error("Could not read parameters class name of job {}", job.getName());
        }

        entityManager.persist(job);
        logger.info("Set up job {}", job.getName());
        jobLogService.logMessage(job.getId(), "Job Added" + hostNameLogPart());

        return job;
    }

    private String hostNameLogPart() {
        String hostName = JobEngineUtil.getHostName();
        return hostName != null ? (" by " + hostName) : "";
    }

    public void syncJobExecutionQueue() {

        for (Job job : Job.getAllByStatus(entityManager, JobStatus.ACTIVE)) {
            if (job.getThreads() < 1) {
                continue;
            }
            int numberOfJobExecutionsQueued = jobEngine.getNumberOfJobExecutionsInQueue(job.getId());
            int addedJobExecutions = 0;

            if (numberOfJobExecutionsQueued < JobEngineConfig.JOB_QUEUE_MIN) {
                for (JobExecution jobExecution : JobExecution.getNextCandidates(entityManager, job.getId(), JobEngineConfig.JOB_QUEUE_MAX)) {
                    if (jobEngine.addJobExecution(jobExecution)) {
                        addedJobExecutions++;
                    }
                }
                if (addedJobExecutions > 0) {
                    logger.info("Added {} new to {} existing job executions in memory queue for job {}", addedJobExecutions, numberOfJobExecutionsQueued,
                                    job.getName());
                }
            }
        }
    }

    public BaseJobWorker getJobWorker(Job job) throws Exception {
        try {

            Class<?> workerClass = Class.forName(job.getWorkerClassName());
            return (BaseJobWorker) CDI.current().select(workerClass).get();

        } catch (ClassNotFoundException exception) {

            logger.error("No JobWorker class found for {}", job);

            job = jobEngineService.getJobById(job.getId());
            job.setStatus(JobStatus.NO_WORKER);

            jobLogService.logException(job.getId(), job.getStatus(), exception, "No JobWorker class found" + hostNameLogPart());
            throw exception;

        } catch (Exception exception) {

            logger.error("Could not instanciate JobWorker for job {}", job, exception);

            job = jobEngineService.getJobById(job.getId());
            job.setStatus(JobStatus.ERROR);

            jobLogService.logException(job.getId(), job.getStatus(), exception, "JobWorker could not be instanciated" + hostNameLogPart());
            throw exception;
        }
    }

    @SuppressWarnings("rawtypes")
    public String getParametersClassName(Job job) throws Exception {

        BaseJobWorker jobWorker = getJobWorker(job);

        if (jobWorker instanceof JobWorkerWith) {
            return ((JobWorkerWith) jobWorker).getParametersClassName();
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteOlderJobExecutions(Long jobId, int minDaysOld) {
        return JobExecution.deleteOlderJobExecutions(entityManager, jobId, LocalDateTime.now().minusDays(minDaysOld));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized JobExecution handleFailedExecution(Job job, Long jobExecutionId, Exception exception, Long duration, String jobExecutionLog,
                    BaseJobWorker jobWorker) {

        JobExecution failedExecution = entityManager.find(JobExecution.class, jobExecutionId);
        JobExecution retryExecution = null;

        if (failedExecution.getFailRetry() < job.getFailRetries()) {
            // retry
            retryExecution = createRetryExecution(failedExecution);
        } else if (failedExecution.getChainId() != null) {
            JobExecution.abortChain(entityManager, failedExecution.getChainId());
        }

        failedExecution.setStatus(JobExecutionStatus.FAILED);
        failedExecution.setEndedAt(JobEngineUtil.timestamp());
        failedExecution.setDuration(duration);
        failedExecution.setLog(jobExecutionLog);
        failedExecution.setFailMessage(exception.getMessage());
        failedExecution.setFailStacktrace(JobEngineUtil.stacktraceToString(exception));

        if (retryExecution == null) {
            jobWorker.onFailed(jobExecutionId);
            if (failedExecution.getChainId() != null) {
                jobWorker.onFailedChain(failedExecution.getChainId(), jobExecutionId);
            }
        } else {
            jobWorker.onRetry(jobExecutionId, retryExecution.getId());
        }
        jobStatisticService.recordFailed(job.getId(), jobExecutionId, duration);
        return retryExecution;
    }

    private JobExecution createRetryExecution(JobExecution failedExecution) {

        // create a new execution to retry the work of the failed one
        JobExecution retryExecution = new JobExecution();
        retryExecution.setJobId(failedExecution.getJobId());
        retryExecution.setStatus(failedExecution.getStatus());
        retryExecution.setStartedAt(JobEngineUtil.timestamp());
        retryExecution.setPriority(failedExecution.isPriority());
        retryExecution.setMaturity(failedExecution.getMaturity());
        retryExecution.setChainId(failedExecution.getChainId());
        retryExecution.setChainPreviousExecutionId(failedExecution.getChainPreviousExecutionId());
        retryExecution.setParameters(failedExecution.getParameters());
        retryExecution.setParametersHash(failedExecution.getParametersHash());

        // increase failure number
        retryExecution.setFailRetry(failedExecution.getFailRetry() + 1);
        if (retryExecution.getFailRetryExecutionId() == null) {
            retryExecution.setFailRetryExecutionId(failedExecution.getId());
        }

        entityManager.persist(retryExecution);
        return retryExecution;
    }

    @Asynchronous
    public void huntJobExecutionZombies() {

        if (JobEngineConfig.ZOMBIE_RECOGNITION_TIME <= 0) {
            return;
        }

        LocalDateTime time = JobEngineUtil.timestamp().minusMinutes(JobEngineConfig.ZOMBIE_RECOGNITION_TIME);
        List<JobExecution> zombies = JobExecution.findZombies(entityManager, time);

        if (!zombies.isEmpty()) {

            for (JobExecution zombie : zombies) {

                logger.warn("Zombie found! {}", zombie);
                JobExecutionStatus cure = JobEngineConfig.ZOMBIE_CURE_STATUS;

                // how to cure it?
                switch (cure) {
                    case QUEUED:
                        JobExecution retryExecution = createRetryExecution(zombie);
                        zombie.setStatus(JobExecutionStatus.FAILED);
                        logger.info("Zombie killed and risen from the death! Now it is {}", retryExecution);
                        jobLogService.logMessage(zombie.getJobId(), "Zombie execution found: Marked as failed and queued a clone");
                        break;
                    case RUNNING:
                        logger.warn("Zombie will still walk free with status {}", cure);
                        jobLogService.logMessage(zombie.getJobId(), "Zombie execution found: No action is taken");
                        break;
                    default:
                        zombie.setStatus(cure);
                        logger.info("Zombie is cured with status {}", cure);
                        jobLogService.logMessage(zombie.getJobId(), "Zombie execution found: Put in status " + cure);
                        break;
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void setJobExecutionRunning(Long jobExecutionId) {

        JobExecution.updateStatusRunning(entityManager, JobEngineUtil.timestamp(), jobExecutionId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void setJobExecutionFinished(Job job, Long jobExecutionId, Long duration, String jobExecutionLog) {

        JobExecution.updateStatusFinished(entityManager, JobEngineUtil.timestamp(), duration, jobExecutionLog, jobExecutionId);
        jobStatisticService.recordFinished(job.getId(), jobExecutionId, duration);
    }

    public synchronized JobExecution getNextInChain(Long chainId, Long currentJobExecutionId) {
        return JobExecution.getNextInChain(entityManager, chainId, currentJobExecutionId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setJobStatus(Long jobId, JobStatus status) {

        // TODO nicht über JPA lösen - Optimistic Lock bei Status aus mehreren Threads möglich - oder darauf reagieren.
        final Job job = jobEngineService.getJobById(jobId);
        if (job != null) {
            job.setStatus(status);
        }
    }

}
