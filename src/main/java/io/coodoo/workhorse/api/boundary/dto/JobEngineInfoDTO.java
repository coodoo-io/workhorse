package io.coodoo.workhorse.api.boundary.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.coodoo.workhorse.jobengine.entity.JobEngineInfo;

public class JobEngineInfoDTO {

    public Long jobId;
    public int queuedExecutions;
    public int queuedPriorityExecutions;
    public List<JobExecutionDTO> runningExecutions = new ArrayList<>();
    public int threadCount;
    public LocalDateTime threadStartTime;
    public boolean paused;

    public JobEngineInfoDTO() {}

    public JobEngineInfoDTO(JobEngineInfo jobEngineInfo) {
        this.jobId = jobEngineInfo.getJobId();
        this.queuedExecutions = jobEngineInfo.getQueuedExecutions();
        this.queuedPriorityExecutions = jobEngineInfo.getQueuedPriorityExecutions();
        if (jobEngineInfo.getRunningExecutions() != null) {
            this.runningExecutions = jobEngineInfo.getRunningExecutions().stream().map(JobExecutionDTO::new).collect(Collectors.toList());
        }
        this.threadCount = jobEngineInfo.getThreadCount();
        this.threadStartTime = jobEngineInfo.getThreadStartTime();
        this.paused = jobEngineInfo.isPaused();
    }

}
