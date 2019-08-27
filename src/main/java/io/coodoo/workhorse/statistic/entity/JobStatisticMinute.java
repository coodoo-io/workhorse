package io.coodoo.workhorse.statistic.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

/**
 * 1 minute record of a jobs activity
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_statistic_minute")
@NamedQueries({

                @NamedQuery(name = "JobStatisticMinute.findLatestByJobId",
                                query = "SELECT j FROM JobStatisticMinute j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC"),
                @NamedQuery(name = "JobStatisticMinute.summaryByJobId",
                                query = "SELECT new io.coodoo.workhorse.statistic.entity.JobStatisticSummary(SUM(j.durationCount), SUM(j.durationSum), MAX(j.durationMax), MIN(j.durationMin), AVG(j.durationAvg), SUM(j.finished), SUM(j.failed), SUM(j.schedule)) FROM JobStatisticMinute j WHERE j.jobId = :jobId AND j.from >= :from AND j.to <= :to"),
                @NamedQuery(name = "JobStatisticMinute.deleteAllByJobId", query = "DELETE FROM JobStatisticMinute j WHERE j.jobId = :jobId"),
                @NamedQuery(name = "JobStatisticMinute.deleteOlderThanDate", query = "DELETE FROM JobStatisticMinute j WHERE j.from < :date")

})
public class JobStatisticMinute extends JobStatistic {

    private static final long serialVersionUID = 1L;

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

    public Integer getQueued() {
        return queued;
    }

    public void setQueued(Integer queued) {
        this.queued = queued;
    }

    public Long getDurationMedian() {
        return durationMedian;
    }

    public void setDurationMedian(Long durationMedian) {
        this.durationMedian = durationMedian;
    }

    @Override
    public String toString() {
        return "JobStatisticMinute" + super.toString() + ", durationMedian=" + durationMedian + ", queued=" + queued;
    }

    /**
     * Executes the query 'JobStatisticMinute.deleteAllByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return Number of deleted objects
     */
    public static int deleteAllByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatisticMinute.deleteAllByJobId");
        query = query.setParameter("jobId", jobId);
        return query.executeUpdate();
    }

    /**
     * Executes the query 'JobStatisticMinute.findLatestByJobId' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return the result
     */
    public static JobStatisticMinute findLatestByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatisticMinute.findLatestByJobId");
        query = query.setParameter("jobId", jobId);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (JobStatisticMinute) results.get(0);
    }

    /**
     * Executes the query 'JobStatisticMinute.summaryByJobId' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @param from the from
     * @param to the to
     * @return the result
     */
    public static JobStatisticSummary summaryByJobId(EntityManager entityManager, Long jobId, LocalDateTime from, LocalDateTime to) {
        Query query = entityManager.createNamedQuery("JobStatisticMinute.summaryByJobId");
        query = query.setParameter("jobId", jobId);
        query = query.setParameter("from", from);
        query = query.setParameter("to", to);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (JobStatisticSummary) results.get(0);
    }

    /**
     * Executes the query 'JobStatisticMinute.deleteOlderThanDate' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param date the date
     * @return Number of deleted objects
     */
    public static int deleteOlderThanDate(EntityManager entityManager, LocalDateTime date) {
        Query query = entityManager.createNamedQuery("JobStatisticMinute.deleteOlderThanDate");
        query = query.setParameter("date", date);
        return query.executeUpdate();
    }

}
