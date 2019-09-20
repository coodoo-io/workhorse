package io.coodoo.workhorse.api.boundary;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.statistic.boundary.JobEngineStatisticService;
import io.coodoo.workhorse.statistic.entity.DurationHeatmap;
import io.coodoo.workhorse.statistic.entity.JobStatisticDay;
import io.coodoo.workhorse.statistic.entity.JobStatisticHour;
import io.coodoo.workhorse.statistic.entity.JobStatisticMinute;
import io.coodoo.workhorse.statistic.entity.MemoryCountData;

/**
 * Rest interface for the workhorse statistics
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/statistics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JobEngineStatisticResource {

    @Inject
    JobEngineStatisticService jobEngineStatisticService;

    @Inject
    JobEngineService jobEngineService;

    @GET
    @Path("/minutes")
    public List<JobStatisticMinute> getJobStatisticsMinutes(@BeanParam ListingParameters listingParameters) {
        return jobEngineStatisticService.listJobStatisticMinutes(listingParameters);
    }

    @GET
    @Path("/hours")
    public List<JobStatisticHour> getJobStatisticsHours(@BeanParam ListingParameters listingParameters) {
        return jobEngineStatisticService.listJobStatisticHours(listingParameters);
    }

    @GET
    @Path("/days")
    public List<JobStatisticDay> getJobStatisticsDays(@BeanParam ListingParameters listingParameters) {
        return jobEngineStatisticService.listJobStatisticDays(listingParameters);
    }

    @GET
    @Path("/duration-heatmap")
    public List<DurationHeatmap> getDurationHeatmap() {
        return jobEngineStatisticService.getDurationHeatmap(jobEngineService.getAllJobs().stream().map(Job::getId).collect(Collectors.toList()));
    }

    @GET
    @Path("/duration-heatmap/{jobId}")
    public List<DurationHeatmap> getDurationHeatmap(@PathParam("jobId") Long jobId) {
        return jobEngineStatisticService.getDurationHeatmap(Arrays.asList(jobId));
    }

    @GET
    @Path("/memory-counts/{jobId}")
    public List<MemoryCountData> getMemoryCounts(@PathParam("jobId") Long jobId) {
        return jobEngineStatisticService.getMemoryCounts(jobId);
    }
}
