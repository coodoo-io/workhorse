package io.coodoo.workhorse.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import io.coodoo.framework.jpa.boundary.entity.RevisionDatesEntity;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.log.entity.Log;

/**
 * A LogView defines a single {@link Log} which is joined with job information if it has a relation.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "jobengine_log_view")
public class LogView extends RevisionDatesEntity {

    /**
     * General log message
     */
    @Column(name = "message")
    private String message;

    /**
     * optional reference to the job and its current attributes
     */
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "job_status")
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    @Column(name = "job_fail_retries")
    private Integer jobFailRetries;

    @Column(name = "job_threads")
    private Integer jobThreads;

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
    private boolean stacktrace;

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getJobFailRetries() {
        return jobFailRetries;
    }

    public void setJobFailRetries(Integer jobFailRetries) {
        this.jobFailRetries = jobFailRetries;
    }

    public Integer getJobThreads() {
        return jobThreads;
    }

    public void setJobThreads(Integer jobThreads) {
        this.jobThreads = jobThreads;
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

    public boolean isStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(boolean stacktrace) {
        this.stacktrace = stacktrace;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LogView [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", message=");
        builder.append(message);
        builder.append(", jobId=");
        builder.append(jobId);
        builder.append(", jobName=");
        builder.append(jobName);
        builder.append(", jobDescription=");
        builder.append(jobDescription);
        builder.append(", jobStatus=");
        builder.append(jobStatus);
        builder.append(", jobFailRetries=");
        builder.append(jobFailRetries);
        builder.append(", jobThreads=");
        builder.append(jobThreads);
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
        builder.append(stacktrace);
        builder.append("]");
        return builder.toString();
    }

}
