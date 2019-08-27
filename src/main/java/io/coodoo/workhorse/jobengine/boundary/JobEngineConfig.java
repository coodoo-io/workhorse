package io.coodoo.workhorse.jobengine.boundary;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;

/**
 * Basic configuration that can be changed in the implementation<br>
 * If for example you want to change the error log marker, just call<br>
 * <tt>JobEngineConfig.LOG_ERROR_MARKER = "SCREW-UP:"</tt>
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public final class JobEngineConfig {

    private JobEngineConfig() {}

    /**
     * ZoneId Object time zone for LocalDateTime instance creation. Default is UTC
     */
    public static ZoneId TIME_ZONE = ZoneId.of("UTC");

    /**
     * Job queue poller interval in seconds
     */
    public static int JOB_QUEUE_POLLER_INTERVAL = 5;

    /**
     * Max amount of executions to load into the memory queue per job
     */
    public static int JOB_QUEUE_MAX = 1000;

    /**
     * Min amount of executions in memory queue before the poller gets to add more
     */
    public static int JOB_QUEUE_MIN = 100;

    /**
     * A zombie is an execution that is stuck in status {@link JobExecutionStatus#RUNNING} for this amount of minutes (if set to 0 there the hunt is off)
     */
    public static int ZOMBIE_RECOGNITION_TIME = 120;

    /**
     * If an execution is stuck in status {@link JobExecutionStatus#RUNNING} and doesn't change, it has became a zombie! Once found we have a cure!
     */
    public static JobExecutionStatus ZOMBIE_CURE_STATUS = JobExecutionStatus.ABORTED;

    /**
     * Days until minute by minute statistic records gets deleted (0 to keep all)
     */
    public static int DAYS_UNTIL_STATISTIC_MINUTES_DELETION = 10;

    /**
     * Days until hourly statistic records gets deleted (0 to keep all)
     */
    public static int DAYS_UNTIL_STATISTIC_HOURS_DELETION = 30;

    /**
     * Log timestamp pattern. Default is <code>[HH:mm:ss.SSS]</code>
     */
    public static DateTimeFormatter LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern("'['HH:mm:ss.SSS']'");

    /**
     * Log info marker. Default is none
     */
    public static String LOG_INFO_MARKER = null;

    /**
     * Log warn marker. Default is <code>[WARN]</code>
     */
    public static String LOG_WARN_MARKER = "[WARN]";

    /**
     * Log error marker. Default is <code>[ERROR]</code>
     */
    public static String LOG_ERROR_MARKER = "[ERROR]";

}
