package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

/**
 * Base worker class to define the creation and execution of jobs.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public abstract class JobWorker {

    @Inject
    protected JobEngineService jobEngineService;

    @Inject
    JobExecutionLogger jobExecutionLogger;

    private Job job;

    private JobExecution jobExecution;

    /**
     * Gets the Job from the database
     * 
     * @return the Job that belongs to this service
     */
    private Job getJob() {
        if (job == null) {
            job = jobEngineService.getJobByClassName(getClass().getName());
        }
        return job;
    }

    /**
     * <i>This is where the magic happens!</i>
     * <p>
     * The job engine will call this method, so this is where you can do whatever you want.
     * </p>
     * <p>
     * If you added {@link JobExecutionParameters} to this Job, you can get it using {@link #getParameters()}.
     * </p>
     * 
     * @throws Exception in case the job execution fails
     */
    public abstract void doWork() throws Exception;

    /**
     * The job engine will uses this method as the entrance point to prepare an execute the {@link #doWork()} method.
     * 
     * @param jobExecution job execution object, containing parameters and meta information
     * @throws Exception in case the job execution fails
     */
    public void doWork(JobExecution jobExecution) throws Exception {

        this.jobExecution = jobExecution;
        jobExecutionLogger.setLog(jobExecution.getLog());
        doWork();
    }

    @SuppressWarnings("unchecked")
    protected <T> T getParameters() {

        if (jobExecution != null && jobExecution.getParameters() != null) {
            return (T) jobExecution.getParameters();
        }
        return null;
    }

    protected void logLineWithTimestamp(String message) {
        jobExecutionLogger.lineWithTimestamp(message);
    }

    protected void logLine(String message) {
        jobExecutionLogger.line(message);
    }

    /**
     * @return the log text of the current active job execution or <code>null</code> if there isn't any
     */
    public String getJobExecutionLog() {
        return jobExecutionLogger.getLog();
    }

    /**
     * This method will (mainly) be called by the schedule timer in order to check if there is stuff to do.<br>
     * Its goal is creating {@link JobExecution} objects that gets added to the job engine to be executed.
     * <p>
     * Use <code>createJobExecution(JobExecutionParameters parameters)</code> to add single JobExecutions!
     * </p>
     */
    public void scheduledJobExecutionCreation() {
        createJobExecution();
    }

    /**
     * <i>Convenience method to {@link #createJobExecution(JobExecutionParameters)}</i><br>
     * <br>
     * This creates a parameterless {@link JobExecution} object that gets added to the job engine with default options.
     * 
     * @return job execution ID
     */
    public Long createJobExecution() {
        return createJobExecution(null);
    }

    /**
     * <i>Convenience method to {@link #createJobExecution(JobExecutionParameters, Boolean, LocalDateTime)}</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the job engine with default options.
     * 
     * @param parameters needed parameters to do the job
     * @return job execution ID
     */
    public Long createJobExecution(JobExecutionParameters parameters) {
        return createJobExecution(parameters, false, null);
    }

    /**
     * <i>This is an access point to get the job engine started with a new job with job parameters.</i><br>
     * <br>
     * 
     * This creates a {@link JobExecution} object that gets added to the job engine to be executed as soon as possible.
     * 
     * @param parameters needed parameters to do the job
     * @param priority priority queuing
     * @param maturity specified time for the execution
     * @return job execution ID
     */
    public Long createJobExecution(JobExecutionParameters parameters, Boolean priority, LocalDateTime maturity) {
        return create(parameters, priority, maturity, null, null).getId();
    }

    /**
     * <i>This is an access point to get the job engine started with a new job with job parameters.</i><br>
     * <br>
     * 
     * This creates a {@link JobExecution} object that gets added to the job engine to be executed as soon as possible.
     * 
     * @param parameters needed parameters to do the job
     * @param priority priority queuing
     * @param delayValue time to wait
     * @param delayUnit what kind of time to wait
     * @return job execution ID
     */
    public Long createJobExecution(JobExecutionParameters parameters, Boolean priority, Long delayValue, ChronoUnit delayUnit) {
        return create(parameters, priority, delayToMaturity(delayValue, delayUnit), null, null).getId();
    }

    /**
     * <i>Convenience method to {@link #createJobExecution(JobExecutionParameters, Boolean, LocalDateTime)}</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the priority queue of the job engine to be treated first class.
     * 
     * @param parameters needed parameters to do the job
     * @return job execution ID
     */
    public Long createPriorityJobExecution(JobExecutionParameters parameters) {
        return createJobExecution(parameters, true, null);
    }

    /**
     * <i>Convenience method to {@link #createJobExecution(JobExecutionParameters, Boolean, Long, ChronoUnit)}</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the job engine after the given delay.
     * 
     * @param parameters needed parameters to do the job
     * @param delayValue time to wait
     * @param delayUnit what kind of time to wait
     * @return job execution ID
     */
    public Long createDelayedJobExecution(JobExecutionParameters parameters, Long delayValue, ChronoUnit delayUnit) {
        return createJobExecution(parameters, false, delayValue, delayUnit);
    }

    /**
     * <i>Convenience method to {@link #createJobExecution(JobExecutionParameters, Boolean, LocalDateTime)}</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the job engine at a specified time.
     * 
     * @param parameters needed parameters to do the job
     * @param maturity specified time for the execution
     * @return job execution ID
     */
    public Long createPlannedJobExecution(JobExecutionParameters parameters, LocalDateTime maturity) {
        return createJobExecution(parameters, false, maturity);
    }

    /**
     * This creates a chain of {@link JobExecution} objects, so when the first one gets executed it will bring all its chained friends.
     * 
     * @param parametersList list of needed parameters to do the job in the order of the execution chain
     * @return chain ID
     */
    public Long createChainedJobExecutions(List<JobExecutionParameters> parametersList) {
        return createChainedJobExecutions(parametersList, false, null);
    }

    /**
     * This creates a chain of {@link JobExecution} objects that gets added to the priority queue of the job engine to be treated first class. So when the first
     * one gets executed it will bring all its chained friends.
     * 
     * @param parametersList list of needed parameters to do the job in the order of the execution chain
     * @return chain ID
     */
    public Long createPriorityChainedJobExecutions(List<JobExecutionParameters> parametersList) {
        return createChainedJobExecutions(parametersList, true, null);
    }

    /**
     * This creates a chain of {@link JobExecution} objects that gets added to the job engine after the given delay. So when the first one gets executed it will
     * bring all its chained friends.
     * 
     * @param parametersList list of needed parameters to do the job in the order of the execution chain
     * @param maturity specified time for the execution
     * @return chain ID
     */
    public Long createPlannedChainedJobExecutions(List<JobExecutionParameters> parametersList, LocalDateTime maturity) {
        return createChainedJobExecutions(parametersList, false, maturity);
    }

    /**
     * This creates a chain of {@link JobExecution} objects that gets added to the job engine after the given delay. So when the first one gets executed it will
     * bring all its chained friends.
     * 
     * @param parametersList list of needed parameters to do the job in the order of the execution chain
     * @param delayValue time to wait
     * @param delayUnit what kind of time to wait
     * @return chain ID
     */
    public Long createDelayedChainedJobExecutions(List<JobExecutionParameters> parametersList, Long delayValue, ChronoUnit delayUnit) {
        return createChainedJobExecutions(parametersList, false, delayToMaturity(delayValue, delayUnit));
    }

    /**
     * This creates a chain of {@link JobExecution} objects, so when the first one gets executed it will bring all its chained friends.
     * 
     * @param parametersList list of needed parameters to do the job in the order of the execution chain
     * @param priority priority queuing
     * @param maturity specified time for the execution
     * @return chain ID
     */
    public Long createChainedJobExecutions(List<JobExecutionParameters> parametersList, Boolean priority, LocalDateTime maturity) {

        Long chainId = null;
        Long chainPreviousExecutionId = null;

        for (JobExecutionParameters parameters : parametersList) {
            if (chainId == null) { // start of chain
                // mark as chained, so the poller wont draft it to early
                Long id = create(parameters, priority, maturity, -1L, -1L).getId();
                JobExecution jobExecution = jobEngineService.getJobExecutionById(id);

                chainPreviousExecutionId = id;
                chainId = id;
                jobExecution.setChainId(chainId);
                jobExecution.setChainPreviousExecutionId(null);

            } else { // chain peasants
                chainPreviousExecutionId = create(parameters, priority, maturity, chainId, chainPreviousExecutionId).getId();
            }
        }
        return chainId;
    }

    private LocalDateTime delayToMaturity(Long delayValue, ChronoUnit delayUnit) {

        LocalDateTime maturity = null;
        if (delayValue != null && delayUnit != null) {
            maturity = JobEngineUtil.timestamp().plus(delayValue, delayUnit);
        }
        return maturity;
    }

    private JobExecution create(JobExecutionParameters parameters, Boolean priority, LocalDateTime maturity, Long chainId, Long chainPreviousExecutionId) {

        Long jobId = getJob().getId();
        boolean uniqueInQueue = getJob().isUniqueInQueue();

        return jobEngineService.createJobExecution(jobId, parameters, priority, maturity, chainId, chainPreviousExecutionId, uniqueInQueue);
    }

    public long currentQueuedExecutions() {
        return jobEngineService.currentJobExecutions(getJob().getId(), JobExecutionStatus.QUEUED);
    }

    public long currentRunningExecutions() {
        return jobEngineService.currentJobExecutions(getJob().getId(), JobExecutionStatus.RUNNING);
    }

    public long currentFinishedExecutions() {
        return jobEngineService.currentJobExecutions(getJob().getId(), JobExecutionStatus.FINISHED);
    }

    public long currentAbortedExecutions() {
        return jobEngineService.currentJobExecutions(getJob().getId(), JobExecutionStatus.ABORTED);
    }

    public long currentFailedExecutions() {
        return jobEngineService.currentJobExecutions(getJob().getId(), JobExecutionStatus.FAILED);
    }

}
