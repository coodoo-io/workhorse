package io.coodoo.workhorse.jobengine.control;

import javax.annotation.Resource;
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

import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobSchedule;
import io.coodoo.workhorse.jobengine.entity.JobType;

@Singleton
public class JobScheduler {

    private static Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    @Inject
    JobEngineService jobEngineService;

    @Inject
    JobEngineController jobEngineController;

    @Resource
    protected TimerService timerService;

    public void start(Job job) {

        if (JobType.SCHEDULED.equals(job.getType())) {

            JobSchedule jobSchedule = jobEngineService.getScheduleByJobId(job.getId());

            if (jobSchedule == null) {
                throw new RuntimeException("No schedule found for job " + job.getName());
            }

            stop(job);

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setInfo(job);
            timerConfig.setPersistent(false);

            ScheduleExpression scheduleExpression = toScheduleExpression(jobSchedule);
            timerService.createCalendarTimer(scheduleExpression, timerConfig);

            logger.info("Schedule {} started for Job {}", toString(scheduleExpression), job.getName());
        }
    }

    public void stop(Job job) {
        if (JobType.SCHEDULED.equals(job.getType())) {
            for (Timer timer : timerService.getTimers()) {
                if (job.equals(timer.getInfo())) {

                    logger.info("Schedule {} stopped for Job {}", toString(timer.getSchedule()), job.getName());
                    timer.cancel();
                }
            }
        }
    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void timeout(Timer currentTimer) {

        Job job = (Job) currentTimer.getInfo();
        try {

            BaseJobWorker jobWorker = jobEngineController.getJobWorker(job);
            jobWorker.scheduledJobExecutionCreation();

        } catch (Exception e) {
            logger.error("Timeout failed for job {}", job.getName(), e);
        }
    }

    public ScheduleExpression toScheduleExpression(JobSchedule jobSchedule) {

        ScheduleExpression scheduleExpression = new ScheduleExpression();
        scheduleExpression.second(jobSchedule.getSecond());
        scheduleExpression.minute(jobSchedule.getMinute());
        scheduleExpression.hour(jobSchedule.getHour());
        scheduleExpression.dayOfWeek(jobSchedule.getDayOfWeek());
        scheduleExpression.dayOfMonth(jobSchedule.getDayOfMonth());
        scheduleExpression.month(jobSchedule.getMonth());
        scheduleExpression.year(jobSchedule.getYear());
        return scheduleExpression;
    }

    public String toString(ScheduleExpression scheduleExpression) {

        StringBuilder expression = new StringBuilder();
        expression.append("[");
        expression.append(scheduleExpression.getSecond());
        expression.append(" ");
        expression.append(scheduleExpression.getMinute());
        expression.append(" ");
        expression.append(scheduleExpression.getHour());
        expression.append(" ");
        expression.append(scheduleExpression.getDayOfWeek());
        expression.append(" ");
        expression.append(scheduleExpression.getDayOfMonth());
        expression.append(" ");
        expression.append(scheduleExpression.getMonth());
        expression.append(" ");
        expression.append(scheduleExpression.getYear());
        expression.append("]");
        return expression.toString();
    }

}
