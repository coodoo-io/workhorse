package io.coodoo.workhorse.api.dto;

import java.time.LocalDateTime;

import io.coodoo.workhorse.jobengine.entity.JobExecutionInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobExecutionInfoDTO {

    public Long id;
    public JobExecutionStatus status;
    public Long duration;
    public LocalDateTime startedAt;
    public LocalDateTime endedAt;
    public Long failRetryExecutionId;

    public JobExecutionInfoDTO() {}

    public JobExecutionInfoDTO(JobExecutionInfo jobExecutionInfo) {
        this.id = jobExecutionInfo.getId();
        this.status = jobExecutionInfo.getStatus();
        this.duration = jobExecutionInfo.getDuration();
        this.startedAt = jobExecutionInfo.getStartedAt();
        this.endedAt = jobExecutionInfo.getEndedAt();
        this.failRetryExecutionId = jobExecutionInfo.getFailRetryExecutionId();
    }

}
