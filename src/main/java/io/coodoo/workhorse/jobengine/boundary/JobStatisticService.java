package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.framework.listing.boundary.Listing;
import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.JobEngine;
import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.control.MemoryCount;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobStatistic;

/**
 * Provides access to statistics data
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobStatisticService {

    private final Logger logger = LoggerFactory.getLogger(JobStatisticService.class);

    private Map<Long, MemoryCount> memoryCounts = new ConcurrentHashMap<>();

    @Inject
    JobEngine jobEngine;

    @Inject
    JobEngineService jobEngineService;

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        for (Job job : jobEngineService.getAllJobs()) {
            memoryCounts.put(job.getId(), new MemoryCount());
        }
    }

    @Schedule(hour = "*", minute = "*", persistent = false) // every minute
    public void iterateMemoryCount() {

        if (jobEngineService.isRunning()) {
            for (Map.Entry<Long, MemoryCount> entry : memoryCounts.entrySet()) {

                Long jobId = entry.getKey();
                JobStatistic jobStatistic = entry.getValue().collectAndIterate(jobId, jobEngine.getNumberOfJobExecutionsInQueue(jobId));

                if (jobStatistic != null) {
                    entityManager.persist(jobStatistic);
                }
            }
        }
    }

    public void recordFinished(Long jobId, Long jobExecutionId, long duration) {
        memoryCounts.get(jobId).incrementFinished(jobExecutionId, duration);
    }

    public void recordFailed(Long jobId, Long jobExecutionId, long duration) {
        memoryCounts.get(jobId).incrementFailed(jobExecutionId, duration);
    }

    public void recordTrigger(Long jobId) {
        memoryCounts.get(jobId).incrementScheduleTriggers();
    }

    public Map<Long, MemoryCount> getMemoryCounts() {
        return memoryCounts;
    }

    public List<JobStatistic> listJobStatistics(ListingParameters listingParameters) {
        return Listing.getListing(entityManager, JobStatistic.class, listingParameters);
    }

    public int deleteAllByJobId(Long jobId) {
        return JobStatistic.deleteAllByJobId(entityManager, jobId);
    }

    public Object[] getMemoryCounts(Long jobId) {

        if (memoryCounts != null && memoryCounts.containsKey(jobId)) {

            MemoryCount memoryCount = memoryCounts.get(jobId);

            Object[] data = new Object[memoryCount.size];
            for (int i = 0; i < memoryCount.size; i++) {

                LocalDateTime time = memoryCount.time[i];
                if (time == null) {
                    time = JobEngineUtil.timestamp();
                }
                data[i] = new Object[] {time, memoryCount.queued[i], memoryCount.finished[i], memoryCount.failed[i]};
            }
            return data;
        }
        return null;
    }

}
