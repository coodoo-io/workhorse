package io.coodoo.workhorse.jobengine.boundary;

import javax.enterprise.context.RequestScoped;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;

@RequestScoped
public class JobExecutionLogger {

    private StringBuffer logBuffer;

    public String getLog() {

        if (logBuffer != null && logBuffer.length() > 0) {
            return logBuffer.toString();
        }
        return null;
    }

    public void setLog(String log) {

        this.logBuffer = log == null ? new StringBuffer() : new StringBuffer(log);
    }

    public void line(String message) {
        appendLog(message, false);
    }

    public void lineWithTimestamp(String message) {
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

}
