package io.coodoo.workhorse.jobengine.entity;

import java.time.LocalDateTime;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobExecutionInfoTime {

    private LocalDateTime firstStartedAt;

    private LocalDateTime lastEndedAt;

    private Double avgDuration;

    public JobExecutionInfoTime() {}

    public JobExecutionInfoTime(LocalDateTime firstStartedAt, LocalDateTime lastEndedAt, Double avgDuration) {
        this.firstStartedAt = firstStartedAt;
        this.lastEndedAt = lastEndedAt;
        this.avgDuration = avgDuration;
    }

    public LocalDateTime getFirstStartedAt() {
        return firstStartedAt;
    }

    public void setFirstStartedAt(LocalDateTime firstStartedAt) {
        this.firstStartedAt = firstStartedAt;
    }

    public LocalDateTime getLastEndedAt() {
        return lastEndedAt;
    }

    public void setLastEndedAt(LocalDateTime lastEndedAt) {
        this.lastEndedAt = lastEndedAt;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Double avgDuration) {
        this.avgDuration = avgDuration;
    }

    @Override
    public String toString() {
        return "JobExecutionInfoTime [firstStartedAt=" + firstStartedAt + ", lastEndedAt=" + lastEndedAt + ", avgDuration=" + avgDuration + "]";
    }

}
