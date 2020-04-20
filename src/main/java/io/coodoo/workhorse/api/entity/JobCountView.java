package io.coodoo.workhorse.api.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import io.coodoo.framework.jpa.entity.AbstractIdOccCreatedUpdatedAtEntity;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.StringListConverter;

@SuppressWarnings("serial")
@Entity
@Table(name = "jobengine_job_count_view")
public class JobCountView extends AbstractIdOccCreatedUpdatedAtEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    @Convert(converter = StringListConverter.class)
    private List<String> tags = new ArrayList<>();

    @Column(name = "worker_class_name")
    private String workerClassName;

    @Column(name = "parameters_class_name")
    private String parametersClassName;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "threads")
    private int threads;

    @Column(name = "max_per_minute")
    private Integer maxPerMinute;

    @Column(name = "fail_retries")
    private int failRetries;

    @Column(name = "retry_delay")
    private int retryDelay;

    @Column(name = "days_until_clean_up")
    private int daysUntilCleanUp;

    @Column(name = "unique_in_queue")
    private boolean uniqueInQueue;

    @Column(name = "total")
    private int total;

    @Column(name = "queued")
    private Integer queued;

    @Column(name = "running")
    private Integer running;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getWorkerClassName() {
        return workerClassName;
    }

    public void setWorkerClassName(String workerClassName) {
        this.workerClassName = workerClassName;
    }

    public String getParametersClassName() {
        return parametersClassName;
    }

    public void setParametersClassName(String parametersClassName) {
        this.parametersClassName = parametersClassName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public Integer getMaxPerMinute() {
        return maxPerMinute;
    }

    public void setMaxPerMinute(Integer maxPerMinute) {
        this.maxPerMinute = maxPerMinute;
    }

    public int getFailRetries() {
        return failRetries;
    }

    public void setFailRetries(int failRetries) {
        this.failRetries = failRetries;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public int getDaysUntilCleanUp() {
        return daysUntilCleanUp;
    }

    public void setDaysUntilCleanUp(int daysUntilCleanUp) {
        this.daysUntilCleanUp = daysUntilCleanUp;
    }

    public boolean isUniqueInQueue() {
        return uniqueInQueue;
    }

    public void setUniqueInQueue(boolean uniqueInQueue) {
        this.uniqueInQueue = uniqueInQueue;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Integer getQueued() {
        return queued;
    }

    public void setQueued(Integer queued) {
        this.queued = queued;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

}
