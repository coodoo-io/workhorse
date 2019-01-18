package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.slf4j.Logger;

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
     * The job engine will uses this method to perform the job execution.
     * 
     * @param jobExecution job execution object, containing parameters and meta information
     * @throws Exception in case the job execution fails
     */
    public abstract void doWork(JobExecution jobExecution) throws Exception;

    /**
     * The job engine will call this callback method after the job execution is finished. <br>
     * <i>If needed, this method can be overwritten to react on a finished job execution.</i>
     * 
     * @param jobExecutionId ID of current job execution that is finished
     */
    public void onFinished(Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after the last job execution of a chain is finished. <br>
     * <i>If needed, this method can be overwritten to react on a finished job execution.</i>
     * 
     * @param jobExecutionId ID of last job execution of a chain that is finished
     */
    public void onChainFinished(Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after the job execution has failed and there will be a retry of the failed job execution. <br>
     * <i>If needed, this method can be overwritten to react on a finished job execution.</i>
     * 
     * @param failedExecutionId ID of current job execution that has failed
     * @param retryExecutionId ID of new job execution that that will retry the failed one
     */
    public void onRetry(Long failedExecutionId, Long retryExecutionId) {}

    /**
     * The job engine will call this callback method after the job execution has failed. <br>
     * <i>If needed, this method can be overwritten to react on a finished job execution.</i>
     * 
     * @param jobExecutionId ID of current job execution that has failed
     */
    public void onFailed(Long jobExecutionId) {}

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

    /**
     * Gets the Job-ID from the database
     * 
     * @return the ID of the job that belongs to this service
     */
    public Long getJobId() {
        if (getJob() != null) {
            return null;
        }
        return job.getId();
    }

    /**
     * Adds the message text in as a new line to the executions log
     * 
     * @param message text to log
     */
    protected void logLine(String message) {
        jobContext.logLine(message);
    }

    /**
     * Adds a timestamp followed by the message text in as a new line to the executions log <br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code><br>
     * Example: <code>[22:06:42.680] Step 3 complete</code>
     * 
     * @param message text to log
     */
    protected void logLineWithTimestamp(String message) {
        jobContext.logLineWithTimestamp(message);
    }

    /**
     * Adds a timestamp followed by the message text in as a new line to the executions log and also adds the message in severity INFO to the server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code><br>
     * Example: <code>[22:06:42.680] Step 3 complete</code>
     * 
     * @param logger server log logger
     * @param message text to log
     */
    protected void logInfo(Logger logger, String message) {
        jobContext.logInfo(logger, message);
    }

    /**
     * Adds a timestamp followed by an error marker and the error message as a new line to the executions log. It also adds the message in severity ERROR to the
     * server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code><br>
     * Error marker: <code>[ERROR]</code><br>
     * Example: <code>[22:06:42.680] [ERROR] Dafuq was that?!?!</code>
     * 
     * @param logger server log logger
     * @param message text to log
     */
    protected void logError(Logger logger, String message) {
        jobContext.logError(logger, message);
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
