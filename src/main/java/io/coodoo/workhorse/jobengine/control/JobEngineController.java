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
import io.coodoo.workhorse.jobengine.boundary.JobWorker;
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

                JobConfig jobConfig = workerClass.getAnnotation(JobConfig.class);
                JobType jobType = availableWorker.getValue();

                job = new Job(workerClass, jobType, jobConfig);
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

    public JobWorker getJobWorker(Job job) throws Exception {
        try {

            Class<?> workerClass = Class.forName(job.getWorkerClassName());
            return (JobWorker) CDI.current().select(workerClass).get();

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
            if (job.getDaysUntilCLeanUp() > 0) {
                jobExecutionCleanupWorker.createJobExecution(new JobExecutionCleanupParameter(job.getId(), job.getName(), job.getDaysUntilCLeanUp()));
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteOlderJobExecutions(Long jobId, int minDaysOld) {
        return JobExecution.deleteOlderJobExecutions(entityManager, jobId, LocalDateTime.now().minusDays(minDaysOld));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized JobExecution handleFailedExecution(Job job, Long jobExecutionId, Exception exception, Long duration) {

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
            retryExecution.setParametersJson(failedExecution.getParametersJson());

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
        failedExecution.setFailMessage(exception.getMessage());
        failedExecution.setFailStacktrace(JobEngineUtil.stacktraceToString(exception));

        return retryExecution;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void setJobExecutionStatus(Long jobExecutionId, JobExecutionStatus jobExecutionStatus, Long duration) {

        final LocalDateTime now = JobEngineUtil.timestamp();
        switch (jobExecutionStatus) {
            case RUNNING:
                JobExecution.updateStarted(entityManager, now, jobExecutionId);
                break;
            case FINISHED:
            case FAILED:
                JobExecution.updateEnded(entityManager, jobExecutionStatus, now, duration, jobExecutionId);
                break;
            case ABORTED:
            case QUEUED:
                JobExecution.updateStatus(entityManager, jobExecutionStatus, now, jobExecutionId);
                break;
        }
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
