package io.coodoo.workhorse.api.dto;

import java.time.LocalDateTime;

import io.coodoo.framework.jpa.boundary.entity.dto.RevisionDatesEntityDTO;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

public class JobExecutionDTO extends RevisionDatesEntityDTO {

    public Long jobId;
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
    public String log;
    public int failRetry;
    public Long failRetryExecutionId;
    public String failMessage;
    public String failStacktrace;

    public JobExecutionDTO() {}

    public JobExecutionDTO(JobExecution jobExecution) {
        super(jobExecution);
        this.jobId = jobExecution.getJobId();
        this.status = jobExecution.getStatus();
        this.startedAt = jobExecution.getStartedAt();
        this.endedAt = jobExecution.getEndedAt();
        this.priority = jobExecution.isPriority();
        this.maturity = jobExecution.getMaturity();
        this.batchId = jobExecution.getBatchId();
        this.chainId = jobExecution.getChainId();
        this.chainPreviousExecutionId = jobExecution.getChainPreviousExecutionId();
        this.duration = jobExecution.getDuration();
        this.parameters = jobExecution.getParameters();
        this.log = jobExecution.getLog();
        this.failRetry = jobExecution.getFailRetry();
        this.failRetryExecutionId = jobExecution.getFailRetryExecutionId();
        this.failMessage = jobExecution.getFailMessage();
        this.failStacktrace = jobExecution.getFailStacktrace();
    }

}
