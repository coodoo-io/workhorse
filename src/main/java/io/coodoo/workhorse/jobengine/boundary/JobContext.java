package io.coodoo.workhorse.jobengine.boundary;

import javax.enterprise.context.RequestScoped;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

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

    public void logLine(String message) {
        appendLog(message, false);
    }

    public void logLineWithTimestamp(String message) {
        appendLog(message, true);
    }

    private void appendLog(String message, boolean timestamp) {

        if (logBuffer.length() > 0) {
            logBuffer.append(System.lineSeparator());
        }
        if (timestamp) {
            logBuffer.append("[");
            logBuffer.append(JobEngineUtil.timestamp().toLocalTime());
            logBuffer.append("] ");
        }
        logBuffer.append(message);
    }

    public String getLog() {

        if (logBuffer != null && logBuffer.length() > 0) {
            return logBuffer.toString();
        }
        return null;
    }

}
