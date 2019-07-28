package io.coodoo.workhorse.api.dto;

import java.util.ArrayList;
import java.util.List;

import io.coodoo.framework.jpa.boundary.entity.dto.RevisionDatesOccEntityDTO;
import io.coodoo.workhorse.jobengine.entity.JobCountView;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

public class JobCountViewDTO extends RevisionDatesOccEntityDTO {

    public String name;
    public String description;
    public List<String> tags = new ArrayList<>();
    public String workerClassName;
    public String schedule;
    public JobStatus status;
    public int threads;
    public Integer maxPerMinute;
    public int failRetries;
    public int retryDelay;
    public int daysUntilCleanUp;
    public boolean uniqueInQueue;

    public int total;
    public Integer queued;
    public Integer running;

    public JobCountViewDTO() {}

    public JobCountViewDTO(JobCountView jobCountView) {
        super(jobCountView);
        this.name = jobCountView.getName();
        this.description = jobCountView.getDescription();
        this.tags = jobCountView.getTags();
        this.workerClassName = jobCountView.getWorkerClassName();
        this.schedule = jobCountView.getSchedule();
        this.status = jobCountView.getStatus();
        this.threads = jobCountView.getThreads();
        this.maxPerMinute = jobCountView.getMaxPerMinute();
        this.failRetries = jobCountView.getFailRetries();
        this.retryDelay = jobCountView.getRetryDelay();
        this.daysUntilCleanUp = jobCountView.getDaysUntilCleanUp();
        this.uniqueInQueue = jobCountView.isUniqueInQueue();

        this.total = jobCountView.getTotal();
        this.queued = jobCountView.getQueued();
        this.running = jobCountView.getRunning();
    }

}
