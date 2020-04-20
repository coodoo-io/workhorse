package io.coodoo.workhorse.config.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import io.coodoo.framework.jpa.entity.AbstractIdCreatedUpdatedAtEntity;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

/**
 * Basic configuration<br>
 * <i>There is only one entry in this table and its purpose is to persist the values of {@link JobEngineConfig}!</i>
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_config")
@NamedQueries({

                @NamedQuery(name = "Config.getConfig", query = "SELECT c FROM Config c")

})
public class Config extends AbstractIdCreatedUpdatedAtEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ZoneId Object time zone for LocalDateTime instance creation. Default is UTC
     */
    @Column(name = "time_zone")
    private String timeZone = "UTC";

    /**
     * Job queue poller interval in seconds
     */
    @Column(name = "job_queue_poller_interval")
    private int jobQueuePollerInterval = 5;

    /**
     * Max amount of executions to load into the memory queue per job
     */
    @Column(name = "job_queue_max")
    private int jobQueueMax = 1000;

    /**
     * Min amount of executions in memory queue before the poller gets to add more
     */
    @Column(name = "job_queue_min")
    private int jobQueueMin = 100;

    /**
     * A zombie is an execution that is stuck in status {@link JobExecutionStatus#RUNNING} for this amount of minutes (if set to 0 there the hunt is off)
     */
    @Column(name = "zombie_recognition_time")
    private int zombieRecognitionTime = 120;

    /**
     * If an execution is stuck in status {@link JobExecutionStatus#RUNNING} and doesn't change, it has became a zombie! Once found we have a cure!
     */
    @Column(name = "zombie_cure_status")
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus zombieCureStatus = JobExecutionStatus.ABORTED;

    /**
     * Days until minute by minute statistic records gets deleted (0 to keep all)
     */
    @Column(name = "days_until_statistic_minutes_deletion")
    private int daysUntilStatisticMinutesDeletion = 10;

    /**
     * Days until hourly statistic records gets deleted (0 to keep all)
     */
    @Column(name = "days_until_statistic_hours_deletion")
    private int daysUntilStatisticHoursDeletion = 30;

    /**
     * Log change pattern. Placeholder <code>%s</code> for changeParameter, changeOld and changeNew in this order <br>
     * Default is <code>Changed %s from '%s' to '%s'</code>
     */
    @Column(name = "log_change")
    private String logChange = "%s changed from '%s' to '%s'";

    /**
     * Execution log timestamp pattern. Default is <code>[HH:mm:ss.SSS]</code>
     */
    @Column(name = "log_time_formatter")
    private String logTimeFormatter = "'['HH:mm:ss.SSS']'";

    /**
     * Execution log info marker. Default is none
     */
    @Column(name = "log_info_marker")
    private String logInfoMarker = null;

    /**
     * Execution log warn marker. Default is <code>[WARN]</code>
     */
    @Column(name = "log_warn_marker")
    private String logWarnMarker = "[WARN]";

    /**
     * Execution log error marker. Default is <code>[ERROR]</code>
     */
    @Column(name = "log_error_marker")
    private String logErrorMarker = "[ERROR]";

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public int getJobQueuePollerInterval() {
        return jobQueuePollerInterval;
    }

    public void setJobQueuePollerInterval(int jobQueuePollerInterval) {
        this.jobQueuePollerInterval = jobQueuePollerInterval;
    }

    public int getJobQueueMax() {
        return jobQueueMax;
    }

    public void setJobQueueMax(int jobQueueMax) {
        this.jobQueueMax = jobQueueMax;
    }

    public int getJobQueueMin() {
        return jobQueueMin;
    }

    public void setJobQueueMin(int jobQueueMin) {
        this.jobQueueMin = jobQueueMin;
    }

    public int getZombieRecognitionTime() {
        return zombieRecognitionTime;
    }

    public void setZombieRecognitionTime(int zombieRecognitionTime) {
        this.zombieRecognitionTime = zombieRecognitionTime;
    }

    public JobExecutionStatus getZombieCureStatus() {
        return zombieCureStatus;
    }

    public void setZombieCureStatus(JobExecutionStatus zombieCureStatus) {
        this.zombieCureStatus = zombieCureStatus;
    }

    public int getDaysUntilStatisticMinutesDeletion() {
        return daysUntilStatisticMinutesDeletion;
    }

    public void setDaysUntilStatisticMinutesDeletion(int daysUntilStatisticMinutesDeletion) {
        this.daysUntilStatisticMinutesDeletion = daysUntilStatisticMinutesDeletion;
    }

    public int getDaysUntilStatisticHoursDeletion() {
        return daysUntilStatisticHoursDeletion;
    }

    public void setDaysUntilStatisticHoursDeletion(int daysUntilStatisticHoursDeletion) {
        this.daysUntilStatisticHoursDeletion = daysUntilStatisticHoursDeletion;
    }

    public String getLogChange() {
        return logChange;
    }

    public void setLogChange(String logChange) {
        this.logChange = logChange;
    }

    public String getLogTimeFormatter() {
        return logTimeFormatter;
    }

    public void setLogTimeFormatter(String logTimeFormatter) {
        this.logTimeFormatter = logTimeFormatter;
    }

    public String getLogInfoMarker() {
        return logInfoMarker;
    }

    public void setLogInfoMarker(String logInfoMarker) {
        this.logInfoMarker = logInfoMarker;
    }

    public String getLogWarnMarker() {
        return logWarnMarker;
    }

    public void setLogWarnMarker(String logWarnMarker) {
        this.logWarnMarker = logWarnMarker;
    }

    public String getLogErrorMarker() {
        return logErrorMarker;
    }

    public void setLogErrorMarker(String logErrorMarker) {
        this.logErrorMarker = logErrorMarker;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Config [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", updatedAt=");
        builder.append(updatedAt);
        builder.append(", timeZone=");
        builder.append(timeZone);
        builder.append(", jobQueuePollerInterval=");
        builder.append(jobQueuePollerInterval);
        builder.append(", jobQueueMax=");
        builder.append(jobQueueMax);
        builder.append(", jobQueueMin=");
        builder.append(jobQueueMin);
        builder.append(", zombieRecognitionTime=");
        builder.append(zombieRecognitionTime);
        builder.append(", zombieCureStatus=");
        builder.append(zombieCureStatus);
        builder.append(", daysUntilStatisticMinutesDeletion=");
        builder.append(daysUntilStatisticMinutesDeletion);
        builder.append(", daysUntilStatisticHoursDeletion=");
        builder.append(daysUntilStatisticHoursDeletion);
        builder.append(", logChange=");
        builder.append(logChange);
        builder.append(", logTimeFormatter=");
        builder.append(logTimeFormatter);
        builder.append(", logInfoMarker=");
        builder.append(logInfoMarker);
        builder.append(", logWarnMarker=");
        builder.append(logWarnMarker);
        builder.append(", logErrorMarker=");
        builder.append(logErrorMarker);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Executes the query 'Config.getConfig' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @return the result
     */
    public static Config getConfig(EntityManager entityManager) {
        Query query = entityManager.createNamedQuery("Config.getConfig");
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (Config) results.get(0);
    }

}
