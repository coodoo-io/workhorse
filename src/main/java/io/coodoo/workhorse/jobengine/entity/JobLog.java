package io.coodoo.workhorse.jobengine.entity;

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

/**
 * A log to record changes on jobs
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_log")
@NamedQueries({

                @NamedQuery(name = "JobLog.deleteAllByJobId", query = "DELETE FROM JobLog j WHERE j.jobId = :jobId"),

})
public class JobLog extends RevisionDatesEntity {

    private static final long serialVersionUID = 1L;

    /**
     * The reference to the job
     */
    @Column(name = "job_id")
    private Long jobId;

    /**
     * Status at creation
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

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
     * General log message
     */
    @Column(name = "message")
    private String message;

    /**
     * If available we record the exception stacktrace
     */
    @Column(name = "stacktrace")
    private String stacktrace;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        builder.append("JobLog [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", jobId=");
        builder.append(jobId);
        builder.append(", status=");
        builder.append(status);
        builder.append(", changeParameter=");
        builder.append(changeParameter);
        builder.append(", changeOld=");
        builder.append(changeOld);
        builder.append(", changeNew=");
        builder.append(changeNew);
        builder.append(", message=");
        builder.append(message);
        builder.append(", stacktrace=");
        builder.append(stacktrace != null);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Executes the query 'JobLog.deleteAllByJobId' returning the number of affected rows.
     *
     * @param entityManager the entityManager
     * @param jobId the jobId
     * @return Number of deleted objects
     */
    public static int deleteAllByJobId(EntityManager entityManager, Long jobId) {
        Query query = entityManager.createNamedQuery("JobLog.deleteAllByJobId");
        query = query.setParameter("jobId", jobId);
        return query.executeUpdate();
    }

}
