package io.coodoo.workhorse.statistic.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

/**
 * 1 day record of a jobs activity
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_statistic_day")
@NamedQueries({

                @NamedQuery(name = "JobStatisticDay.findLatestByJobId",
                                query = "SELECT j FROM JobStatisticDay j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC"),
                @NamedQuery(name = "JobStatisticDay.deleteAllByJobId", query = "DELETE FROM JobStatisticDay j WHERE j.jobId = :jobId"),
                @NamedQuery(name = "JobStatisticDay.deleteOldByJobId", query = "DELETE FROM JobStatisticDay j WHERE j.jobId = :jobId AND j.from < :date")

})
public class JobStatisticDay extends JobStatistic {

    private static final long serialVersionUID = 1L;

    public JobStatisticDay() {}

    public JobStatisticDay(JobStatistic jobStatistic, LocalDateTime from, LocalDateTime to) {
        super(jobStatistic, from, to);
    }

    @Override
    public String toString() {
        return "JobStatisticDay" + super.toString();
    }

    /**
     * Executes the query 'JobStatisticDay.deleteAllByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return Number of deleted objects
     */
    public static int deleteAllByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatisticDay.deleteAllByJobId");
        query = query.setParameter("jobId", jobId);
        return query.executeUpdate();
    }

    /**
     * Executes the query 'JobStatisticDay.findLatestByJobId' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return the result
     */
    public static JobStatisticDay findLatestByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatisticDay.findLatestByJobId");
        query = query.setParameter("jobId", jobId);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (JobStatisticDay) results.get(0);
    }

    /**
     * Executes the query 'JobStatisticDay.deleteOldByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @param date the date
     * @return Number of deleted objects
     */
    public static int deleteOldByJobId(EntityManager entityManager, Long jobId, LocalDateTime date) {
        Query query = entityManager.createNamedQuery("JobStatisticDay.deleteOldByJobId");
        query = query.setParameter("jobId", jobId);
        query = query.setParameter("date", date);
        return query.executeUpdate();
    }

}
