package io.coodoo.workhorse.api.dto;

import java.time.LocalDateTime;

import io.coodoo.framework.jpa.boundary.entity.dto.RevisionDatesEntityDTO;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobExecutionView;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.JobType;

public class JobExecutionViewDTO extends RevisionDatesEntityDTO {

    public Long id;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long jobId;
    public String jobName;
    public String jobDescription;
    public JobType jobType;
    public JobStatus jobStatus;
    public int jobThreads;
    public int jobMaxFails;
    public JobExecutionStatus status;
    public LocalDateTime startedAt;
    public LocalDateTime endedAt;
    public boolean priority;
    public LocalDateTime maturity;
    public Long batchId;
    public Long chainId;
    public Long chainPreviousExecutionId;
    public Long duration;
    public String parameters;
    public int failRetry;
    public Long failRetryExecutionId;
    public String failMessage;

    public JobExecutionViewDTO(JobExecutionView jobExecutionView) {
        super(jobExecutionView);
        this.id = jobExecutionView.getId();
        this.createdAt = jobExecutionView.getCreatedAt();
        this.updatedAt = jobExecutionView.getUpdatedAt();
        this.jobId = jobExecutionView.getJobId();
        this.jobName = jobExecutionView.getJobName();
        this.jobDescription = jobExecutionView.getJobDescription();
        this.jobType = jobExecutionView.getJobType();
        this.jobStatus = jobExecutionView.getJobStatus();
        this.jobThreads = jobExecutionView.getJobThreads();
        this.jobMaxFails = jobExecutionView.getJobFailRetries();
        this.status = jobExecutionView.getStatus();
        this.startedAt = jobExecutionView.getStartedAt();
        this.endedAt = jobExecutionView.getEndedAt();
        this.priority = jobExecutionView.isPriority();
        this.maturity = jobExecutionView.getMaturity();
        this.batchId = jobExecutionView.getBatchId();
        this.chainId = jobExecutionView.getChainId();
        this.chainPreviousExecutionId = jobExecutionView.getChainPreviousExecutionId();
        this.duration = jobExecutionView.getDuration();
        this.parameters = jobExecutionView.getParameters();
        this.failRetry = jobExecutionView.getFailRetry();
        this.failRetryExecutionId = jobExecutionView.getFailRetryExecutionId();
        this.failMessage = jobExecutionView.getFailMessage();
    }
}
