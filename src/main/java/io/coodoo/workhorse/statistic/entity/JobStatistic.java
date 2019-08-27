package io.coodoo.workhorse.statistic.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import io.coodoo.framework.jpa.boundary.entity.RevisionDatesEntity;

/**
 * Basic record attributes of job activities
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@MappedSuperclass
public abstract class JobStatistic extends RevisionDatesEntity {

    private static final long serialVersionUID = 1L;

    /**
     * The reference to the job
     */
    @Column(name = "job_id")
    private Long jobId;

    /**
     * Timestamp of recordings beginning
     */
    @Column(name = "recorded_from")
    private LocalDateTime from;

    /**
     * Timestamp of recordings ending
     */
    @Column(name = "recorded_to")
    private LocalDateTime to;

    /**
     * Durations count
     */
    @Column(name = "duration_count")
    private Integer durationCount;

    /**
     * Sum of all durations
     */
    @Column(name = "duration_sum")
    private Long durationSum;

    /**
     * Maximal duration
     */
    @Column(name = "duration_max")
    private Long durationMax;

    /**
     * Minimal duration
     */
    @Column(name = "duration_min")
    private Long durationMin;

    /**
     * Average duration
     */
    @Column(name = "duration_avg")
    private Long durationAvg;

    /**
     * Amount of finished job executions
     */
    @Column(name = "finished")
    private Integer finished;

    /**
     * Amount of failed job executions
     */
    @Column(name = "failed")
    private Integer failed;

    /**
     * Amount of schedule triggers
     */
    @Column(name = "schedule")
    private Integer schedule;

    public JobStatistic() {}

    public JobStatistic(JobStatistic jobStatistic, LocalDateTime from, LocalDateTime to) {
        this.jobId = jobStatistic.getJobId();
        this.from = from;
        this.to = to;
        this.durationCount = jobStatistic.getDurationCount();
        this.durationSum = jobStatistic.getDurationSum();
        this.durationMax = jobStatistic.getDurationMax();
        this.durationMin = jobStatistic.getDurationMin();
        this.durationAvg = jobStatistic.getDurationAvg();
        this.finished = jobStatistic.getFinished();
        this.failed = jobStatistic.getFailed();
        this.schedule = jobStatistic.getSchedule();
    }

    public void update(JobStatisticSummary summary) {
        durationCount = summary.getDurationCount();
        durationSum = summary.getDurationSum();
        durationMax = summary.getDurationMax();
        durationMin = summary.getDurationMin();
        durationAvg = summary.getDurationAvg();
        finished = summary.getFinished();
        failed = summary.getFailed();
        schedule = summary.getSchedule();
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public Integer getDurationCount() {
        return durationCount;
    }

    public void setDurationCount(Integer durationCount) {
        this.durationCount = durationCount;
    }

    public Long getDurationSum() {
        return durationSum;
    }

    public void setDurationSum(Long durationSum) {
        this.durationSum = durationSum;
    }

    public Long getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(Long durationMax) {
        this.durationMax = durationMax;
    }

    public Long getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Long durationMin) {
        this.durationMin = durationMin;
    }

    public Long getDurationAvg() {
        return durationAvg;
    }

    public void setDurationAvg(Long durationAvg) {
        this.durationAvg = durationAvg;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", updatedAt=");
        builder.append(updatedAt);
        builder.append(", jobId=");
        builder.append(jobId);
        builder.append(", from=");
        builder.append(from);
        builder.append(", to=");
        builder.append(to);
        builder.append(", durationCount=");
        builder.append(durationCount);
        builder.append(", durationSum=");
        builder.append(durationSum);
        builder.append(", durationMax=");
        builder.append(durationMax);
        builder.append(", durationMin=");
        builder.append(durationMin);
        builder.append(", durationAvg=");
        builder.append(durationAvg);
        builder.append(", finished=");
        builder.append(finished);
        builder.append(", failed=");
        builder.append(failed);
        builder.append(", schedule=");
        builder.append(schedule);
        builder.append("]");
        return builder.toString();
    }

}
