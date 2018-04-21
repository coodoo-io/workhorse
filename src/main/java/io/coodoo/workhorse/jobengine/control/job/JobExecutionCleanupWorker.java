package io.coodoo.workhorse.jobengine.control.job;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobWorker;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobScheduleConfig;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.annotation.SystemJob;

/**
 * Deletes old Job Executions database entries, which are not needed anymore.
 * 
 * Every Job can configure his own clean up days count by setting JobConfig.
 */
@RequestScoped
@SystemJob
@JobConfig(name = "Job Execution Cleanup", description = "Deletes old job executions from the database")
@JobScheduleConfig(minute = "17", hour = "4")
public class JobExecutionCleanupWorker extends JobWorker {

    private final Logger log = LoggerFactory.getLogger(JobExecutionCleanupWorker.class);

    @Inject
    JobEngineController jobEngineController;

    public void scheduledJobExecutionCreation() {
        jobEngineController.deleteOlderJobExecutions();
    }

    @Override
    public void doWork() {

        JobExecutionCleanupParameter parameters = getParameters();

        int deletedJobExecutions = jobEngineController.deleteOlderJobExecutions(parameters.jobId, parameters.minDaysOld);

        log.info("Deleted {} job executions of job '{}' that were older than {} days", deletedJobExecutions, parameters.jobName, parameters.minDaysOld);
    }

}
