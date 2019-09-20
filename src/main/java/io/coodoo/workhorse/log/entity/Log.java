package io.coodoo.workhorse.log.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import io.coodoo.framework.jpa.boundary.entity.RevisionDatesEntity;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

/**
 * A log to record changes on jobs
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_log")
@NamedQueries({

                @NamedQuery(name = "Log.deleteAllByJobId", query = "DELETE FROM Log j WHERE j.jobId = :jobId"),

})
public class Log extends RevisionDatesEntity {

    private static final long serialVersionUID = 1L;

    /**
     * General log message
     */
    @Column(name = "message")
    private String message;

    /**
     * optional reference to the job
     */
    @Column(name = "job_id")
    private Long jobId;

    /**
     * Job status at creation
     */
    @Column(name = "job_status")
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    /**
     * <code>true</code> if log was made by an user, <code>false</code> if log was made by the system
     */
    @Column(name = "by_user")
    private boolean byUser = false;

    /**
     * Name of changed parameter
     */
    @Column(name = "change_parameter")
    private String changeParameter;

    /**
     * Old value of that changed parameter
     */
    @Column(name = "change_old")
    private String changeOld;

    /**
     * New value of that changed parameter
     */
    @Column(name = "change_new")
    private String changeNew;

    /**
     * Host name of the current running system
     */
    @Column(name = "host_name")
    private String hostName;

    /**
     * If available we record the exception stacktrace
     */
    @Column(name = "stacktrace")
    private String stacktrace;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public boolean isByUser() {
        return byUser;
    }

    public void setByUser(boolean byUser) {
        this.byUser = byUser;
    }

    public String getChangeParameter() {
        return changeParameter;
    }

    public void setChangeParameter(String changeParameter) {
        this.changeParameter = changeParameter;
    }

    public String getChangeOld() {
        return changeOld;
    }

    public void setChangeOld(String changeOld) {
        this.changeOld = changeOld;
    }

    public String getChangeNew() {
        return changeNew;
    }

    public void setChangeNew(String changeNew) {
        this.changeNew = changeNew;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", message=");
        builder.append(message);
        builder.append(", jobId=");
        builder.append(jobId);
        builder.append(", jobStatus=");
        builder.append(jobStatus);
        builder.append(", byUser=");
        builder.append(byUser);
        builder.append(", changeParameter=");
        builder.append(changeParameter);
        builder.append(", changeOld=");
        builder.append(changeOld);
        builder.append(", changeNew=");
        builder.append(changeNew);
        builder.append(", hostName=");
        builder.append(hostName);
        builder.append(", stacktrace=");
        builder.append(stacktrace != null);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Executes the query 'Log.deleteAllByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return Number of deleted objects
     */
    public static int deleteAllByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("Log.deleteAllByJobId");
        query = query.setParameter("jobId", jobId);
        return query.executeUpdate();
    }

}
