package io.coodoo.workhorse.jobengine.boundary;

import javax.enterprise.context.RequestScoped;

import org.slf4j.Logger;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@RequestScoped
public class JobContext {

    protected Job job;

    protected JobExecution jobExecution;

    private StringBuffer logBuffer;

    public void init(JobExecution jobExecution) {

        this.jobExecution = jobExecution;
        this.logBuffer = jobExecution.getLog() == null ? new StringBuffer() : new StringBuffer(jobExecution.getLog());
    }

    public JobExecution getJobExecution() {
        return jobExecution;
    }

    public Long getJobId() {
        return jobExecution.getJobId();
    }

    public Long getJobExecutionId() {
        return jobExecution.getId();
    }

    public String getLog() {

        if (logBuffer != null && logBuffer.length() > 0) {
            return logBuffer.toString();
        }
        return null;
    }

    /**
     * Adds the message text in as a new line to the executions log
     * 
     * @param message text to log
     */
    public void logLine(String message) {
        appendLog(message, false, false);
    }

    /**
     * Adds a timestamp followed by the message text in as a new line to the executions log <br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code><br>
     * Example: <code>[22:06:42.680] Step 3 complete</code>
     * 
     * @param message text to log
     */
    public void logLineWithTimestamp(String message) {
        appendLog(message, true, false);
    }

    /**
     * Adds a timestamp followed by the message text in as a new line to the executions log and also adds the message in severity INFO to the server log<br>
     * Timestamp pattern: <code>[HH:mm:ss.SSS]</code><br>
     * Example: <code>[22:06:42.680] Step 3 complete</code>
     * 
     * @param logger server log logger
     * @param message text to log
     */
    public void logInfo(Logger logger, String message) {
        logger.info(message);
        appendLog(message, true, false);
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
    public void logError(Logger logger, String message) {
        logger.error(message);
        appendLog(message, true, true);
    }

    private void appendLog(String message, boolean timestamp, boolean error) {

        if (logBuffer.length() > 0) {
            logBuffer.append(System.lineSeparator());
        }
        if (timestamp) {
            logBuffer.append("[");
            logBuffer.append(JobEngineUtil.timestamp().toLocalTime());
            logBuffer.append("] ");
        }
        if (error) {
            logBuffer.append("[ERROR] ");
        }
        logBuffer.append(message);
    }

}
