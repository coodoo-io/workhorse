package io.coodoo.workhorse.statistic.entity;

import javax.persistence.Column;

import io.coodoo.workhorse.statistic.boundary.StatisticsUtil;

/**
 * Summary job activities
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public class JobStatisticSummary {

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

    public JobStatisticSummary(Long durationCount, Long durationSum, Long durationMax, Long durationMin, Double durationAvg, Long finished, Long failed,
                    Long schedule) {

        this.durationCount = durationCount == null ? 0 : new Long(durationCount).intValue();
        this.durationSum = durationSum;
        this.durationMax = durationMax;
        this.durationMin = durationMin;
        this.durationAvg = StatisticsUtil.doubleToLong(durationAvg);
        this.finished = finished == null ? 0 : new Long(finished).intValue();
        this.failed = failed == null ? 0 : new Long(failed).intValue();
        this.schedule = schedule == null ? 0 : new Long(schedule).intValue();
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
        builder.append("JobStatisticSummary [durationCount=");
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
