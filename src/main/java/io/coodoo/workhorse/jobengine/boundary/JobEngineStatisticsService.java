package io.coodoo.workhorse.jobengine.boundary;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.JobEngine;

/**
 * Provides statistics
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineStatisticsService {

    private final Logger logger = LoggerFactory.getLogger(JobEngineStatisticsService.class);

    @Inject
    JobEngine jobEngine;

    @Inject
    JobEngineService jobEngineService;

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    public String jobEngineStatus() {
        if (jobEngineService.isRunning()) {
            return jobEngine.getInfo();
        }
        return "-";
    }

    public String jobEngineStatus(Long jobId) {
        if (jobEngineService.isRunning()) {
            return jobEngine.getInfo(jobEngineService.getJobById(jobId));
        }
        return "-";
    }

    public int getNumberOfActiveJobExecutions(Long jobId) {
        return jobEngine.getNumberOfJobExecutionsInQueue(jobId);
    }

    public String getStatus() {

        StringBuffer query = new StringBuffer();
        query.append(" SELECT COUNT(*),");
        query.append(" COUNT(CASE WHEN status = 'QUEUED' THEN 0 END),");
        query.append(" COUNT(CASE WHEN status = 'RUNNING' THEN 0 END),");
        query.append(" COUNT(CASE WHEN status = 'FINISHED' THEN 0 END),");
        query.append(" COUNT(CASE WHEN status = 'FAILED' THEN 0 END),");
        query.append(" COUNT(CASE WHEN status = 'ABORTED' THEN 0 END),");
        query.append(" AVG(duration)");
        query.append(" FROM jobengine_execution");

        Object[] result = (Object[]) entityManager.createNativeQuery(query.toString()).getSingleResult();

        String executions = result[0] + " executions [" + result[1] + " queued, " + result[2] + " running, " + result[3] + " finished, " + result[4]
                        + " failed, " + result[5] + " aborted] with an average duration of " + result[0] + "ms";

        query = new StringBuffer();
        query.append(" SELECT COUNT(*),");
        query.append(" COUNT(CASE WHEN status = 'ACTIVE' THEN 0 END),");
        query.append(" COUNT(CASE WHEN status = 'INACTIVE' THEN 0 END),");
        query.append(" COUNT(CASE WHEN status = 'ERROR' THEN 0 END)");
        query.append(" FROM jobengine_job");

        result = (Object[]) entityManager.createNativeQuery(query.toString()).getSingleResult();

        String jobs = result[0] + " jobs [" + result[1] + " active, " + result[2] + " inactive, " + result[3] + " error]";

        String runnung = "JobEngine is currently " + (jobEngineService.isRunning() ? "running" : "not running");

        String status = runnung + System.lineSeparator() + jobs + System.lineSeparator() + executions;

        logger.info(status);
        return status;
    }

}
