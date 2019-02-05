package io.coodoo.workhorse.jobengine.entity;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobExecutionInfo {

    private Long id;

    private JobExecutionStatus status;

    private Long duration;

    private Long failRetryExecutionId;

    public JobExecutionInfo() {}

    public JobExecutionInfo(Long id, JobExecutionStatus status, Long duration, Long failRetryExecutionId) {
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.failRetryExecutionId = failRetryExecutionId;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getFailRetryExecutionId() {
        return failRetryExecutionId;
    }

    public void setFailRetryExecutionId(Long failRetryExecutionId) {
        this.failRetryExecutionId = failRetryExecutionId;
    }

    @Override
    public String toString() {
        return "JobExecutionInfo [id=" + id + ", status=" + status + ", duration=" + duration + ", failRetryExecutionId=" + failRetryExecutionId + "]";
    }

}
