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
 * 1 hour record of a jobs activity
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_statistic_hour")
@NamedQueries({

                @NamedQuery(name = "JobStatisticHour.findLatestByJobId",
                                query = "SELECT j FROM JobStatisticHour j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC"),
                @NamedQuery(name = "JobStatisticHour.deleteAllByJobId", query = "DELETE FROM JobStatisticHour j WHERE j.jobId = :jobId"),
                @NamedQuery(name = "JobStatisticHour.deleteOlderThanDate", query = "DELETE FROM JobStatisticHour j WHERE j.from < :date")

})
public class JobStatisticHour extends JobStatistic {

    private static final long serialVersionUID = 1L;

    public JobStatisticHour() {}

    public JobStatisticHour(JobStatistic jobStatistic, LocalDateTime from, LocalDateTime to) {
        super(jobStatistic, from, to);
    }

    @Override
    public String toString() {
        return "JobStatisticHour" + super.toString();
    }

    /**
     * Executes the query 'JobStatisticHour.deleteAllByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return Number of deleted objects
     */
    public static int deleteAllByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatisticHour.deleteAllByJobId");
        query = query.setParameter("jobId", jobId);
        return query.executeUpdate();
    }

    /**
     * Executes the query 'JobStatisticHour.findLatestByJobId' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return the result
     */
    public static JobStatisticHour findLatestByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobStatisticHour.findLatestByJobId");
        query = query.setParameter("jobId", jobId);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (JobStatisticHour) results.get(0);
    }

    /**
     * Executes the query 'JobStatisticHour.deleteOlderThanDate' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param date the date
     * @return Number of deleted objects
     */
    public static int deleteOlderThanDate(EntityManager entityManager, LocalDateTime date) {
        Query query = entityManager.createNamedQuery("JobStatisticHour.deleteOlderThanDate");
        query = query.setParameter("date", date);
        return query.executeUpdate();
    }

}
