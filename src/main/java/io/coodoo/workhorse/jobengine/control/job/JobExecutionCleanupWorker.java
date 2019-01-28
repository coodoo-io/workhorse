package io.coodoo.workhorse.jobengine.control.job;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobWorkerWith;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobConfig;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobScheduleConfig;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.annotation.SystemJob;

/**
 * Deletes old Job Executions database entries, which are not needed anymore.
 * 
 * Every Job can configure his own clean up days count by setting JobConfig.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@RequestScoped
@SystemJob
@JobConfig(name = "Job Execution Cleanup", description = "Deletes old job executions from the database")
@JobScheduleConfig(minute = "17", hour = "4")
public class JobExecutionCleanupWorker extends JobWorkerWith<JobExecutionCleanupParameter> {

    private final Logger logger = LoggerFactory.getLogger(JobExecutionCleanupWorker.class);

    @Inject
    JobEngineController jobEngineController;

    public void scheduledJobExecutionCreation() {

        jobEngineController.deleteOlderJobExecutions();
    }

    @Override
    public void doWork(JobExecutionCleanupParameter parameters) throws Exception {

        int deleted = jobEngineController.deleteOlderJobExecutions(parameters.jobId, parameters.minDaysOld);

        logInfo(logger, "Deleted " + deleted + " job executions of job '" + parameters.jobName + "' that were older than " + parameters.minDaysOld + " days");
    }

}
