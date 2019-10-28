package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;

import javax.ejb.Asynchronous;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import io.coodoo.workhorse.jobengine.boundary.JobContext;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.control.event.AllJobExecutionsDoneEvent;
import io.coodoo.workhorse.jobengine.control.event.JobErrorEvent;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.log.boundary.JobEngineLogService;
import io.coodoo.workhorse.log.entity.Log;
import io.coodoo.workhorse.util.JobEngineUtil;

/**
 * Base worker class to define the creation and execution of jobs.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public abstract class BaseJobWorker {

    @Inject
    protected JobEngineService jobEngineService;

    @Inject
    protected JobEngineLogService jobEngineLogService;

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
     * This method will be called by the schedule timer in order to check if there is stuff to do.<br>
     * Its goal is to create one (or more) {@link JobExecution} that gets added to the job engine to be executed. <i>If not overwritten, this method will create
     * a {@link JobExecution} without parameters or specific settings.</i>
     */
    public void onSchedule() {
        createJobExecution();
    }

    /**
     * The job engine will call this callback method after the job execution is finished. <br>
     * <i>If needed, this method can be overwritten to react on a finished job execution.</i>
     * 
     * @param jobExecutionId ID of current job execution that is finished
     */
    public void onFinished(Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after the last job execution of a batch is finished. <br>
     * <i>If needed, this method can be overwritten to react on a finished batch.</i>
     * 
     * @param batchId batch ID
     * @param jobExecutionId ID of last job execution of a batch that is finished
     */
    public void onFinishedBatch(Long batchId, Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after the last job execution of a chain is finished. <br>
     * <i>If needed, this method can be overwritten to react on a finished chain.</i>
     * 
     * @param chainId chain ID
     * @param jobExecutionId ID of last job execution of a chain that is finished
     */
    public void onFinishedChain(Long chainId, Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after the job execution has failed and there will be a retry of the failed job execution. <br>
     * <i>If needed, this method can be overwritten to react on a retry job execution.</i>
     * 
     * @param failedExecutionId ID of current job execution that has failed
     * @param retryExecutionId ID of new job execution that that will retry the failed one
     */
    public void onRetry(Long failedExecutionId, Long retryExecutionId) {}

    /**
     * The job engine will call this callback method after the job execution has failed. <br>
     * <i>If needed, this method can be overwritten to react on a failed job execution.</i>
     * 
     * @param jobExecutionId ID of current job execution that has failed
     */
    public void onFailed(Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after a batch has failed. <br>
     * <i>If needed, this method can be overwritten to react on a failed batch.</i>
     * 
     * @param batchId chain ID
     * @param jobExecutionId ID of last job execution of a batch that has failed
     */
    public void onFailedBatch(Long batchId, Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after a chain has failed. <br>
     * <i>If needed, this method can be overwritten to react on a failed chain.</i>
     * 
     * @param chainId chain ID
     * @param jobExecutionId ID of last job execution of a chain that has failed
     */
    public void onFailedChain(Long chainId, Long jobExecutionId) {}

    /**
     * The job engine will call this callback method after the job went into status ERROR <br>
     * <i>If needed, this method can be overwritten to react on an error that will stop this worker.</i>
     * 
     * @param throwable Error triggering exception
     */
    public void onJobError(Throwable throwable) {}

    /**
     * This is just the listener for jobs going into status error. Use {@link #onJobError(Throwable)} to react on errors of this worker!
     * 
     * @param event any JobErrorEvent
     */
    @Asynchronous
    public void onJobError(@Observes JobErrorEvent event) {
        // is that me?!
        if (getJobId() == event.getJob().getId()) {
            // oh fuck, better refresh the job in this worker
            job = event.getJob();
            // trigger callback method
            onJobError(event.getThrowable());
        }
    }

    /**
     * The job engine will call this callback method after all job executions in the queue are done<br>
     * <i>If needed, this method can be overwritten to react on this event.</i>
     */
    public void onAllJobExecutionsDone() {}

    /**
     * This is just the listener for a depleted job execution queue. Use {@link #onAllJobExecutionsDone()} to react on it!
     * 
     * @param event any AllJobExecutionsDoneEvent
     */
    @Asynchronous
    public void onAllJobExecutionsDone(@Observes AllJobExecutionsDoneEvent event) {
        // is that me?!
        if (getJobId() == event.getJob().getId()) {
            // trigger callback method
            onAllJobExecutionsDone();
        }
    }

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
        if (getJob() == null) { // loads job if needed
            return null;
        }
        return job.getId();
    }

    /**
     * Logs a text message directly to the job
     * 
     * @param message text to log
     * @return the resulting log entry
     */
    public Log logOnJob(String message) {
        return jobEngineLogService.logMessageInNewTransaction(message, getJobId(), false);
    }

    /**
     * Logs a text message to the general workhorse log
     * 
     * @param message text to log
     * @return the resulting log entry
     */
    public Log logGlobally(String message) {
        return jobEngineLogService.logMessageInNewTransaction(message, null, false);
    }

    /**
     * Adds the message text in as a new line to the executions log <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param message text to log
     */
    protected void logLine(String message) {
        jobContext.logLine(message);
    }

    /**
     * Adds a timestamp followed by the message text in as a new line to the executions log <br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Example: <code>[22:06:42.680] Step 3 complete</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param message text to log
     */
    protected void logLineWithTimestamp(String message) {
        jobContext.logLineWithTimestamp(message);
    }

    /**
     * Adds a timestamp followed by an info marker and the info message text in as a new line to the executions log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Info marker: Only if defined in {@link JobEngineConfig#LOG_INFO_MARKER}<br>
     * Example: <code>[22:06:42.680] Step 3 complete</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param message text to log
     */
    protected void logInfo(String message) {
        jobContext.logInfo(message);
    }

    /**
     * Adds a timestamp followed by an info marker and the info message text in as a new line to the executions log and also adds the message in severity INFO
     * to the server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Info marker: Only if defined in {@link JobEngineConfig#LOG_INFO_MARKER}<br>
     * Example: <code>[22:06:42.680] Step 3 complete</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param logger server log logger
     * @param message text to log
     */
    protected void logInfo(Logger logger, String message) {
        jobContext.logInfo(logger, message);
    }

    /**
     * Adds a timestamp followed by an warn marker and the warn message as a new line to the executions log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Error marker: <code>[WARN]</code> or as defined in {@link JobEngineConfig#LOG_WARN_MARKER}<br>
     * Example: <code>[22:06:42.680] [WARN] Well thats suspicious...</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param message text to log
     */
    protected void logWarn(String message) {
        jobContext.logWarn(message);
    }

    /**
     * Adds a timestamp followed by an warn marker and the warn message as a new line to the executions log. It also adds the message in severity WARN to the
     * server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Error marker: <code>[WARN]</code> or as defined in {@link JobEngineConfig#LOG_WARN_MARKER}<br>
     * Example: <code>[22:06:42.680] [WARN] Well thats suspicious...</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param logger server log logger
     * @param message text to log
     */
    protected void logWarn(Logger logger, String message) {
        jobContext.logWarn(logger, message);
    }

    /**
     * Adds a timestamp followed by an error marker and the error message as a new line to the executions log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Error marker: <code>[ERROR]</code> or as defined in {@link JobEngineConfig#LOG_ERROR_MARKER}<br>
     * Example: <code>[22:06:42.680] [ERROR] Dafuq was that?!?!</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param message text to log
     */
    protected void logError(String message) {
        jobContext.logError(message);
    }

    /**
     * Adds a timestamp followed by an error marker and the error message as a new line to the executions log. It also adds the message in severity ERROR to the
     * server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Error marker: <code>[ERROR]</code> or as defined in {@link JobEngineConfig#LOG_ERROR_MARKER}<br>
     * Example: <code>[22:06:42.680] [ERROR] Dafuq was that?!?!</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param logger server log logger
     * @param message text to log
     */
    protected void logError(Logger logger, String message) {
        jobContext.logError(logger, message);
    }

    /**
     * Adds a timestamp followed by an error marker and the error message as a new line to the executions log. It also adds the message in severity ERROR and
     * the throwable to the server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code> or as defined in {@link JobEngineConfig#LOG_TIME_FORMATTER}<br>
     * Error marker: <code>[ERROR]</code> or as defined in {@link JobEngineConfig#LOG_ERROR_MARKER}<br>
     * Example: <code>[22:06:42.680] [ERROR] Dafuq was that?!?!</code> <br>
     * <i>CAUTION: This will only work in the context of the doWork method!</i>
     * 
     * @param logger server log logger
     * @param message text to log
     * @param throwable cause of error
     */
    protected void logError(Logger logger, String message, Throwable throwable) {
        jobContext.logError(logger, message, throwable);
    }

    /**
     * @return the log text of the current running job execution or <code>null</code> if there isn't any
     */
    public String getJobExecutionLog() {
        return jobContext.getLog();
    }

    /**
     * @return the context of the current running job execution
     */
    public JobContext getJobContext() {
        return jobContext;
    }

    /**
     * <i>Convenience method to create a job execution</i><br>
     * <br>
     * This creates a parameterless {@link JobExecution} object that gets added to the job engine with default options.
     * 
     * @return job execution ID
     */
    public Long createJobExecution() {
        return create(null, null, null, null, null, null).getId();
    }

    protected JobExecution create(Object parameters, Boolean priority, LocalDateTime maturity, Long batchId, Long chainId, Long chainPreviousExecutionId) {

        Long jobId = getJob().getId();
        boolean uniqueInQueue = getJob().isUniqueInQueue();

        String parametersJson = JobEngineUtil.parametersToJson(parameters);

        return jobEngineService.createJobExecution(jobId, parametersJson, priority, maturity, batchId, chainId, chainPreviousExecutionId, uniqueInQueue);
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
