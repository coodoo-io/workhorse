package io.coodoo.workhorse.api.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

// FIXME: this is plain MySQL syntax!!!
@Entity
public class JobExecutionCounts {

    // @formatter:off
    private static final String QUERY = "SELECT 0 AS id,"
                                        + " COUNT(e.id) AS total,"
                                        + " IFNULL(SUM(CASE WHEN e.status = 'QUEUED'   THEN 1 ELSE 0 END),0) AS queued,"
                                        + " IFNULL(SUM(CASE WHEN e.status = 'RUNNING'  THEN 1 ELSE 0 END),0) AS running,"
                                        + " IFNULL(SUM(CASE WHEN e.status = 'FINISHED' THEN 1 ELSE 0 END),0) AS finished,"
                                        + " IFNULL(SUM(CASE WHEN e.status = 'FAILED'   THEN 1 ELSE 0 END),0) AS failed,"
                                        + " IFNULL(SUM(CASE WHEN e.status = 'ABORTED'  THEN 1 ELSE 0 END),0) AS aborted,"
                                        + " IFNULL(ROUND(AVG(e.duration),0),0) AS averageDuration"
                                      + " FROM jobengine_execution e"
                                      + " WHERE (e.job_id = :jobId OR :jobId IS NULL)"
                                        + " AND (e.created_at >= :date OR e.started_at >= :date OR e.ended_at >= :date)"
                                        + " LIMIT 1";
    // @formatter:on

    @Id // Pseudo-ID to satisfy JPA
    private Long id;
    private Long total;
    private Long queued;
    private Long running;
    private Long finished;
    private Long failed;
    private Long aborted;
    private Long averageDuration;

    /**
     * Queries the current counts of {@link JobExecution} status for one specific or all jobs
     * 
     * @param entityManager persistence
     * @param jobId optional, <code>null</code> will get the counts for all
     * @param consideredLastMinutes optional, this query is expensive, so make sure to limit the timeframe! If <code>null</code> a default of 60 minutes is
     *        given.
     * @return fresh counts!
     */
    public static JobExecutionCounts query(EntityManager entityManager, Long jobId, Integer consideredLastMinutes) {

        int minutes = consideredLastMinutes == null ? 60 : consideredLastMinutes;
        LocalDateTime date = JobEngineUtil.timestamp().minusMinutes(minutes);

        Query query = entityManager.createNativeQuery(QUERY, JobExecutionCounts.class);
        query = query.setParameter("jobId", jobId);
        query = query.setParameter("date", date);
        return (JobExecutionCounts) query.getSingleResult();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getQueued() {
        return queued;
    }

    public void setQueued(Long queued) {
        this.queued = queued;
    }

    public Long getRunning() {
        return running;
    }

    public void setRunning(Long running) {
        this.running = running;
    }

    public Long getFinished() {
        return finished;
    }

    public void setFinished(Long finished) {
        this.finished = finished;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getAborted() {
        return aborted;
    }

    public void setAborted(Long aborted) {
        this.aborted = aborted;
    }

    public Long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(Long averageDuration) {
        this.averageDuration = averageDuration;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobExecutionCounts [total=");
        builder.append(total);
        builder.append(", queued=");
        builder.append(queued);
        builder.append(", running=");
        builder.append(running);
        builder.append(", finished=");
        builder.append(finished);
        builder.append(", failed=");
        builder.append(failed);
        builder.append(", aborted=");
        builder.append(aborted);
        builder.append(", averageDuration=");
        builder.append(averageDuration);
        builder.append("]");
        return builder.toString();
    }
}
