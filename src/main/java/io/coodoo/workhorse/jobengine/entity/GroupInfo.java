package io.coodoo.workhorse.jobengine.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class GroupInfo {

    private Long id;

    private JobExecutionStatus status;

    private int size;

    private int queued;

    private int running;

    private int finished;

    private int failed;

    private int aborted;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private int progress;

    private Long duration;

    private LocalDateTime expectedEnd;

    private Long expectedDuration;

    private List<JobExecutionInfo> executionInfos;

    public GroupInfo() {}

    public GroupInfo(Long id, List<JobExecutionInfo> executionInfos) {

        this.id = id;
        size = executionInfos.size();
        queued = 0;
        running = 0;
        finished = 0;
        failed = 0;
        aborted = 0;
        startedAt = null;
        endedAt = null;
        this.executionInfos = executionInfos;
        this.duration = 0L;

        for (JobExecutionInfo execution : executionInfos) {
            switch (execution.getStatus()) {
                case QUEUED:
                    queued++;
                    break;
                case RUNNING:
                    if (execution.getStartedAt() != null && (startedAt == null || execution.getStartedAt().isBefore(startedAt))) {
                        startedAt = execution.getStartedAt();
                    }
                    running++;
                    break;
                case FINISHED:
                    if (execution.getStartedAt() != null && (startedAt == null || execution.getStartedAt().isBefore(startedAt))) {
                        startedAt = execution.getStartedAt();
                    }
                    if (execution.getEndedAt() != null && (endedAt == null || execution.getEndedAt().isAfter(endedAt))) {
                        endedAt = execution.getEndedAt();
                    }
                    finished++;
                    duration += execution.getDuration();
                    break;
                case FAILED:
                    if (execution.getStartedAt() != null && (startedAt == null || execution.getStartedAt().isBefore(startedAt))) {
                        startedAt = execution.getStartedAt();
                    }
                    if (execution.getEndedAt() != null && (endedAt == null || execution.getEndedAt().isAfter(endedAt))) {
                        endedAt = execution.getEndedAt();
                    }
                    failed++;
                    duration += execution.getDuration();
                    break;
                case ABORTED:
                    if (execution.getStartedAt() != null && (startedAt == null || execution.getStartedAt().isBefore(startedAt))) {
                        startedAt = execution.getStartedAt();
                    }
                    aborted++;
                    if (execution.getDuration() != null) {
                        duration += execution.getDuration();
                    }
                    break;
            }
        }

        if (size > 0) {
            int doneCount = finished + failed + aborted;
            progress = (int) ((doneCount * 100.0f) / size);
            if (progress > 0 && progress < 100) {
                expectedDuration = (duration / doneCount) * size;
                if (endedAt != null) {
                    int togoCount = size - doneCount;
                    if (togoCount > 0) {
                        expectedEnd = endedAt.plusSeconds(((duration / doneCount) * togoCount) / 1000);
                    }
                }
            }
            if (queued == size) {
                status = JobExecutionStatus.QUEUED;
            } else if (doneCount == size) {
                status = JobExecutionStatus.FINISHED;
                if (aborted > 0) {
                    // when a chain execution fails, the remaining queued executions get aborted // FIXME: also a batch will be in state ABORTED...
                    status = JobExecutionStatus.ABORTED;
                }
            } else if (queued < size) {
                status = JobExecutionStatus.RUNNING;
                endedAt = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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

    public List<JobExecutionInfo> getExecutionInfos() {
        return executionInfos;
    }

    public void setExecutionInfos(List<JobExecutionInfo> executionInfos) {
        this.executionInfos = executionInfos;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        StringBuilder builder = new StringBuilder();
        builder.append("GroupInfo [id=");
        builder.append(id);
        builder.append(", status=");
        builder.append(status);
        builder.append(", size=");
        builder.append(size);
        builder.append(", queued=");
        builder.append(queued);
        builder.append(", running=");
        builder.append(running);
        builder.append(", finished=");
        builder.append(finished);
        builder.append(", failed=");
        builder.append(failed);
        builder.append(", aborted=");
        builder.append(aborted);
        builder.append(", startedAt=");
        builder.append(startedAt);
        builder.append(", endedAt=");
        builder.append(endedAt);
        builder.append(", progress=");
        builder.append(progress);
        builder.append(", duration=");
        builder.append(duration);
        builder.append(", expectedEnd=");
        builder.append(expectedEnd);
        builder.append(", expectedDuration=");
        builder.append(expectedDuration);
        builder.append(", executionInfos=");
        builder.append(executionInfos != null ? executionInfos.subList(0, Math.min(executionInfos.size(), maxLen)) : null);
        builder.append("]");
        return builder.toString();
    }

}
