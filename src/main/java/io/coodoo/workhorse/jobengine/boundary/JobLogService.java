package io.coodoo.workhorse.jobengine.boundary;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.framework.listing.boundary.Listing;
import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingResult;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobLog;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

/**
 * Provides access to the job logs
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobLogService {

    private final Logger logger = LoggerFactory.getLogger(JobLogService.class);

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @Inject
    JobEngineService jobEngineService;

    public ListingResult<JobLog> listJobLogs(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, JobLog.class, listingParameters);
    }

    public JobLog getJobLog(Long id) {
        return entityManager.find(JobLog.class, id);
    }

    public void logChange(Long jobId, JobStatus status, String changeParameter, String changeOld, String changeNew, String message) {

        JobLog jobLog = new JobLog();
        jobLog.setJobId(jobId);
        jobLog.setStatus(status);
        jobLog.setChangeParameter(changeParameter);
        jobLog.setChangeOld(changeOld);
        jobLog.setChangeNew(changeNew);
        jobLog.setMessage(message);

        entityManager.persist(jobLog);
        logger.info("Logged change: {}", jobLog);
    }

    @Asynchronous
    public void logException(Long jobId, JobStatus status, Exception exception, String message) {

        JobLog jobLog = new JobLog();
        jobLog.setStatus(status);
        jobLog.setMessage(message != null ? message : exception.getMessage());
        jobLog.setStacktrace(JobEngineUtil.stacktraceToString(exception));

        entityManager.persist(jobLog);
        logger.info("Logged exception: {}", jobLog);
    }

    public JobLog logMessage(Long jobId, String message) {

        Job job = jobEngineService.getJobById(jobId);

        JobLog jobLog = new JobLog();
        jobLog.setJobId(job.getId());
        jobLog.setStatus(job.getStatus());
        jobLog.setMessage(message);

        entityManager.persist(jobLog);
        logger.info("Logged message: {}", jobLog);

        return jobLog;
    }

    public int deleteAllByJobId(Long jobId) {
        return JobLog.deleteAllByJobId(entityManager, jobId);
    }

}
