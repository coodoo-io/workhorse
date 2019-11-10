package io.coodoo.workhorse.statistic.boundary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
import io.coodoo.framework.listing.control.ListingConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.JobEngine;
import io.coodoo.workhorse.jobengine.control.MemoryCount;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.statistic.entity.DurationHeatmap;
import io.coodoo.workhorse.statistic.entity.DurationHeatmapDetail;
import io.coodoo.workhorse.statistic.entity.JobStatisticDay;
import io.coodoo.workhorse.statistic.entity.JobStatisticHour;
import io.coodoo.workhorse.statistic.entity.JobStatisticMinute;
import io.coodoo.workhorse.statistic.entity.MemoryCountData;
import io.coodoo.workhorse.util.JobEngineUtil;

/**
 * Provides access to statistics data
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobEngineStatisticService {

    private final Logger logger = LoggerFactory.getLogger(JobEngineStatisticService.class);

    private static final DateTimeFormatter DURATION_HEATMAP_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
            initMemoryCount(job.getId());
        }
    }

    public void initMemoryCount(Long jobId) {
        memoryCounts.put(jobId, new MemoryCount());
    }

    @Schedule(hour = "*", minute = "*", persistent = false) // every minute
    public void iterateMemoryCount() {

        if (jobEngineService.isRunning()) {
            for (Map.Entry<Long, MemoryCount> entry : memoryCounts.entrySet()) {

                Long jobId = entry.getKey();
                JobStatisticMinute jobStatisticMinute = entry.getValue().collectAndIterate(jobId, jobEngine.getNumberOfJobExecutionsInQueue(jobId));

                if (jobStatisticMinute != null) {

                    entityManager.persist(jobStatisticMinute);

                    LocalDateTime hourFrom = jobStatisticMinute.getTo().withMinute(0).withSecond(0);
                    LocalDateTime hourTo = jobStatisticMinute.getTo().withMinute(59).withSecond(59);
                    JobStatisticHour jobStatisticHour = JobStatisticHour.findLatestByJobId(entityManager, jobId);
                    if (jobStatisticHour == null || jobStatisticHour.getTo().getHour() < jobStatisticMinute.getTo().getHour()) {
                        entityManager.persist(new JobStatisticHour(jobStatisticMinute, hourFrom, hourTo));
                    } else {
                        jobStatisticHour.update(JobStatisticMinute.summaryByJobId(entityManager, jobId, hourFrom, hourTo));
                    }

                    LocalDateTime dayFrom = jobStatisticMinute.getTo().withHour(0).withMinute(0).withSecond(0);
                    LocalDateTime dayTo = jobStatisticMinute.getTo().withHour(23).withMinute(59).withSecond(59);
                    JobStatisticDay jobStatisticDay = JobStatisticDay.findLatestByJobId(entityManager, jobId);
                    if (jobStatisticDay == null || jobStatisticDay.getTo().getDayOfMonth() < jobStatisticMinute.getTo().getDayOfMonth()) {
                        entityManager.persist(new JobStatisticDay(jobStatisticMinute, dayFrom, dayTo));
                    } else {
                        jobStatisticDay.update(JobStatisticMinute.summaryByJobId(entityManager, jobId, dayFrom, dayTo));
                    }
                }
            }
        }
    }

    @Schedule(hour = "12", minute = "0", persistent = false) // every day at noon
    public void highNoon() {

        if (JobEngineConfig.DAYS_UNTIL_STATISTIC_MINUTES_DELETION > 0) {
            LocalDateTime date = JobEngineUtil.timestamp().minusDays(JobEngineConfig.DAYS_UNTIL_STATISTIC_MINUTES_DELETION).minusHours(6);
            int deleted = JobStatisticMinute.deleteOlderThanDate(entityManager, date);
            logger.info("Deleted {} minute by minute statistic records older than {} days", deleted, JobEngineConfig.DAYS_UNTIL_STATISTIC_MINUTES_DELETION);
        }
        if (JobEngineConfig.DAYS_UNTIL_STATISTIC_HOURS_DELETION > 0) {
            LocalDateTime date = JobEngineUtil.timestamp().minusDays(JobEngineConfig.DAYS_UNTIL_STATISTIC_HOURS_DELETION).minusHours(6);
            int deleted = JobStatisticHour.deleteOlderThanDate(entityManager, date);
            logger.info("Deleted {} hourly statistic records older than {} days", deleted, JobEngineConfig.DAYS_UNTIL_STATISTIC_HOURS_DELETION);
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

    public List<JobStatisticMinute> listJobStatisticMinutes(ListingParameters listingParameters) {
        return Listing.getListing(entityManager, JobStatisticMinute.class, listingParameters);
    }

    public List<JobStatisticHour> listJobStatisticHours(ListingParameters listingParameters) {
        return Listing.getListing(entityManager, JobStatisticHour.class, listingParameters);
    }

    public List<JobStatisticDay> listJobStatisticDays(ListingParameters listingParameters) {
        return Listing.getListing(entityManager, JobStatisticDay.class, listingParameters);
    }

    /**
     * see https://www.npmjs.com/package/angular2-calendar-heatmap
     */
    public List<DurationHeatmap> getDurationHeatmap(List<Long> jobIds) {

        List<DurationHeatmap> result = new ArrayList<>();
        Map<Long, String> jobNames = new HashMap<>();

        for (Long jobId : jobIds) {
            Job job = jobEngineService.getJobById(jobId);
            jobNames.put(jobId, job.getName());
        }

        LocalDateTime dateMinutes = JobEngineUtil.timestamp().withHour(0).withMinute(0).withSecond(0);
        long millisMinutes = StatisticsUtil.toEpochMilli(dateMinutes);
        LocalDateTime dateHours = dateMinutes.minusDays(7);
        long millisHours = StatisticsUtil.toEpochMilli(dateHours);

        String jobIdFilter = jobIds.stream().map(jobId -> jobId.toString()).collect(Collectors.joining(ListingConfig.OPERATOR_OR));
        String fromNotNullFilter = ListingConfig.OPERATOR_NOT + " " + ListingConfig.OPERATOR_NULL;

        // first day in minutes
        {
            String fromFilter = ListingConfig.OPERATOR_GT + millisMinutes;

            ListingParameters listingParameters = new ListingParameters(1, 0, "from");
            listingParameters.addFilterAttributes("jobId", jobIdFilter);
            listingParameters.addFilterAttributes("from", fromFilter);
            listingParameters.addFilterAttributes("durationSum", fromNotNullFilter);
            List<JobStatisticMinute> jobStatisticMinutes = listJobStatisticMinutes(listingParameters);

            DurationHeatmap durationHeatmap = new DurationHeatmap();
            durationHeatmap.setDate(dateMinutes.format(DURATION_HEATMAP_DATE_FORMATTER));

            for (JobStatisticMinute jobStatisticMinute : jobStatisticMinutes) {

                DurationHeatmapDetail durationHeatmapDetail = new DurationHeatmapDetail();
                durationHeatmapDetail.setName(jobNames.get(jobStatisticMinute.getJobId()));
                durationHeatmapDetail.setDate(jobStatisticMinute.getFrom());
                durationHeatmapDetail.setValue(jobStatisticMinute.getDurationSum() / 1000);
                durationHeatmap.getDetails().add(durationHeatmapDetail);

                durationHeatmap.setTotal(durationHeatmap.getTotal() + jobStatisticMinute.getDurationSum());
            }
            durationHeatmap.setTotal(durationHeatmap.getTotal() / 1000);
            result.add(durationHeatmap);
        }
        // first week in hours
        {
            String fromFilter = millisHours + ListingConfig.OPERATOR_TO + millisMinutes;

            ListingParameters listingParameters = new ListingParameters(1, 0, "from");
            listingParameters.addFilterAttributes("jobId", jobIdFilter);
            listingParameters.addFilterAttributes("from", fromFilter);
            listingParameters.addFilterAttributes("durationSum", fromNotNullFilter);
            List<JobStatisticHour> jobStatisticHours = listJobStatisticHours(listingParameters);

            Map<String, List<JobStatisticHour>> days = jobStatisticHours.stream().collect(
                            Collectors.groupingBy(s -> s.getFrom().format(DURATION_HEATMAP_DATE_FORMATTER), Collectors.mapping(s -> s, Collectors.toList())));

            for (Map.Entry<String, List<JobStatisticHour>> entry : days.entrySet()) {

                DurationHeatmap durationHeatmap = new DurationHeatmap();
                durationHeatmap.setDate(entry.getKey());

                for (JobStatisticHour jobStatisticHour : entry.getValue()) {

                    DurationHeatmapDetail durationHeatmapDetail = new DurationHeatmapDetail();
                    durationHeatmapDetail.setName(jobNames.get(jobStatisticHour.getJobId()));
                    durationHeatmapDetail.setDate(jobStatisticHour.getFrom());
                    durationHeatmapDetail.setValue(jobStatisticHour.getDurationSum() / 1000);
                    durationHeatmap.getDetails().add(durationHeatmapDetail);

                    durationHeatmap.setTotal(durationHeatmap.getTotal() + jobStatisticHour.getDurationSum());
                }
                durationHeatmap.setTotal(durationHeatmap.getTotal() / 1000);
                result.add(durationHeatmap);
            }
        }
        // all other days
        {
            String fromFilter = ListingConfig.OPERATOR_LT + millisHours;

            ListingParameters listingParameters = new ListingParameters(1, 0, "from");
            listingParameters.addFilterAttributes("jobId", jobIdFilter);
            listingParameters.addFilterAttributes("from", fromFilter);
            listingParameters.addFilterAttributes("durationSum", fromNotNullFilter);
            List<JobStatisticDay> jobStatisticDays = listJobStatisticDays(listingParameters);

            Map<String, List<JobStatisticDay>> days = jobStatisticDays.stream().collect(
                            Collectors.groupingBy(s -> s.getFrom().format(DURATION_HEATMAP_DATE_FORMATTER), Collectors.mapping(s -> s, Collectors.toList())));

            for (Map.Entry<String, List<JobStatisticDay>> entry : days.entrySet()) {

                DurationHeatmap durationHeatmap = new DurationHeatmap();
                durationHeatmap.setDate(entry.getKey());

                for (JobStatisticDay jobStatisticDay : entry.getValue()) {

                    DurationHeatmapDetail durationHeatmapDetail = new DurationHeatmapDetail();
                    durationHeatmapDetail.setName(jobNames.get(jobStatisticDay.getJobId()));
                    durationHeatmapDetail.setDate(jobStatisticDay.getFrom());
                    durationHeatmapDetail.setValue(jobStatisticDay.getDurationSum() / 1000);
                    durationHeatmap.getDetails().add(durationHeatmapDetail);

                    durationHeatmap.setTotal(durationHeatmap.getTotal() + jobStatisticDay.getDurationSum());
                }
                durationHeatmap.setTotal(durationHeatmap.getTotal() / 1000);
                result.add(durationHeatmap);
            }
        }
        return result;
    }

    public int deleteAllByJobId(Long jobId) {

        int deleted = 0;
        deleted += JobStatisticMinute.deleteAllByJobId(entityManager, jobId);
        deleted += JobStatisticHour.deleteAllByJobId(entityManager, jobId);
        deleted += JobStatisticDay.deleteAllByJobId(entityManager, jobId);
        return deleted;
    }

    public List<MemoryCountData> getMemoryCounts(Long jobId) {

        if (memoryCounts != null && memoryCounts.containsKey(jobId)) {

            MemoryCount memoryCount = memoryCounts.get(jobId);

            List<MemoryCountData> data = new ArrayList<>();
            for (int i = 0; i < memoryCount.size; i++) {

                LocalDateTime time = memoryCount.time[i];
                if (time == null) {
                    time = JobEngineUtil.timestamp();
                }
                data.add(new MemoryCountData(time, memoryCount.queued[i].get(), memoryCount.finished[i].get(), memoryCount.failed[i].get()));
            }
            Collections.sort(data, Comparator.comparing(MemoryCountData::getTime));
            return data;
        }
        return null;
    }

}
