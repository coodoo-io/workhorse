package io.coodoo.workhorse.api.dto;

import io.coodoo.workhorse.jobengine.entity.JobExecutionCounts;

public class JobExecutionCountsDTO {

    public Long total;
    public Long queued;
    public Long running;
    public Long finished;
    public Long failed;
    public Long aborted;
    public Long averageDuration;

    public JobExecutionCountsDTO() {}

    public JobExecutionCountsDTO(JobExecutionCounts counts) {
        this.total = counts.getTotal();
        this.queued = counts.getQueued();
        this.running = counts.getRunning();
        this.finished = counts.getFinished();
        this.failed = counts.getFailed();
        this.aborted = counts.getAborted();
        this.averageDuration = counts.getAverageDuration();
    }
}
