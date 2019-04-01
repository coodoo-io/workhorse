package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;
import java.util.List;

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
import io.coodoo.workhorse.jobengine.boundary.annotation.InitialJobConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobScheduleConfig;
import io.coodoo.workhorse.jobengine.control.annotation.SystemJob;
import io.coodoo.workhorse.jobengine.control.job.JobExecutionCleanupWorker;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.JobType;
import io.coodoo.workhorse.jobengine.entity.StringListConverter;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineController {

    private final Logger logger = LoggerFactory.getLogger(JobEngineController.class);

    @Inject
    JobEngine jobEngine;

    @Inject
    JobEngineService jobEngineService;

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @Inject
    JobScheduler jobScheduler;

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
                }
            } catch (ClassNotFoundException e) {

                setJobStatus(job.getId(), JobStatus.NO_WORKER);
                logger.error("No JobWorker class found for {}", job);
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

            if (initialJobConfig.schedule().isEmpty()) {
                job.setType(JobType.ON_DEMAND);
            } else {
                job.setType(JobType.SCHEDULED);
                job.setSchedule(initialJobConfig.schedule());
            }
            job.setStatus(initialJobConfig.status());
            job.setThreads(initialJobConfig.threads());

            if (initialJobConfig.maxPerMinute() != InitialJobConfig.JOB_CONFIG_MAX_PER_MINUTE) {
                job.setMaxPerMinute(initialJobConfig.maxPerMinute());
            }
            job.setFailRetries(initialJobConfig.failRetries());
            job.setRetryDelay(initialJobConfig.retryDelay());
            job.setDaysUntilCleanUp(initialJobConfig.daysUntilCleanUp());
            job.setUniqueInQueue(initialJobConfig.uniqueInQueue());

        } else if (workerClass.isAnnotationPresent(JobConfig.class)) {

            // Use initial worker informations from annotation if available
            JobConfig jobConfig = workerClass.getAnnotation(JobConfig.class);
            job.setName(jobConfig.name().isEmpty() ? workerClass.getSimpleName() : jobConfig.name());
            job.setDescription(jobConfig.description().isEmpty() ? null : jobConfig.description());
            job.setWorkerClassName(workerClass.getName());
            job.setType(JobType.ON_DEMAND);
            job.setStatus(jobConfig.status());
            job.setThreads(jobConfig.threads());
            job.setFailRetries(jobConfig.failRetries());
            job.setRetryDelay(jobConfig.retryDelay());
            job.setDaysUntilCleanUp(jobConfig.daysUntilCleanUp());
            job.setUniqueInQueue(jobConfig.uniqueInQueue());

            if (workerClass.isAnnotationPresent(JobScheduleConfig.class)) {
                JobScheduleConfig jobScheduleConfig = workerClass.getAnnotation(JobScheduleConfig.class);
                job.setType(JobType.SCHEDULED);
                job.setSchedule(jobScheduleConfig.second() + " " + jobScheduleConfig.minute() + " " + jobScheduleConfig.hour() + " "
                                + jobScheduleConfig.dayOfMonth() + " " + jobScheduleConfig.month() + " " + jobScheduleConfig.dayOfWeek() + " "
                                + jobScheduleConfig.year());
            }

        } else {

            // Use initial default worker informations
            job.setName(workerClass.getSimpleName());
            job.setWorkerClassName(workerClass.getName());
            job.setType(JobType.ON_DEMAND);
            job.setStatus(JobStatus.ACTIVE);
            job.setThreads(InitialJobConfig.JOB_CONFIG_THREADS);
            job.setFailRetries(InitialJobConfig.JOB_CONFIG_FAIL_RETRIES);
            job.setRetryDelay(InitialJobConfig.JOB_CONFIG_RETRY_DELAY);
            job.setDaysUntilCleanUp(InitialJobConfig.JOB_CONFIG_DAYS_UNTIL_CLEANUP);
            job.setUniqueInQueue(InitialJobConfig.JOB_CONFIG_UNIQUE_IN_QUEUE);
        }

        if (workerClass.isAnnotationPresent(SystemJob.class)) {
            // maybe its one of ours...
            job.setType(JobType.SYSTEM);
        }

        entityManager.persist(job);
        logger.info("Set up job {}", job.getName());
        return job;
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

            throw exception;

        } catch (Exception exception) {

            logger.error("Could not instanciate JobWorker for job {}", job, exception);

            job = jobEngineService.getJobById(job.getId());
            job.setStatus(JobStatus.ERROR);

            throw exception;
        }
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

            // create a new execution to retry the work of the failed one
            retryExecution = new JobExecution();
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
                retryExecution.setFailRetryExecutionId(jobExecutionId);
            }

            entityManager.persist(retryExecution);

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
        return retryExecution;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void setJobExecutionRunning(Long jobExecutionId) {

        JobExecution.updateStatusRunning(entityManager, JobEngineUtil.timestamp(), jobExecutionId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void setJobExecutionFinished(Long jobExecutionId, Long duration, String jobExecutionLog) {

        JobExecution.updateStatusFinished(entityManager, JobEngineUtil.timestamp(), duration, jobExecutionLog, jobExecutionId);
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
