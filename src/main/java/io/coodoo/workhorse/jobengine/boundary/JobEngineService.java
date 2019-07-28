package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.framework.listing.boundary.Listing;
import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingResult;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.BaseJobWorker;
import io.coodoo.workhorse.jobengine.control.JobEngine;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.control.JobQueuePoller;
import io.coodoo.workhorse.jobengine.control.JobScheduler;
import io.coodoo.workhorse.jobengine.entity.GroupInfo;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobCountView;
import io.coodoo.workhorse.jobengine.entity.JobEngineInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionCounts;
import io.coodoo.workhorse.jobengine.entity.JobExecutionInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobExecutionView;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.util.CronExpression;

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

        for (Job job : getAllScheduledJobs()) {
            jobScheduler.start(job);
        }
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

    private void updateJobStatus(Long jobId, JobStatus status) {
        Job job = getJobById(jobId);
        job.setStatus(status);
        logger.info("Job status updated to: {}", status);
    }

    public void clearMemoryQueue(Long jobId) {
        jobEngine.clearMemoryQueue(getJobById(jobId));
    }

    public boolean isRunning() {
        return jobQueuePoller.isRunning();
    }

    public JobEngineInfo getJobEngineInfo(Long jobId) {
        return jobEngine.getInfo(jobId);
    }

    public List<Job> getAllJobs() {
        return Job.getAll(entityManager);
    }

    public Job getJobById(Long jobId) {
        return entityManager.find(Job.class, jobId);
    }

    public Long countJobsByStatus(JobStatus jobStatus) {
        return Job.countAllByStatus(entityManager, jobStatus);
    }

    public JobExecutionCounts getJobExecutionCounts(Long jobId, Integer consideredLastMinutes) {
        return JobExecutionCounts.query(entityManager, jobId, consideredLastMinutes);
    }

    public Job getJobByClassName(String className) {
        return Job.getByWorkerClassName(entityManager, className);
    }

    public BaseJobWorker getJobWorker(Job job) throws Exception {
        return jobEngineController.getJobWorker(job);
    }

    public List<Job> getAllScheduledJobs() {
        return Job.getAllScheduled(entityManager);
    }

    public Job updateJob(Long jobId, String name, String description, List<String> tags, String workerClassName, String schedule, JobStatus status, int threads,
                    Integer maxPerMinute, int failRetries, int retryDelay, int daysUntilCleanUp, boolean uniqueInQueue) {

        Job job = getJobById(jobId);

        jobScheduler.stop(job);
        jobEngine.clearMemoryQueue(job);

        job.setName(name);
        job.setDescription(description);
        job.setTags(tags);
        job.setWorkerClassName(workerClassName);
        job.setSchedule(schedule);
        job.setStatus(status);
        job.setThreads(threads);
        job.setMaxPerMinute(maxPerMinute);
        job.setFailRetries(failRetries);
        job.setRetryDelay(retryDelay);
        job.setDaysUntilCleanUp(daysUntilCleanUp);
        job.setUniqueInQueue(uniqueInQueue);

        logger.info("Job updated: {}", job);

        jobScheduler.start(job);
        return job;
    }

    public void deleteJob(Long jobId) {

        Job job = getJobById(jobId);

        jobScheduler.stop(job);
        jobEngine.clearMemoryQueue(job);

        int deletedJobExecutions = JobExecution.deleteAllByJobId(entityManager, jobId);

        entityManager.remove(job);
        logger.info("Job removed (including {} executions): {}", deletedJobExecutions, job);
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

        if (JobExecutionStatus.QUEUED == jobExecution.getStatus() && JobExecutionStatus.QUEUED != status) {
            jobEngine.removeFromMemoryQueue(jobExecution);
        }
        jobExecution.setStatus(status);
        jobExecution.setParameters(parameters);
        jobExecution.setPriority(priority);
        jobExecution.setMaturity(maturity);
        jobExecution.setFailRetry(fails);
        logger.info("JobExecution updated: {}", jobExecution);
        return jobExecution;
    }

    public void deleteJobExecution(Long jobExecutionId) {

        JobExecution jobExecution = getJobExecutionById(jobExecutionId);

        if (JobExecutionStatus.QUEUED == jobExecution.getStatus()) {
            jobEngine.removeFromMemoryQueue(jobExecution);
        }

        entityManager.remove(jobExecution);
        logger.info("JobExecution removed: {}", jobExecution);
    }

    public void triggerScheduledJobExecutionCreation(Job job) throws Exception {

        BaseJobWorker jobWorker = getJobWorker(job);
        jobWorker.onSchedule();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long currentJobExecutions(Long jobId, JobExecutionStatus jobExecutionStatus) {
        return JobExecution.countByJobIdAndStatus(entityManager, jobId, jobExecutionStatus);
    }

    /**
     * Get the next execution times of a scheduled job
     * 
     * @param jobId ID of the scheduled job
     * @param times amount of future execution times
     * @param startTime start time for this request (if <tt>null</tt> then current time is used)
     * @return List of {@link LocalDateTime} representing the next execution times of a scheduled job
     */
    public List<LocalDateTime> getNextScheduledTimes(Long jobId, int times, LocalDateTime startTime) {

        Job job = getJobById(jobId);
        return getNextScheduledTimes(job.getSchedule(), times, startTime);
    }

    /**
     * Get the next execution times defined by {@link Job#getSchedule()}
     * 
     * @param schedule CRON Expression
     * @param times amount of future execution times
     * @param startTime start time for this request (if <tt>null</tt> then current time is used)
     * @return List of {@link LocalDateTime} representing the next execution times of a scheduled job
     */
    public List<LocalDateTime> getNextScheduledTimes(String schedule, int times, LocalDateTime startTime) {

        List<LocalDateTime> nextScheduledTimes = new ArrayList<>();
        if (schedule == null) {
            return nextScheduledTimes;
        }

        CronExpression cronExpression = new CronExpression(schedule);
        LocalDateTime nextScheduledTime = startTime != null ? startTime : JobEngineUtil.timestamp();

        for (int i = 0; i < times; i++) {
            nextScheduledTime = cronExpression.nextTimeAfter(nextScheduledTime);
            nextScheduledTimes.add(nextScheduledTime);
        }
        return nextScheduledTimes;
    }

    /**
     * Get the execution times of a scheduled job
     * 
     * @param jobId ID of the scheduled job
     * @param startTime start time for this request (if <tt>null</tt> then current time is used)
     * @param endTime end time for this request (if <tt>null</tt> then current time plus 1 day is used)
     * @return List of {@link LocalDateTime} representing the execution times of a scheduled job between the <tt>startTime</tt> and <tt>endTime</tt>
     */
    public List<LocalDateTime> getScheduledTimes(Long jobId, LocalDateTime startTime, LocalDateTime endTime) {

        Job job = getJobById(jobId);
        return getScheduledTimes(job.getSchedule(), startTime, endTime);
    }

    /**
     * Get the execution times defined by {@link Job#getSchedule()}
     * 
     * @param schedule CRON Expression
     * @param startTime start time for this request (if <tt>null</tt> then current time is used)
     * @param endTime end time for this request (if <tt>null</tt> then current time plus 1 day is used)
     * @return List of {@link LocalDateTime} representing the execution times of a scheduled job between the <tt>startTime</tt> and <tt>endTime</tt>
     */
    public List<LocalDateTime> getScheduledTimes(String schedule, LocalDateTime startTime, LocalDateTime endTime) {

        List<LocalDateTime> scheduledTimes = new ArrayList<>();
        if (schedule == null) {
            return scheduledTimes;
        }

        CronExpression cronExpression = new CronExpression(schedule);
        LocalDateTime scheduledTime = startTime != null ? startTime : JobEngineUtil.timestamp();
        LocalDateTime endOfTimes = endTime != null ? endTime : scheduledTime.plusDays(1);

        while (scheduledTime.isBefore(endOfTimes)) {
            scheduledTime = cronExpression.nextTimeAfter(scheduledTime);
            scheduledTimes.add(scheduledTime);
        }
        return scheduledTimes;
    }

    public ListingResult<Job> listJobs(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, Job.class, listingParameters);
    }

    public ListingResult<JobCountView> listJobsWithCounts(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, JobCountView.class, listingParameters);
    }

    public ListingResult<JobExecutionView> listJobExecutionViews(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, JobExecutionView.class, listingParameters);
    }

}
