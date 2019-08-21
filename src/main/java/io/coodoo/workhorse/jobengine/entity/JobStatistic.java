package io.coodoo.workhorse.jobengine.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import io.coodoo.framework.jpa.boundary.entity.RevisionDatesEntity;

/**
 * Record of the jobs activity
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_statistic")
@NamedQueries({

                @NamedQuery(name = "JobStatistic.deleteAllByJobId", query = "DELETE FROM JobStatistic j WHERE j.jobId = :jobId")

})
public class JobStatistic extends RevisionDatesEntity {

    private static final long serialVersionUID = 1L;

    /**
     * The reference to the job
     */
    @Column(name = "job_id")
    private Long jobId;

    /**
     * Average duration
     */
    @Column(name = "duration_avg")
    private Long durationAvg;

    /**
     * Duration median
     */
    @Column(name = "duration_median")
    private Long durationMedian;

    /**
     * Amount of queued job executions
     */
    @Column(name = "queued")
    private Integer queued;

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

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getDurationAvg() {
        return durationAvg;
    }

    public void setDurationAvg(Long durationAvg) {
        this.durationAvg = durationAvg;
    }

    public Long getDurationMedian() {
        return durationMedian;
    }

    public void setDurationMedian(Long durationMedian) {
        this.durationMedian = durationMedian;
    }

    public Integer getQueued() {
        return queued;
    }

    public void setQueued(Integer queued) {
        this.queued = queued;
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
        builder.append("JobStatistic [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", updatedAt=");
        builder.append(updatedAt);
        builder.append(", jobId=");
        builder.append(jobId);
        builder.append(", durationAvg=");
        builder.append(durationAvg);
        builder.append(", durationMedian=");
        builder.append(durationMedian);
        builder.append(", queued=");
        builder.append(queued);
        builder.append(", finished=");
        builder.append(finished);
        builder.append(", failed=");
        builder.append(failed);
        builder.append(", schedule=");
        builder.append(schedule);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Executes the query 'JobStatistic.deleteAllByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return Number of deleted objects
     */
    public static int deleteAllByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatistic.deleteAllByJobId");
        query = query.setParameter("jobId", jobId);
        return query.executeUpdate();
    }

}
