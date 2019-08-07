package io.coodoo.workhorse.api.dto;

import java.util.ArrayList;
import java.util.List;

import io.coodoo.framework.jpa.boundary.entity.dto.RevisionDatesOccEntityDTO;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

public class JobDTO extends RevisionDatesOccEntityDTO {

    public String name;
    public String description;
    public List<String> tags = new ArrayList<>();
    public String workerClassName;
    public String parametersClassName;
    public String schedule;
    public JobStatus status;
    public int threads;
    public Integer maxPerMinute;
    public int failRetries;
    public int retryDelay;
    public int daysUntilCleanUp;
    public boolean uniqueInQueue;

    public JobDTO() {}

    public JobDTO(Job job) {
        super(job);
        this.name = job.getName();
        this.description = job.getDescription();
        this.tags = job.getTags();
        this.workerClassName = job.getWorkerClassName();
        this.parametersClassName = job.getParametersClassName();
        this.schedule = job.getSchedule();
        this.status = job.getStatus();
        this.threads = job.getThreads();
        this.maxPerMinute = job.getMaxPerMinute();
        this.failRetries = job.getFailRetries();
        this.retryDelay = job.getRetryDelay();
        this.daysUntilCleanUp = job.getDaysUntilCleanUp();
        this.uniqueInQueue = job.isUniqueInQueue();
    }

}
