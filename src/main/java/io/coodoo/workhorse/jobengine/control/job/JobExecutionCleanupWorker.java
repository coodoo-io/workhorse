package io.coodoo.workhorse.jobengine.control.job;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.boundary.JobWorker;
import io.coodoo.workhorse.jobengine.boundary.annotation.InitialJobConfig;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.annotation.SystemJob;
import io.coodoo.workhorse.jobengine.entity.Job;

/**
 * Deletes old Job Executions database entries, which are not needed anymore.
 * 
 * Every Job can configure his own clean up days count by setting JobConfig.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@RequestScoped
@SystemJob
@InitialJobConfig(name = "Job Execution Cleanup", schedule = "0 17 4 * * *", failRetries = 1, description = "Deletes old job executions from the database")
public class JobExecutionCleanupWorker extends JobWorker {

    private final Logger logger = LoggerFactory.getLogger(JobExecutionCleanupWorker.class);

    @Inject
    JobEngineService jobEngineService;

    @Inject
    JobEngineController jobEngineController;

    @Override
    public void doWork() throws Exception {

        List<Job> jobs = jobEngineService.getAllJobs();
        int deletedSum = 0;
        logInfo(logger, "Deleted | Days | Job ID | Job Name");

        for (Job job : jobs) {
            if (job.getDaysUntilCleanUp() > 0) {
                try {
                    int deleted = jobEngineController.deleteOlderJobExecutions(job.getId(), job.getDaysUntilCleanUp());
                    logInfo(logger, String.format("%7d | %4d | %6d | %s", deleted, job.getDaysUntilCleanUp(), job.getId(), job.getName()));
                    deletedSum += deleted;
                } catch (Exception e) {
                    logError(logger, "Could not delete executions for job (ID " + job.getId() + ") ': " + e.getMessage(), e);
                }
            } else {
                logInfo(logger, String.format("      - |    - | %6d | %s", job.getId(), job.getName()));
            }
        }

        logInfo(logger, "Deleted " + deletedSum + " job executions");
    }
}
