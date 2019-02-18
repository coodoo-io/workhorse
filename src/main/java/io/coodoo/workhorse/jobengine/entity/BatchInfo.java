package io.coodoo.workhorse.jobengine.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class BatchInfo {

    private Long batchId;

    private JobExecutionStatus status;

    private int size;

    private int queued;

    private int running;

    private int finished;

    private int failed;

    private int aborted;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private Long duration;

    private LocalDateTime expectedEnd;

    private Long expectedDuration;

    private List<JobExecutionInfo> executions;

    public BatchInfo() {}

    public BatchInfo(Long batchId, JobExecutionInfoTime batchInfoTime, List<JobExecutionInfo> executions) {

        this.batchId = batchId;
        this.size = executions.size();
        this.queued = 0;
        this.running = 0;
        this.finished = 0;
        this.failed = 0;
        this.aborted = 0;
        this.startedAt = batchInfoTime.getFirstStartedAt();
        this.endedAt = batchInfoTime.getLastEndedAt();
        this.executions = executions;
        this.duration = 0L;
        int noDurationCount = 0;

        for (JobExecutionInfo execution : executions) {
            switch (execution.getStatus()) {
                case QUEUED:
                    this.queued++;
                    noDurationCount++;
                    break;
                case RUNNING:
                    this.running++;
                    noDurationCount++;
                    break;
                case FINISHED:
                    this.finished++;
                    this.duration += execution.getDuration();
                    break;
                case FAILED:
                    this.failed++;
                    this.duration += execution.getDuration();
                    break;
                case ABORTED:
                    this.aborted++;
                    break;
            }
        }
        if (batchInfoTime.getAvgDuration() != null && this.startedAt != null && this.endedAt == null) {
            this.expectedDuration = this.duration + (long) (batchInfoTime.getAvgDuration() * noDurationCount);
            this.expectedEnd = this.startedAt.plusSeconds(this.expectedDuration / 1000);
        }
        this.status = JobExecutionStatus.QUEUED;
        if (this.running > 0) {
            this.status = JobExecutionStatus.RUNNING;
        } else if (this.queued == 0 && this.running == 0) {
            this.status = JobExecutionStatus.FINISHED;
            if (this.aborted > 0) {
                this.status = JobExecutionStatus.ABORTED;
            }
        }
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public JobExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(JobExecutionStatus status) {
        this.status = status;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getQueued() {
        return queued;
    }

    public void setQueued(int queued) {
        this.queued = queued;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getAborted() {
        return aborted;
    }

    public void setAborted(int aborted) {
        this.aborted = aborted;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getExpectedEnd() {
        return expectedEnd;
    }

    public void setExpectedEnd(LocalDateTime expectedEnd) {
        this.expectedEnd = expectedEnd;
    }

    public Long getExpectedDuration() {
        return expectedDuration;
    }

    public void setExpectedDuration(Long expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public List<JobExecutionInfo> getExecutions() {
        return executions;
    }

    public void setExecutions(List<JobExecutionInfo> executions) {
        this.executions = executions;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return "BatchInfo [batchId=" + batchId + ", status=" + status + ", size=" + size + ", queued=" + queued + ", running=" + running + ", finished="
                        + finished + ", failed=" + failed + ", aborted=" + aborted + ", startedAt=" + startedAt + ", endedAt=" + endedAt + ", duration="
                        + duration + ", expectedEnd=" + expectedEnd + ", expectedDuration=" + expectedDuration + ", executions="
                        + (executions != null ? executions.subList(0, Math.min(executions.size(), maxLen)) : null) + "]";
    }

}
