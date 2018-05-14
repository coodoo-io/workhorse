package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;

import javax.inject.Inject;

import io.coodoo.workhorse.jobengine.boundary.JobContext;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

/**
 * Base worker class to define the creation and execution of jobs.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public abstract class BaseJobWorker {

    @Inject
    protected JobEngineService jobEngineService;

    @Inject
    protected JobContext jobContext;

    private Job job;

    /**
     * The job engine will uses this method as the entrance point to perform the execution.
     * 
     * @param jobExecution job execution object, containing parameters and meta information
     * @throws Exception in case the job execution fails
     */
    public abstract void doWork(JobExecution jobExecution) throws Exception;

    /**
     * Gets the Job from the database
     * 
     * @return the Job that belongs to this service
     */
    protected Job getJob() {
        if (job == null) {
            job = jobEngineService.getJobByClassName(getClass().getName());
        }
        return job;
    }

    protected void logLineWithTimestamp(String message) {
        jobContext.logLineWithTimestamp(message);
    }

    protected void logLine(String message) {
        jobContext.logLine(message);
    }

    /**
     * @return the log text of the current active job execution or <code>null</code> if there isn't any
     */
    public String getJobExecutionLog() {
        return jobContext.getLog();
    }

    /**
     * This method will (mainly) be called by the schedule timer in order to check if there is stuff to do.<br>
     * Its goal is creating {@link JobExecution} objects that gets added to the job engine to be executed.
     * <p>
     * Use <code>createJobExecution(Object parameters)</code> to add single JobExecutions!
     * </p>
     */
    public void scheduledJobExecutionCreation() {
        createJobExecution();
    }

    /**
     * <i>Convenience method to create a job execution</i><br>
     * <br>
     * This creates a parameterless {@link JobExecution} object that gets added to the job engine with default options.
     * 
     * @return job execution ID
     */
    public Long createJobExecution() {
        return create(null, null, null, null, null).getId();
    }

    protected JobExecution create(Object parameters, Boolean priority, LocalDateTime maturity, Long chainId, Long chainPreviousExecutionId) {

        Long jobId = getJob().getId();
        boolean uniqueInQueue = getJob().isUniqueInQueue();

        String parametersJson = JobEngineUtil.parametersToJson(parameters);

        return jobEngineService.createJobExecution(jobId, parametersJson, priority, maturity, chainId, chainPreviousExecutionId, uniqueInQueue);
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
