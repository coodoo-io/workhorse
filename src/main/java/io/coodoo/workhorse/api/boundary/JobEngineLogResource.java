package io.coodoo.workhorse.api.boundary;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingResult;
import io.coodoo.workhorse.api.entity.LogView;
import io.coodoo.workhorse.log.boundary.JobEngineLogService;
import io.coodoo.workhorse.log.entity.Log;

/**
 * Rest interface for the workhorse logs
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse/logs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JobEngineLogResource {

    @Inject
    JobEngineLogService jobEngineLogService;

    @Inject
    JobEngineApiService jobEngineApiService;

    @GET
    @Path("/")
    public ListingResult<Log> getLogs(@BeanParam ListingParameters listingParameters) {
        return jobEngineLogService.listLogs(listingParameters);
    }

    @GET
    @Path("/view")
    public ListingResult<LogView> getLogViews(@BeanParam ListingParameters listingParameters) {
        return jobEngineApiService.getLogViews(listingParameters);
    }

    @GET
    @Path("/{id}")
    public Log getLog(@PathParam("id") Long id) {
        return jobEngineLogService.getLog(id);
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/")
    public Log createLogMessage(String message) {
        return jobEngineLogService.logMessage(message, null, true);
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{jobId}")
    public Log createLogMessage(@PathParam("jobId") Long jobId, String message) {
        return jobEngineLogService.logMessage(message, jobId, true);
    }

}
