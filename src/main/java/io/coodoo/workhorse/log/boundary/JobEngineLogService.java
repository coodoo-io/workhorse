package io.coodoo.workhorse.log.boundary;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.framework.listing.boundary.Listing;
import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingResult;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.log.entity.Log;
import io.coodoo.workhorse.util.JobEngineUtil;

/**
 * Provides access to the job logs
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineLogService {

    private final Logger logger = LoggerFactory.getLogger(JobEngineLogService.class);

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @Inject
    JobEngineService jobEngineService;

    public ListingResult<Log> listLogs(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, Log.class, listingParameters);
    }

    public Log getLog(Long id) {
        return entityManager.find(Log.class, id);
    }

    public Log logChange(Long jobId, JobStatus jobStatus, String changeParameter, Object changeOld, Object changeNew, String message) {

        String co = changeOld == null ? "" : changeOld.toString();
        String cn = changeNew == null ? "" : changeNew.toString();

        if (message == null) {
            message = String.format(JobEngineConfig.LOG_CHANGE, changeParameter, co, cn);
        }
        return createLog(message, jobId, jobStatus, true, changeParameter, co, cn, null);
    }

    @Asynchronous
    public void logException(Exception exception, String message, Long jobId, JobStatus jobStatus) {
        createLog(message != null ? message : JobEngineUtil.getMessagesFromException(exception), jobId, jobStatus, false, null, null, null,
                        JobEngineUtil.stacktraceToString(exception));
    }

    /**
     * Logs a text message
     * 
     * @param message text to log
     * @param jobId optional: belonging {@link Job}-ID
     * @param byUser <code>true</code> if author is a user, <code>false</code> if author is the system
     * @return the resulting log entry
     */
    public Log logMessage(String message, Long jobId, boolean byUser) {

        JobStatus jobStatus = null;
        if (jobId != null) {
            Job job = jobEngineService.getJobById(jobId);
            if (job != null) {
                jobStatus = job.getStatus();
            }
        }
        return createLog(message, jobId, jobStatus, byUser, null, null, null, null);
    }

    public Log createLog(String message, Long jobId, JobStatus jobStatus, boolean byUser, String changeParameter, String changeOld, String changeNew,
                    String stacktrace) {

        Log log = new Log();
        log.setMessage(message);
        log.setJobId(jobId);
        log.setJobStatus(jobStatus);
        log.setByUser(byUser);
        log.setChangeParameter(changeParameter);
        log.setChangeOld(changeOld);
        log.setChangeNew(changeNew);
        log.setHostName(JobEngineUtil.getHostName());
        log.setStacktrace(stacktrace);

        entityManager.persist(log);
        logger.info("Created: {}", log);
        return log;
    }

    public int deleteAllByJobId(Long jobId) {
        return Log.deleteAllByJobId(entityManager, jobId);
    }

}
