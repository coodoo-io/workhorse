package io.coodoo.workhorse.jobengine.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import io.coodoo.framework.jpa.boundary.entity.RevisionDatesOccEntity;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobScheduleConfig;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_schedule")
@NamedQueries({@NamedQuery(name = "JobSchedule.getAll", query = "SELECT s FROM JobSchedule s"),
                @NamedQuery(name = "JobSchedule.getByJobId", query = "SELECT s FROM JobSchedule s WHERE s.jobId=:jobId")})
public class JobSchedule extends RevisionDatesOccEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "second")
    private String second;

    @Column(name = "minute")
    private String minute;

    @Column(name = "hour")
    private String hour;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "day_of_month")
    private String dayOfMonth;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private String year;

    JobSchedule() {}

    public JobSchedule(Long jobId, JobScheduleConfig jobScheduleConfig) {
        super();
        this.jobId = jobId;
        this.second = jobScheduleConfig.second();
        this.minute = jobScheduleConfig.minute();
        this.hour = jobScheduleConfig.hour();
        this.dayOfWeek = jobScheduleConfig.dayOfWeek();
        this.dayOfMonth = jobScheduleConfig.dayOfMonth();
        this.month = jobScheduleConfig.month();
        this.year = jobScheduleConfig.year();
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "JobSchedule [createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", id=" + id + ", jobId=" + jobId + ", second=" + second + ", minute="
                        + minute + ", hour=" + hour + ", dayOfWeek=" + dayOfWeek + ", dayOfMonth=" + dayOfMonth + ", month=" + month + ", year=" + year + "]";
    }

    public String toCronString() {
        return second + " " + minute + " " + hour + " " + dayOfWeek + " " + dayOfMonth + " " + month + " " + year;
    }

    /**
     * Executes the query 'Schedule.getAll' returning a list of result objects.
     *
     * @param entityManager the entityManager
     * @return List of result objects
     */
    @SuppressWarnings("unchecked")
    public static List<JobSchedule> getAll(EntityManager entityManager) {
        Query query = entityManager.createNamedQuery("JobSchedule.getAll");
        return query.getResultList();
    }

    /**
     * Executes the query 'Schedule.getByJobId' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return the result
     */
    public static JobSchedule getByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobSchedule.getByJobId");
        query = query.setParameter("jobId", jobId);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (JobSchedule) results.get(0);
    }

}
