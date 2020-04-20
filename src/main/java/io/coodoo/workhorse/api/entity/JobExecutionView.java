package io.coodoo.workhorse.api.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import io.coodoo.framework.jpa.entity.AbstractIdCreatedUpdatedAtEntity;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

/**
 * <p>
 * A JobExceuctionView defines a single execution which is joined with job information.
 * </p>
 * <p>
 * Every needed information of an execution is stored with this entity.
 * </p>
 * 
 * @author coodoo GmbH (coodoo.io)
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "jobengine_execution_view")
public class JobExecutionView extends AbstractIdCreatedUpdatedAtEntity {

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "job_status")
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    @Column(name = "job_fail_retries")
    private int jobFailRetries;

    @Column(name = "job_threads")
    private int jobThreads;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "priority")
    private boolean priority;

    @Column(name = "maturity")
    private LocalDateTime maturity;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "chain_id")
    private Long chainId;

    @Column(name = "chain_previous_execution_id")
    private Long chainPreviousExecutionId;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "parameters")
    private String parameters;

    @Column(name = "fail_retry")
    private int failRetry;

    @Column(name = "fail_retry_execution_id")
    private Long failRetryExecutionId;

    @Column(name = "fail_message")
    private String failMessage;

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getJobFailRetries() {
        return jobFailRetries;
    }

    public void setJobFailRetries(int jobFailRetries) {
        this.jobFailRetries = jobFailRetries;
    }

    public int getJobThreads() {
        return jobThreads;
    }

    public void setJobThreads(int jobThreads) {
        this.jobThreads = jobThreads;
    }

    public JobExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(JobExecutionStatus status) {
        this.status = status;
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

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public LocalDateTime getMaturity() {
        return maturity;
    }

    public void setMaturity(LocalDateTime maturity) {
        this.maturity = maturity;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public Long getChainPreviousExecutionId() {
        return chainPreviousExecutionId;
    }

    public void setChainPreviousExecutionId(Long chainPreviousExecutionId) {
        this.chainPreviousExecutionId = chainPreviousExecutionId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getFailRetry() {
        return failRetry;
    }

    public void setFailRetry(int failRetry) {
        this.failRetry = failRetry;
    }

    public Long getFailRetryExecutionId() {
        return failRetryExecutionId;
    }

    public void setFailRetryExecutionId(Long failRetryExecutionId) {
        this.failRetryExecutionId = failRetryExecutionId;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    @Override
    public String toString() {
        return String.format("Job [name=%1$s, description=%2$s]", jobName, jobDescription);
    }

}
