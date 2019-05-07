package io.coodoo.workhorse.jobengine.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobEngineInfo {

    private Long jobId;
    private int queuedExecutions = 0;
    private int queuedPriorityExecutions = 0;
    private List<JobExecution> runningExecutions = new ArrayList<>();
    private int threadCount = 0;
    private LocalDateTime threadStartTime;
    private boolean paused;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public int getQueuedExecutions() {
        return queuedExecutions;
    }

    public void setQueuedExecutions(int queuedExecutions) {
        this.queuedExecutions = queuedExecutions;
    }

    public int getQueuedPriorityExecutions() {
        return queuedPriorityExecutions;
    }

    public void setQueuedPriorityExecutions(int queuedPriorityExecutions) {
        this.queuedPriorityExecutions = queuedPriorityExecutions;
    }

    public List<JobExecution> getRunningExecutions() {
        return runningExecutions;
    }

    public void setRunningExecutions(List<JobExecution> runningExecutions) {
        this.runningExecutions = runningExecutions;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public LocalDateTime getThreadStartTime() {
        return threadStartTime;
    }

    public void setThreadStartTime(LocalDateTime threadStartTime) {
        this.threadStartTime = threadStartTime;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

}
