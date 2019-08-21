package io.coodoo.workhorse.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingResult;
import io.coodoo.workhorse.api.dto.GroupInfoDTO;
import io.coodoo.workhorse.api.dto.JobCountViewDTO;
import io.coodoo.workhorse.api.dto.JobDTO;
import io.coodoo.workhorse.api.dto.JobEngineInfoDTO;
import io.coodoo.workhorse.api.dto.JobExecutionCountsDTO;
import io.coodoo.workhorse.api.dto.JobExecutionDTO;
import io.coodoo.workhorse.api.dto.JobExecutionViewDTO;
import io.coodoo.workhorse.api.dto.JobScheduleExecutionTimeDTO;
import io.coodoo.workhorse.api.dto.JobStatisticDTO;
import io.coodoo.workhorse.api.dto.JobStatusCountsDTO;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.boundary.JobStatisticService;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobCountView;
import io.coodoo.workhorse.jobengine.entity.JobEngineInfo;
import io.coodoo.workhorse.jobengine.entity.JobExecutionView;
import io.coodoo.workhorse.jobengine.entity.JobStatus;

/**
 * Rest interface to the workhorse
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Path("/workhorse")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JobEngineResource {

    @Inject
    JobEngineService jobEngineService;

    @Inject
    JobStatisticService jobStatisticService;

    @GET
    @Path("/infos")
    public List<JobEngineInfoDTO> getJobEngineInfos() {

        return jobEngineService.getAllJobs().stream().map(job -> new JobEngineInfoDTO(jobEngineService.getJobEngineInfo(job.getId())))
                        .collect(Collectors.toList());
    }

    @GET
    @Path("/infos/{jobId}")
    public JobEngineInfoDTO getJobEngineInfo(@PathParam("jobId") Long jobId) {

        JobEngineInfo jobEngineInfo = jobEngineService.getJobEngineInfo(jobId);

        return new JobEngineInfoDTO(jobEngineInfo);
    }

    @GET
    @Path("/start")
    public Response start(@QueryParam("interval") Integer interval) {

        if (interval != null) {
            JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL = interval;
        }

        jobEngineService.start();
        return Response.ok().build();
    }

    @GET
    @Path("/stop")
    public Response stop() {
        jobEngineService.stop();
        return Response.ok().build();
    }

    @GET
    @Path("/is-running")
    public Response isRunning() {
        return Response.ok(jobEngineService.isRunning()).build();
    }

    @GET
    @Path("/get-memory-counts/{jobId}")
    public Object[] getMemoryCounts(@PathParam("jobId") Long jobId) {

        return jobStatisticService.getMemoryCounts(jobId);
    }

    @GET
    @Path("/job-status-counts")
    public JobStatusCountsDTO getJobStatusCounts() {

        JobStatusCountsDTO counts = new JobStatusCountsDTO();
        counts.active = jobEngineService.countJobsByStatus(JobStatus.ACTIVE);
        counts.inactive = jobEngineService.countJobsByStatus(JobStatus.INACTIVE);
        counts.error = jobEngineService.countJobsByStatus(JobStatus.ERROR);
        counts.noWorker = jobEngineService.countJobsByStatus(JobStatus.NO_WORKER);
        counts.total = counts.active + counts.inactive + counts.error + counts.noWorker;
        return counts;
    }

    @GET
    @Path("/jobs")
    public ListingResult<JobDTO> getJobs(@BeanParam ListingParameters listingParameters) {

        ListingResult<Job> jobsListing = jobEngineService.listJobs(listingParameters);
        List<JobDTO> results = jobsListing.getResults().stream().map(job -> new JobDTO(job, getMemoryCounts(job.getId()))).collect(Collectors.toList());

        return new ListingResult<JobDTO>(results, jobsListing.getMetadata());
    }

    @GET
    @Path("/jobs-count")
    public ListingResult<JobCountViewDTO> getJobsWithCounts(@BeanParam ListingParameters listingParameters) {

        ListingResult<JobCountView> jobsListing = jobEngineService.listJobsWithCounts(listingParameters);
        List<JobCountViewDTO> results = jobsListing.getResults().stream().map(JobCountViewDTO::new).collect(Collectors.toList());

        return new ListingResult<JobCountViewDTO>(results, jobsListing.getMetadata());
    }

    @GET
    @Path("/executions/{jobExecutionId}")
    public JobExecutionDTO getJobExecution(@PathParam("jobExecutionId") Long jobExecutionId) {
        return new JobExecutionDTO(jobEngineService.getJobExecutionById(jobExecutionId));
    }

    @GET
    @Path("/jobs/{jobId}/execution-views")
    public ListingResult<JobExecutionViewDTO> getExecutionViews(@PathParam("jobId") Long jobId, @BeanParam ListingParameters listingParameters) {

        if (jobId != null && jobId > 0) {
            listingParameters.addFilterAttributes("jobId", jobId.toString());
        }
        ListingResult<JobExecutionView> jobListing = jobEngineService.listJobExecutionViews(listingParameters);
        List<JobExecutionViewDTO> results = jobListing.getResults().stream().map(JobExecutionViewDTO::new).collect(Collectors.toList());

        return new ListingResult<JobExecutionViewDTO>(results, jobListing.getMetadata());
    }

    @GET
    @Path("/jobs/{jobId}")
    public JobDTO getJob(@PathParam("jobId") Long jobId) {
        return new JobDTO(jobEngineService.getJobById(jobId));
    }

    @GET
    @Path("/jobs/{jobId}/execution-counts/{minutes}")
    public JobExecutionCountsDTO getJobExecutionCountByJob(@PathParam("jobId") Long jobId, @PathParam("minutes") Integer minutes) {
        return new JobExecutionCountsDTO(jobEngineService.getJobExecutionCounts(jobId, minutes));
    }

    @PUT
    @Path("/jobs/{jobId}")
    public JobDTO updateJob(@PathParam("jobId") Long jobId, JobDTO jobDto) {

        Job job = jobEngineService.updateJob(jobId, jobDto.name, jobDto.description, jobDto.tags, jobDto.workerClassName, jobDto.schedule, jobDto.status,
                        jobDto.threads, jobDto.maxPerMinute, jobDto.failRetries, jobDto.retryDelay, jobDto.daysUntilCleanUp, jobDto.uniqueInQueue);

        return new JobDTO(job);
    }

    @DELETE
    @Path("/jobs/{jobId}")
    public void deleteJob(@PathParam("jobId") Long jobId) {
        jobEngineService.deleteJob(jobId);
    }

    @GET
    @Path("/jobs/{jobId}/activate")
    public void activateJob(@PathParam("jobId") Long jobId) {
        jobEngineService.activateJob(jobId);
    }

    @GET
    @Path("/jobs/{jobId}/deactivate")
    public void deactivateJob(@PathParam("jobId") Long jobId) {
        jobEngineService.deactivateJob(jobId);
    }

    @PUT
    @Path("/jobs/{jobId}/clear-memory-queue")
    public void clearMemoryQueue(@PathParam("jobId") Long jobId) {
        jobEngineService.clearMemoryQueue(jobId);
    }

    @GET
    @Path("/jobs/{jobId}/executions/{jobExecutionId}")
    public JobExecutionDTO getJobExecution(@PathParam("jobId") Long jobId, @PathParam("jobExecutionId") Long jobExecutionId) {
        return new JobExecutionDTO(jobEngineService.getJobExecutionById(jobExecutionId));
    }

    @POST
    @Path("/jobs/{jobId}/executions")
    public JobExecutionDTO createJobExecution(@PathParam("jobId") Long jobId, JobExecutionDTO jobExecutionDto) {
        return new JobExecutionDTO(jobEngineService.createJobExecution(jobId, jobExecutionDto.parameters, jobExecutionDto.priority, jobExecutionDto.maturity,
                        jobExecutionDto.batchId, jobExecutionDto.chainId, jobExecutionDto.chainPreviousExecutionId, false));
    }

    @PUT
    @Path("/jobs/{jobId}/executions/{jobExecutionId}")
    public JobExecutionDTO updateJobExecution(@PathParam("jobId") Long jobId, @PathParam("jobExecutionId") Long jobExecutionId,
                    JobExecutionDTO jobExecutionDto) {
        return new JobExecutionDTO(jobEngineService.updateJobExecution(jobExecutionDto.id, jobExecutionDto.status, jobExecutionDto.parameters,
                        jobExecutionDto.priority, jobExecutionDto.maturity, jobExecutionDto.failRetry));
    }

    @DELETE
    @Path("/jobs/{jobId}/executions/{jobExecutionId}")
    public void deleteJobExecution(@PathParam("jobId") Long jobId, @PathParam("jobExecutionId") Long jobExecutionId) {
        jobEngineService.deleteJobExecution(jobExecutionId);
    }

    @GET
    @Path("/jobs/{jobId}/batch/{batchId}")
    public GroupInfoDTO getBatchInfo(@PathParam("jobId") Long jobId, @PathParam("batchId") Long batchId) {
        return new GroupInfoDTO(jobEngineService.getJobExecutionBatchInfo(batchId));
    }

    @GET
    @Path("/jobs/{jobId}/batch/{batchId}/executions")
    public List<JobExecutionDTO> getBatchJobExecutions(@PathParam("jobId") Long jobId, @PathParam("batchId") Long batchId) {
        return jobEngineService.getJobExecutionBatch(batchId).stream().map(JobExecutionDTO::new).collect(Collectors.toList());
    }

    @GET
    @Path("/jobs/{jobId}/chain/{chainId}")
    public GroupInfoDTO getChainInfo(@PathParam("jobId") Long jobId, @PathParam("chainId") Long chainId) {
        return new GroupInfoDTO(jobEngineService.getJobExecutionChainInfo(chainId));
    }

    @GET
    @Path("/jobs/{jobId}/chain/{chainId}/executions")
    public List<JobExecutionDTO> getChainJobExecutions(@PathParam("jobId") Long jobId, @PathParam("chainId") Long chainId) {
        return jobEngineService.getJobExecutionChain(chainId).stream().map(JobExecutionDTO::new).collect(Collectors.toList());
    }

    @POST
    @Path("/jobs/{jobId}/scheduled-job-execution")
    public JobDTO scheduledJobExecutionCreation(@PathParam("jobId") Long jobId, JobDTO jobDto) throws Exception {

        jobEngineService.triggerScheduledJobExecutionCreation(jobEngineService.getJobById(jobId));
        return jobDto;
    }

    @GET
    @Path("/jobs/next-scheduled-times")
    public List<LocalDateTime> getNextScheduledTimes(@QueryParam("schedule") String schedule, @QueryParam("times") Integer times,
                    @QueryParam("start") String start) {

        Integer scheduleTimes = times != null ? times : 5;
        LocalDateTime startTime = start != null ? LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME) : null;

        return jobEngineService.getNextScheduledTimes(schedule, scheduleTimes, startTime);
    }

    @GET
    @Path("/jobs/schedule-executions")
    public List<JobScheduleExecutionTimeDTO> getAllScheduleExecutionTimes(@QueryParam("start") String start, @QueryParam("end") String end) {

        LocalDateTime startTime = start != null ? LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime endTime = end != null ? LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME) : null;

        List<JobScheduleExecutionTimeDTO> scheduledTimes = new ArrayList<>();
        for (Job job : jobEngineService.getAllScheduledJobs()) {
            try {
                JobScheduleExecutionTimeDTO dto = new JobScheduleExecutionTimeDTO();
                dto.jobId = job.getId();
                dto.jobName = job.getName();
                dto.schedule = job.getSchedule();
                dto.executions = jobEngineService.getScheduledTimes(job.getSchedule(), startTime, endTime);
                scheduledTimes.add(dto);
            } catch (RuntimeException e) {
            }
        }
        return scheduledTimes;
    }

    @GET
    @Path("/statistics")
    public List<JobStatisticDTO> getJobStatistics(@BeanParam ListingParameters listingParameters) {

        return jobStatisticService.listJobStatistics(listingParameters).stream().map(JobStatisticDTO::new).collect(Collectors.toList());
    }

}
