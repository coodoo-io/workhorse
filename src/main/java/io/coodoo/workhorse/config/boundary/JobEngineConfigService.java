package io.coodoo.workhorse.config.boundary;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.framework.jpa.control.JpaEssentialsConfig;
import io.coodoo.workhorse.config.entity.Config;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.JobQueuePoller;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.log.boundary.JobEngineLogService;

/**
 * Access to the job engines configuration
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineConfigService {

    private final Logger logger = LoggerFactory.getLogger(JobEngineConfigService.class);

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @Inject
    JobEngineLogService jobEngineLogService;

    @Inject
    JobQueuePoller jobQueuePoller;

    public Config getConfig() {

        Config config = Config.getConfig(entityManager);

        if (config == null) {

            config = new Config();
            entityManager.persist(config);

            logger.info("Created: {}", config);
            jobEngineLogService.logMessage("Initial config set: " + config, null, false);
            initializeStaticConfig();
        }
        return config;
    }

    public Config initializeStaticConfig() {

        Config config = getConfig();

        if (config.getTimeZone() == null) {
            JobEngineConfig.TIME_ZONE = ZoneId.systemDefault();
            JpaEssentialsConfig.LOCAL_DATE_TIME_ZONE = JobEngineConfig.TIME_ZONE.getId();
        } else {
            JpaEssentialsConfig.LOCAL_DATE_TIME_ZONE = config.getTimeZone();
            JobEngineConfig.TIME_ZONE = ZoneId.of(JpaEssentialsConfig.LOCAL_DATE_TIME_ZONE);
        }
        JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL = config.getJobQueuePollerInterval();
        JobEngineConfig.JOB_QUEUE_MAX = config.getJobQueueMax();
        JobEngineConfig.JOB_QUEUE_MIN = config.getJobQueueMin();
        JobEngineConfig.ZOMBIE_RECOGNITION_TIME = config.getZombieRecognitionTime();
        JobEngineConfig.ZOMBIE_CURE_STATUS = config.getZombieCureStatus();
        JobEngineConfig.DAYS_UNTIL_STATISTIC_MINUTES_DELETION = config.getDaysUntilStatisticMinutesDeletion();
        JobEngineConfig.DAYS_UNTIL_STATISTIC_HOURS_DELETION = config.getDaysUntilStatisticHoursDeletion();
        JobEngineConfig.LOG_CHANGE = config.getLogChange();
        JobEngineConfig.LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern(config.getLogTimeFormatter());
        JobEngineConfig.LOG_INFO_MARKER = config.getLogInfoMarker();
        JobEngineConfig.LOG_WARN_MARKER = config.getLogWarnMarker();
        JobEngineConfig.LOG_ERROR_MARKER = config.getLogErrorMarker();

        logger.info("Initialized: {}", config);
        return config;
    }

    public Config updateConfig(String timeZone, int jobQueuePollerInterval, int jobQueueMax, int jobQueueMin, int zombieRecognitionTime,
                    JobExecutionStatus zombieCureStatus, int daysUntilStatisticMinutesDeletion, int daysUntilStatisticHoursDeletion, String logChange,
                    String logTimeFormatter, String logInfoMarker, String logWarnMarker, String logErrorMarker) {

        Config config = getConfig();

        updateTimeZone(config, timeZone);
        updateJobQueuePollerInterval(config, jobQueuePollerInterval);
        updateJobQueueMax(config, jobQueueMax);
        updateJobQueueMin(config, jobQueueMin);
        updateZombieRecognitionTime(config, zombieRecognitionTime);
        updateZombieCureStatuse(config, zombieCureStatus);
        updateDaysUntilStatisticMinutesDeletion(config, daysUntilStatisticMinutesDeletion);
        updateDaysUntilStatisticHoursDeletion(config, daysUntilStatisticHoursDeletion);
        updateLogChange(config, logChange);
        updateLogTimeFormatter(config, logTimeFormatter);
        updateLogInfoMarker(config, logInfoMarker);
        updateLogWarnMarker(config, logWarnMarker);
        updateLogErrorMarker(config, logErrorMarker);

        logger.info("Updated: {}", config);
        return config;
    }

    private void updateTimeZone(Config config, String timeZone) {

        if (timeZone != null && !ZoneId.getAvailableZoneIds().contains(timeZone)) {
            throw new RuntimeException("Time zone '" + timeZone + "' is not available!");
        }
        if (!Objects.equals(config.getTimeZone(), timeZone)) {
            ZoneId systemDefault = ZoneId.systemDefault();
            if (timeZone == null || timeZone == systemDefault.getId()) {
                JobEngineConfig.TIME_ZONE = systemDefault;
                JpaEssentialsConfig.LOCAL_DATE_TIME_ZONE = systemDefault.getId();
                jobEngineLogService.logChange(null, null, "Time zone", config.getTimeZone(), systemDefault,
                                "System default time-zone is used: " + systemDefault);
            } else {
                JobEngineConfig.TIME_ZONE = ZoneId.of(timeZone);
                JpaEssentialsConfig.LOCAL_DATE_TIME_ZONE = timeZone;
                jobEngineLogService.logChange(null, null, "Time zone", config.getTimeZone(), timeZone, null);
            }
            config.setTimeZone(timeZone);
        }
    }

    private void updateJobQueuePollerInterval(Config config, int jobQueuePollerInterval) {

        if (jobQueuePollerInterval < 1 || jobQueuePollerInterval > 60) {
            throw new RuntimeException("The job queue poller interval must be between 1 and 60!");
        }
        if (config.getJobQueuePollerInterval() != jobQueuePollerInterval) {

            JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL = jobQueuePollerInterval;
            jobEngineLogService.logChange(null, null, "Job queue poller interval", config.getJobQueuePollerInterval(), jobQueuePollerInterval, null);
            config.setJobQueuePollerInterval(jobQueuePollerInterval);

            if (jobQueuePoller.isRunning()) {
                jobQueuePoller.start(); // also stops it, so it is a restart
            }
        }
    }

    private void updateJobQueueMax(Config config, int jobQueueMax) {

        if (jobQueueMax < 1) {
            throw new RuntimeException("The max amount of executions to load into the memory queue per job must be higher than 0!");
        }
        if (config.getJobQueueMax() != jobQueueMax) {

            JobEngineConfig.JOB_QUEUE_MAX = jobQueueMax;
            jobEngineLogService.logChange(null, null, "Max amount of executions to load into the memory queue per job", config.getJobQueueMax(), jobQueueMax,
                            null);
            config.setJobQueueMax(jobQueueMax);
        }
    }

    private void updateJobQueueMin(Config config, int jobQueueMin) {

        if (jobQueueMin < 1) {
            throw new RuntimeException("The min amount of executions in memory queue before the poller gets to add more must be higher than 0!");
        }
        if (config.getJobQueueMin() != jobQueueMin) {

            JobEngineConfig.JOB_QUEUE_MIN = jobQueueMin;
            jobEngineLogService.logChange(null, null, "Min amount of executions in memory queue before the poller gets to add more", config.getJobQueueMin(),
                            jobQueueMin, null);
            config.setJobQueueMin(jobQueueMin);
        }
    }

    private void updateZombieRecognitionTime(Config config, int zombieRecognitionTime) {

        if (zombieRecognitionTime < 0) {
            throw new RuntimeException("The zombie recognition time more can't be negative!");
        }
        if (config.getZombieRecognitionTime() != zombieRecognitionTime) {

            JobEngineConfig.ZOMBIE_RECOGNITION_TIME = zombieRecognitionTime;

            String message = zombieRecognitionTime > 0 ? null : "Zombie recognition time is set to '0', so the hunt is off!";
            jobEngineLogService.logChange(null, null, "Zombie recognition time", config.getZombieRecognitionTime(), zombieRecognitionTime, message);
            config.setZombieRecognitionTime(zombieRecognitionTime);
        }
    }

    private void updateZombieCureStatuse(Config config, JobExecutionStatus zombieCureStatus) {

        if (zombieCureStatus == null) {
            throw new RuntimeException("The zombie cure status is needed!");
        }
        if (!Objects.equals(config.getZombieCureStatus(), zombieCureStatus)) {

            JobEngineConfig.ZOMBIE_CURE_STATUS = zombieCureStatus;
            jobEngineLogService.logChange(null, null, "Zombie cure status", config.getZombieCureStatus(), zombieCureStatus, null);
            config.setZombieCureStatus(zombieCureStatus);
        }
    }

    private void updateDaysUntilStatisticMinutesDeletion(Config config, int daysUntilStatisticMinutesDeletion) {

        if (daysUntilStatisticMinutesDeletion < 0) {
            throw new RuntimeException("The days until by minute statistic records gets deleted can't be negative!");
        }
        if (config.getDaysUntilStatisticMinutesDeletion() != daysUntilStatisticMinutesDeletion) {

            JobEngineConfig.DAYS_UNTIL_STATISTIC_MINUTES_DELETION = daysUntilStatisticMinutesDeletion;

            String message = daysUntilStatisticMinutesDeletion > 0 ? null
                            : "Days until by minute statistic records gets deleted is set to '0', so all will be kept!";
            jobEngineLogService.logChange(null, null, "Days until by minute statistic records gets deleted", config.getDaysUntilStatisticMinutesDeletion(),
                            daysUntilStatisticMinutesDeletion, message);
            config.setDaysUntilStatisticMinutesDeletion(daysUntilStatisticMinutesDeletion);
        }
    }

    private void updateDaysUntilStatisticHoursDeletion(Config config, int daysUntilStatisticHoursDeletion) {

        if (daysUntilStatisticHoursDeletion < 0) {
            throw new RuntimeException("The days until hourly statistic records gets deleted can't be negative!");
        }
        if (config.getDaysUntilStatisticHoursDeletion() != daysUntilStatisticHoursDeletion) {

            JobEngineConfig.DAYS_UNTIL_STATISTIC_HOURS_DELETION = daysUntilStatisticHoursDeletion;

            String message = daysUntilStatisticHoursDeletion > 0 ? null
                            : "Days until hourly statistic records gets deleted is set to '0', so all will be kept!";
            jobEngineLogService.logChange(null, null, "Days until hourly statistic records gets deleted", config.getDaysUntilStatisticHoursDeletion(),
                            daysUntilStatisticHoursDeletion, message);
            config.setDaysUntilStatisticHoursDeletion(daysUntilStatisticHoursDeletion);
        }
    }

    private void updateLogChange(Config config, String logChange) {

        if (logChange == null) {
            throw new RuntimeException("The log change pattern is needed!");
        }
        if (("_" + logChange + "_").split("%s", -1).length - 1 != 3) {
            throw new RuntimeException("The log change pattern needs the placeholder '%s' three times!");
        }
        if (!Objects.equals(config.getLogChange(), logChange)) {

            JobEngineConfig.LOG_CHANGE = logChange;
            jobEngineLogService.logChange(null, null, "Log change pattern", config.getLogChange(), logChange, null);
            config.setLogChange(logChange);
        }
    }

    private void updateLogTimeFormatter(Config config, String logTimeFormatter) {
        if (logTimeFormatter == null) {
            throw new RuntimeException("The execution log timestamp pattern is needed!");
        }
        if (!Objects.equals(config.getLogTimeFormatter(), logTimeFormatter)) {

            JobEngineConfig.LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern(logTimeFormatter);
            jobEngineLogService.logChange(null, null, "Execution log timestamp pattern", config.getLogTimeFormatter(), logTimeFormatter, null);
            config.setLogTimeFormatter(logTimeFormatter);
        }
    }

    private void updateLogInfoMarker(Config config, String logInfoMarker) {
        if (!Objects.equals(config.getLogInfoMarker(), logInfoMarker)) {

            JobEngineConfig.LOG_INFO_MARKER = logInfoMarker;
            jobEngineLogService.logChange(null, null, "Execution log info marker", config.getLogInfoMarker(), logInfoMarker, null);
            config.setLogInfoMarker(logInfoMarker);
        }
    }

    private void updateLogWarnMarker(Config config, String logWarnMarker) {
        if (!Objects.equals(config.getLogWarnMarker(), logWarnMarker)) {

            JobEngineConfig.LOG_WARN_MARKER = logWarnMarker;
            jobEngineLogService.logChange(null, null, "Execution log warn marker", config.getLogWarnMarker(), logWarnMarker, null);
            config.setLogWarnMarker(logWarnMarker);
        }
    }

    private void updateLogErrorMarker(Config config, String logErrorMarker) {
        if (!Objects.equals(config.getLogErrorMarker(), logErrorMarker)) {

            JobEngineConfig.LOG_ERROR_MARKER = logErrorMarker;
            jobEngineLogService.logChange(null, null, "Execution log error marker", config.getLogErrorMarker(), logErrorMarker, null);
            config.setLogErrorMarker(logErrorMarker);
        }
    }

}
