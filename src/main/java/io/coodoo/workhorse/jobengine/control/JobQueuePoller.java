package io.coodoo.workhorse.jobengine.control;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.log.boundary.JobEngineLogService;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@ApplicationScoped
public class JobQueuePoller {

    private static final int ZOMBIE_HUNT_INTERVAL = 300;

    private static Logger logger = LoggerFactory.getLogger(JobQueuePoller.class);

    private static ScheduledFuture<?> scheduledFuture;

    private static int zombieWatch = ZOMBIE_HUNT_INTERVAL;

    @Resource
    ManagedScheduledExecutorService scheduler;

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @Inject
    JobEngine jobEngine;

    @Inject
    JobEngineController jobEngineController;

    @Inject
    JobEngineLogService jobEngineLogService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void poll() {
        for (Job job : Job.getAllByStatus(entityManager, JobStatus.ACTIVE)) {
            if (job.getThreads() < 1) {
                continue;
            }
            int numberOfJobExecutionsQueued = jobEngine.getNumberOfJobExecutionsInQueue(job.getId());
            int addedJobExecutions = 0;

            if (numberOfJobExecutionsQueued < JobEngineConfig.JOB_QUEUE_MIN) {
                for (JobExecution jobExecution : JobExecution.getNextCandidates(entityManager, job.getId(), JobEngineConfig.JOB_QUEUE_MAX)) {
                    if (jobEngine.addJobExecution(jobExecution)) {
                        addedJobExecutions++;
                    }
                }
                if (addedJobExecutions > 0) {
                    logger.info("Added {} new to {} existing job executions in memory queue for job {}", addedJobExecutions, numberOfJobExecutionsQueued,
                                    job.getName());
                }
            }
        }
        huntZombies();
    }

    private void huntZombies() {
        zombieWatch += JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL;
        if (zombieWatch > ZOMBIE_HUNT_INTERVAL) { // watch for zombies every 5 minutes
            // go, hunt down zombies!
            jobEngineController.huntJobExecutionZombies();
            zombieWatch = 0;
        }
    }

    public void start() {
        if (isRunning()) {
            stop();
        }
        scheduledFuture = this.scheduler.scheduleAtFixedRate(this::poll, 0, JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL, TimeUnit.SECONDS);

        String logMessage = String.format("Job queue poller started with a %s seconds interval", JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL);
        logger.info(logMessage);
        jobEngineLogService.logMessage(logMessage, null, true);
    }

    public void stop() {
        if (isRunning()) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;

            String logMessage = "Job queue poller stopped";
            logger.info(logMessage);
            jobEngineLogService.logMessage(logMessage, null, true);
        } else {
            logger.info("Job queue poller cann't be stopped because it's currently not running!");
        }
    }

    public boolean isRunning() {
        return scheduledFuture != null;
    }

}
