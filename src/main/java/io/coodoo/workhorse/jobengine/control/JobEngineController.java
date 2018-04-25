package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobScheduleConfig;
import io.coodoo.workhorse.jobengine.control.job.JobExecutionCleanupParameter;
import io.coodoo.workhorse.jobengine.control.job.JobExecutionCleanupWorker;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobSchedule;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.JobType;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineController {

    private static final int ADD_JOB_EXECUTION_LIMIT = 1000;
    private static final int ACTIVE_JOB_EXECUTION_MINIMUM_LEVEL = 100;

    private final Logger log = LoggerFactory.getLogger(JobEngineController.class);

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

        log.info("Checking JobWorker classes...");
        Map<Class<?>, JobType> availableWorkers = JobEngineUtil.getAvailableWorkers();

        for (Map.Entry<Class<?>, JobType> availableWorker : availableWorkers.entrySet()) {

            Class<?> workerClass = availableWorker.getKey();
            log.info("Found JobWorker class {}", workerClass.getSimpleName());

            Job job = jobEngineService.getJobByClassName(workerClass.getName());

            if (job == null) {
                job = new Job();

                if (workerClass.isAnnotationPresent(JobConfig.class)) {
                    // Use initial worker informations from annotation if available
                    JobConfig jobConfig = workerClass.getAnnotation(JobConfig.class);
                    job.setName(jobConfig.name().isEmpty() ? workerClass.getSimpleName() : jobConfig.name());
                    job.setDescription(jobConfig.description().isEmpty() ? null : jobConfig.description());
                    job.setWorkerClassName(workerClass.getName());
                    job.setType(availableWorker.getValue());
                    job.setStatus(jobConfig.status());
                    job.setThreads(jobConfig.threads());
                    job.setFailRetries(jobConfig.failRetries());
                    job.setRetryDelay(jobConfig.retryDelay());
                    job.setDaysUntilCleanUp(jobConfig.daysUntilCleanUp());
                    job.setUniqueInQueue(jobConfig.uniqueInQueue());
                } else {
                    // Use initial default worker informations
                    job.setName(workerClass.getSimpleName());
                    job.setWorkerClassName(workerClass.getName());
                    job.setType(availableWorker.getValue());
                    job.setStatus(JobStatus.ACTIVE);
                    job.setThreads(JobConfig.JOB_CONFIG_THREADS);
                    job.setFailRetries(JobConfig.JOB_CONFIG_FAIL_RETRIES);
                    job.setRetryDelay(JobConfig.JOB_CONFIG_RETRY_DELAY);
                    job.setDaysUntilCleanUp(JobConfig.JOB_CONFIG_DAYS_UNTIL_CLEANUP);
                    job.setUniqueInQueue(JobConfig.JOB_CONFIG_UNIQUE_IN_QUEUE);
                }
                entityManager.persist(job);

                log.info("Set up job {} for JobWorker {}", job.getName(), workerClass.getSimpleName());

                if (workerClass.isAnnotationPresent(JobScheduleConfig.class)) {
                    JobScheduleConfig jobScheduleConfig = workerClass.getAnnotation(JobScheduleConfig.class);

                    JobSchedule jobSchedule = new JobSchedule(job.getId(), jobScheduleConfig);
                    entityManager.persist(jobSchedule);

                    log.info("Set up schedule {} for JobWorker {}", jobScheduler.toString(jobScheduler.toScheduleExpression(jobSchedule)),
                                    workerClass.getSimpleName());
                }
            }
        }

        log.info("Checking persisted jobs...");
        for (Job job : jobEngineService.getAllJobs()) {

            try {
                Class<?> workerClass = Class.forName(job.getWorkerClassName());
                if (availableWorkers.get(workerClass) == null) {
                    throw new ClassNotFoundException();
                }
            } catch (ClassNotFoundException e) {

                setJobStatus(job.getId(), JobStatus.ERROR);
                log.error("No JobWorker class found for {}", job);
            }
        }
    }

    public void syncJobExecutionQueue() {

        for (Job job : Job.getAllByStatus(entityManager, JobStatus.ACTIVE)) {
            if (job.getThreads() < 1) {
                continue;
            }
            int numberOfJobExecutionsQueued = jobEngine.getNumberOfJobExecutionsInQueue(job.getId());
            int addedJobExecutions = 0;

            if (numberOfJobExecutionsQueued < ACTIVE_JOB_EXECUTION_MINIMUM_LEVEL) {
                for (JobExecution jobExecution : JobExecution.getNextCandidates(entityManager, job.getId(), ADD_JOB_EXECUTION_LIMIT)) {
                    if (jobEngine.addJobExecution(jobExecution)) {
                        addedJobExecutions++;
                    }
                }
                if (addedJobExecutions > 0) {
                    log.info("Added {} new to {} existing job executions in memory queue for job {}", addedJobExecutions, numberOfJobExecutionsQueued,
                                    job.getName());
                }
            }
        }
    }

    public BaseJobWorker getJobWorker(Job job) throws Exception {
        try {

            Class<?> workerClass = Class.forName(job.getWorkerClassName());
            return (BaseJobWorker) CDI.current().select(workerClass).get();

        } catch (Exception exception) {

            log.error("Could not find JobWorker for job {}", job.getName(), exception);

            job = jobEngineService.getJobById(job.getId());
            job.setStatus(JobStatus.ERROR);

            throw exception;
        }
    }

    /**
     * Erstellt für alle konfigurierten
     */
    public void deleteOlderJobExecutions() {
        for (Job job : Job.getAll(entityManager)) {
            if (job.getDaysUntilCleanUp() > 0) {
                jobExecutionCleanupWorker.createJobExecution(new JobExecutionCleanupParameter(job.getId(), job.getName(), job.getDaysUntilCleanUp()));
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteOlderJobExecutions(Long jobId, int minDaysOld) {
        return JobExecution.deleteOlderJobExecutions(entityManager, jobId, LocalDateTime.now().minusDays(minDaysOld));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized JobExecution handleFailedExecution(Job job, Long jobExecutionId, Exception exception, Long duration, String jobExecutionLog) {

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
