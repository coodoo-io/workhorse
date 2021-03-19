package io.coodoo.workhorse.jobengine.control;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.log.boundary.JobEngineLogService;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobScheduler {

    private static Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    @Inject
    JobEngineService jobEngineService;

    @Inject
    JobEngineController jobEngineController;

    @Inject
    JobEngineLogService jobEngineLogService;

    @Resource
    protected TimerService timerService;

    public void start(Job job) {

        if (JobStatus.ACTIVE.equals(job.getStatus()) && job.getSchedule() != null && !job.getSchedule().isEmpty()) {

            stop(job);

            ScheduleExpression scheduleExpression = createScheduledExpression(job);
            TimerConfig timerConfig = new TimerConfig(job, false);

            try {
                timerService.createCalendarTimer(scheduleExpression, timerConfig);
                logger.info("Schedule started for Job {}", job.getName());

            } catch (IllegalArgumentException e) {
                jobEngineController.setJobStatus(job.getId(), JobStatus.ERROR);
                logger.error("Invalid schedule found for {}", job, e);
                jobEngineLogService.logException(e, "Invalid schedule: " + job.getSchedule(), job.getId(), JobStatus.ERROR);
            }
        }
    }

    public void stop(Job job) {
        for (Timer timer : timerService.getTimers()) {
            if (job.equals(timer.getInfo())) {

                logger.info("Schedule stopped for Job {}", job.getName());
                timer.cancel();
            }
        }
    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void timeout(Timer currentTimer) {

        Job job = (Job) currentTimer.getInfo();
        try {
            jobEngineService.triggerScheduledJobExecutionCreation(job);
        } catch (Exception e) {
            logger.error("Timeout failed for job {}", job.getName(), e);
        }
    }

    // TODO test da fuck outta this!
    protected ScheduleExpression createScheduledExpression(Job job) {

        final String[] parts = job.getSchedule().split("\\s+");

        if (parts.length < 5 || parts.length > 7) {
            throw new RuntimeException("Invalid schedule in " + job);
        }
        boolean withSeconds = parts.length > 5;
        int ix = withSeconds ? 1 : 0;

        ScheduleExpression scheduleExpression = new ScheduleExpression();
        scheduleExpression.second(withSeconds ? parts[0] : "0");
        scheduleExpression.minute(parts[ix++]);
        scheduleExpression.hour(parts[ix++]);
        scheduleExpression.dayOfMonth(parts[ix++]);
        scheduleExpression.month(parts[ix++]);
        scheduleExpression.dayOfWeek(parts[ix++]);
        if (parts.length > ix) {
            scheduleExpression.year(parts[ix++]);
        }
        scheduleExpression.timezone(JobEngineConfig.TIME_ZONE.toString());

        return scheduleExpression;
    }

}
