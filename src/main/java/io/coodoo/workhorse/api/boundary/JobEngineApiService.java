package io.coodoo.workhorse.api.boundary;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.BeanParam;

import io.coodoo.framework.listing.boundary.Listing;
import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingResult;
import io.coodoo.workhorse.api.entity.JobCountView;
import io.coodoo.workhorse.api.entity.JobExecutionCounts;
import io.coodoo.workhorse.api.entity.JobExecutionView;
import io.coodoo.workhorse.api.entity.LogView;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.entity.Job;

/**
 * Provides functionality for the REST-API only
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineApiService {

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    public ListingResult<Job> listJobs(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, Job.class, listingParameters);
    }

    public ListingResult<JobCountView> listJobsWithCounts(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, JobCountView.class, listingParameters);
    }

    public ListingResult<JobExecutionView> listJobExecutionViews(ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, JobExecutionView.class, listingParameters);
    }

    public JobExecutionCounts getJobExecutionCounts(Long jobId, Integer consideredLastMinutes) {
        return JobExecutionCounts.query(entityManager, jobId, consideredLastMinutes);
    }

    public ListingResult<LogView> getLogViews(@BeanParam ListingParameters listingParameters) {
        return Listing.getListingResult(entityManager, LogView.class, listingParameters);
    }
}
