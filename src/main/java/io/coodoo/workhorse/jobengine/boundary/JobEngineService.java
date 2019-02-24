package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.BaseJobWorker;
import io.coodoo.workhorse.jobengine.control.JobEngine;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.JobQueuePoller;
import io.coodoo.workhorse.jobengine.control.JobScheduler;
import io.coodoo.workhorse.jobengine.entity.GroupInfo;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobSchedule;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.JobType;

/**
 * Provides basically CRUD and management functionality
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineService {

    private final Logger logger = LoggerFactory.getLogger(JobEngineService.class);

    @Inject
    JobEngine jobEngine;

    @Inject
    JobEngineController jobEngineController;

    @EJB
    JobQueuePoller jobQueuePoller;

    @EJB
    JobScheduler jobScheduler;

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    public void start() {

        logger.info("Starting job engine...");

        jobEngineController.checkJobConfiguration();
        jobEngine.initializeMemoryQueues();
        jobQueuePoller.start();
        getAllJobs().forEach(job -> jobScheduler.start(job));
    }

    public void stop() {

        logger.info("Stopping job engine...");

        jobQueuePoller.stop();

        for (Job job : getAllJobs()) {
            jobScheduler.stop(job);
            jobEngine.clearMemoryQueue(job);
        }
    }

    public void activateJob(Long jobId) {

        Job job = getJobById(jobId);

        logger.info("Activate job {}", job.getName());

        updateJobStatus(job.getId(), JobStatus.ACTIVE);
        jobScheduler.start(job);
    }

    public void deactivateJob(Long jobId) {

        Job job = getJobById(jobId);

        logger.info("Deactivate job {}", job.getName());

        updateJobStatus(job.getId(), JobStatus.INACTIVE);
        jobScheduler.stop(job);
        jobEngine.clearMemoryQueue(job);
    }

    public boolean isRunning() {
        return jobQueuePoller.isRunning();
    }

    public List<Job> getAllJobs() {
        return Job.getAll(entityManager);
    }

    public Job getJobById(Long jobId) {
        return entityManager.find(Job.class, jobId);
    }

    public Job getJobByClassName(String className) {
        return Job.getByWorkerClassName(entityManager, className);
    }

    public BaseJobWorker getJobWorker(Job job) throws Exception {
        return jobEngineController.getJobWorker(job);
    }

    public Job updateJob(Long jobId, String name, String description, String workerClassName, JobStatus status, int threads, Integer maxPerMinute,
                    int failRetries) {
        Job job = getJobById(jobId);
        job.setName(name);
        job.setDescription(description);
        job.setWorkerClassName(workerClassName);
        job.setStatus(status);
        job.setThreads(threads);
        job.setMaxPerMinute(maxPerMinute);
        job.setFailRetries(failRetries);
        logger.debug("Job updated: {}", job);
        return job;
    }

    public void deleteJob(Long jobId) {
        Job job = getJobById(jobId);
        int deletedJobExecutions = JobExecution.deleteAllByJobId(entityManager, jobId);
        if (JobType.SCHEDULED.equals(job.getType()) && getScheduleByJobId(jobId) != null) {
            deleteSchedule(jobId);
        }
        entityManager.remove(job);
        logger.debug("Job removed (including {} executions): {}", deletedJobExecutions, job);
    }

    public JobExecution getJobExecutionById(Long jobExecutionId) {
        return entityManager.find(JobExecution.class, jobExecutionId);
    }

    public GroupInfo getJobExecutionBatchInfo(Long batchId) {

        List<JobExecutionInfo> batchInfo = JobExecution.getBatchInfo(entityManager, batchId);
        return new GroupInfo(batchId, batchInfo);
    }

    /**
     * Check whether all executions of a batch job are finished.
     * 
     * @param batchId the ID of the batch executions
     * @return <code>true</code> if no execution of this batch job is either queued or running.
     */
    public boolean isBatchFinished(Long batchId) {
        Long queuedExecutions = countBatchExecutions(batchId, JobExecutionStatus.QUEUED);
        if (queuedExecutions.equals(0l)) {
            Long runningExecutions = countBatchExecutions(batchId, JobExecutionStatus.RUNNING);
            if (runningExecutions.equals(0l)) {
                return true;
            }
        }
        return false;
    }

    public Long countBatchExecutions(Long batchId, JobExecutionStatus status) {
        return JobExecution.countBatchByStatus(entityManager, batchId, status);
    }

    public List<JobExecution> getJobExecutionBatch(Long batchId) {
        return JobExecution.getBatch(entityManager, batchId);
    }

    public GroupInfo getJobExecutionChainInfo(Long chainId) {

        List<JobExecutionInfo> batchInfo = JobExecution.getChainInfo(entityManager, chainId);
        return new GroupInfo(chainId, batchInfo);
    }

    public List<JobExecution> getJobExecutionChain(Long chainId) {
        return JobExecution.getChain(entityManager, chainId);
    }

    public List<JobExecution> getAllByStatus(JobExecutionStatus jobExecutionStatus) {
        return JobExecution.getAllByStatus(entityManager, jobExecutionStatus);
    }

    public List<JobExecution> getAllByJobIdAndStatus(Long jobId, JobExecutionStatus jobExecutionStatus) {
        return JobExecution.getAllByJobIdAndStatus(entityManager, jobId, jobExecutionStatus);
    }

    public JobExecution createJobExecution(Long jobId, String parameters, Boolean priority, LocalDateTime maturity, Long batchId, Long chainId,
                    Long previousJobExecutionId, boolean uniqueInQueue) {

        Integer parametersHash = null;
        if (parameters != null) {
            parametersHash = parameters.hashCode();
            if (parameters.trim().isEmpty() || parameters.isEmpty()) {
                parameters = null;
                parametersHash = null;
            }
        }

        if (uniqueInQueue) {
            // Prüfen ob es bereits eine Job Excecution mit diesn Parametern existiert und im Status QUEUED ist. Wenn ja diese zurückgeben.
            JobExecution equalQueuedJobExcecution = JobExecution.getFirstCreatedByJobIdAndParametersHash(entityManager, jobId, parametersHash);
            if (equalQueuedJobExcecution != null) {
                return equalQueuedJobExcecution;
            }
        }

        JobExecution jobExecution = new JobExecution();
        jobExecution.setJobId(jobId);
        jobExecution.setStatus(JobExecutionStatus.QUEUED);
        jobExecution.setParameters(parameters);
        jobExecution.setParametersHash(parametersHash);
        jobExecution.setFailRetry(0);
        jobExecution.setPriority(priority != null ? priority : false);
        jobExecution.setMaturity(maturity);
        jobExecution.setBatchId(batchId);
        jobExecution.setChainId(chainId);
        jobExecution.setChainPreviousExecutionId(previousJobExecutionId);

        entityManager.persist(jobExecution);
        logger.debug("JobExecution created: {}", jobExecution);
        return jobExecution;
    }

    public JobExecution updateJobExecution(Long jobExecutionId, JobExecutionStatus status, String parameters, boolean priority, LocalDateTime maturity,
                    int fails) {
        JobExecution jobExecution = getJobExecutionById(jobExecutionId);
        jobExecution.setStatus(status);
        jobExecution.setParameters(parameters);
        jobExecution.setPriority(priority);
        jobExecution.setMaturity(maturity);
        jobExecution.setFailRetry(fails);
        logger.debug("JobExecution updated: {}", jobExecution);
        return jobExecution;
    }

    public void deleteJobExecution(Long jobExecutionId) {
        JobExecution jobExecution = getJobExecutionById(jobExecutionId);
        entityManager.remove(jobExecution);
        logger.debug("JobExecution removed: {}", jobExecution);
    }

    public void updateJobStatus(Long jobId, JobStatus status) {
        Job job = getJobById(jobId);
        job.setStatus(status);
        logger.debug("Job status updated to: {}", status);
    }

    public void clearMemoryQueue(Long jobId) {
        jobEngine.clearMemoryQueue(getJobById(jobId));
    }

    public List<JobSchedule> getAllSchedules() {
        return JobSchedule.getAll(entityManager);
    }

    public JobSchedule getScheduleById(Long scheduleId) {
        return entityManager.find(JobSchedule.class, scheduleId);
    }

    public JobSchedule getScheduleByJobId(Long jobId) {
        return JobSchedule.getByJobId(entityManager, jobId);
    }

    public JobSchedule updateSchedule(Long jobId, String second, String minute, String hour, String dayOfWeek, String dayOfMonth, String month, String year) {
        JobSchedule jobSchedule = getScheduleByJobId(jobId);
        jobSchedule.setSecond(second);
        jobSchedule.setMinute(minute);
        jobSchedule.setHour(hour);
        jobSchedule.setDayOfWeek(dayOfWeek);
        jobSchedule.setDayOfMonth(dayOfMonth);
        jobSchedule.setMonth(month);
        jobSchedule.setYear(year);
        logger.debug("Schedule updated: {}", jobSchedule);

        jobScheduler.start(getJobById(jobId));

        return jobSchedule;
    }

    public void deleteSchedule(Long jobId) {
        JobSchedule jobSchedule = getScheduleByJobId(jobId);

        Job job = getJobById(jobSchedule.getJobId());
        jobScheduler.stop(job);

        entityManager.remove(jobSchedule);
        logger.debug("Schedule removed: {}", jobSchedule);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long currentJobExecutions(Long jobId, JobExecutionStatus jobExecutionStatus) {
        return JobExecution.countByJobIdAndStatus(entityManager, jobId, jobExecutionStatus);
    }

}
