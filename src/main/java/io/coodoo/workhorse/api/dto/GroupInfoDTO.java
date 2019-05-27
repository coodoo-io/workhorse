package io.coodoo.workhorse.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.coodoo.workhorse.jobengine.entity.GroupInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class GroupInfoDTO {

    public Long id;
    public JobExecutionStatus status;
    public int size;
    public int queued;
    public int running;
    public int finished;
    public int failed;
    public int aborted;
    public LocalDateTime startedAt;
    public LocalDateTime endedAt;
    public int progress;
    public Long duration;
    public LocalDateTime expectedEnd;
    public Long expectedDuration;
    public List<JobExecutionInfoDTO> executionInfos;

    public GroupInfoDTO() {}

    public GroupInfoDTO(GroupInfo groupInfo) {
        super();
        this.id = groupInfo.getId();
        this.status = groupInfo.getStatus();
        this.size = groupInfo.getSize();
        this.queued = groupInfo.getQueued();
        this.running = groupInfo.getRunning();
        this.finished = groupInfo.getFinished();
        this.failed = groupInfo.getFailed();
        this.aborted = groupInfo.getAborted();
        this.startedAt = groupInfo.getStartedAt();
        this.endedAt = groupInfo.getEndedAt();
        this.progress = groupInfo.getProgress();
        this.duration = groupInfo.getDuration();
        this.expectedEnd = groupInfo.getExpectedEnd();
        this.expectedDuration = groupInfo.getExpectedDuration();
        this.executionInfos = groupInfo.getExecutionInfos().stream().map(JobExecutionInfoDTO::new).collect(Collectors.toList());
    }

}
