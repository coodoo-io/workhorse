package io.coodoo.workhorse.api.entity;

import javax.persistence.EntityManager;

import io.coodoo.framework.listing.boundary.Listing;
import io.coodoo.framework.listing.boundary.ListingParameters;
import io.coodoo.framework.listing.boundary.ListingPredicate;
import io.coodoo.framework.listing.boundary.Stats;
import io.coodoo.framework.listing.boundary.Term;
import io.coodoo.framework.listing.control.ListingConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.util.JobEngineUtil;

public class JobExecutionCounts {

    private Long total = 0L;
    private Long queued = 0L;
    private Long running = 0L;
    private Long finished = 0L;
    private Long failed = 0L;
    private Long aborted = 0L;
    private Long averageDuration;
    private Long minDuration;
    private Long maxDuration;
    private Long sumDuration;

    /**
     * Queries the current counts of {@link JobExecution} status for one specific or all jobs
     * 
     * @param entityManager persistence
     * @param jobId optional, <code>null</code> will get the counts for all
     * @param consideredLastMinutes optional, this query is expensive, so make sure to limit the timeframe! If <code>null</code> a default of 60 minutes is
     *        given.
     * @return fresh counts!
     */
    public static JobExecutionCounts query(EntityManager entityManager, Long jobId, Integer consideredLastMinutes) {

        int minutes = consideredLastMinutes == null ? 60 : consideredLastMinutes;
        String timeFilter = ListingConfig.OPERATOR_GT
                        + JobEngineUtil.timestamp().minusMinutes(minutes).atZone(JobEngineConfig.TIME_ZONE).toInstant().toEpochMilli();

        ListingParameters listingParameters = new ListingParameters();
        if (jobId != null) {
            listingParameters.addFilterAttributes("jobId", jobId.toString());
        }

        ListingPredicate filter = new ListingPredicate().or();
        filter.addPredicate(new ListingPredicate().filter("createdAt", timeFilter));
        filter.addPredicate(new ListingPredicate().filter("startedAt", timeFilter));
        filter.addPredicate(new ListingPredicate().filter("endedAt", timeFilter));

        listingParameters.setPredicate(filter);
        listingParameters.addTermsAttributes("status", "5"); // there are only five status
        listingParameters.addStatsAttributes("duration", "all");

        JobExecutionCounts counts = new JobExecutionCounts();
        for (Term term : Listing.getTerms(entityManager, JobExecution.class, listingParameters).get("status")) {
            switch ((JobExecutionStatus) term.getValue()) {
                case QUEUED:
                    counts.setQueued(term.getCount());
                    break;
                case RUNNING:
                    counts.setRunning(term.getCount());
                    break;
                case FINISHED:
                    counts.setFinished(term.getCount());
                    break;
                case FAILED:
                    counts.setFailed(term.getCount());
                    break;
                case ABORTED:
                    counts.setAborted(term.getCount());
                    break;
            }
        }
        Stats stats = Listing.getStats(entityManager, JobExecution.class, listingParameters).get("duration");
        counts.setTotal(stats.getCount());
        if (stats.getAvg() != null) {
            counts.setAverageDuration(stats.getAvg().longValue());
        }
        if (stats.getMin() != null) {
            counts.setMinDuration(stats.getMin().longValue());
        }
        if (stats.getMax() != null) {
            counts.setMaxDuration(stats.getMax().longValue());
        }
        if (stats.getSum() != null) {
            counts.setSumDuration(stats.getSum().longValue());
        }
        return counts;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getQueued() {
        return queued;
    }

    public void setQueued(Long queued) {
        this.queued = queued;
    }

    public Long getRunning() {
        return running;
    }

    public void setRunning(Long running) {
        this.running = running;
    }

    public Long getFinished() {
        return finished;
    }

    public void setFinished(Long finished) {
        this.finished = finished;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getAborted() {
        return aborted;
    }

    public void setAborted(Long aborted) {
        this.aborted = aborted;
    }

    public Long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(Long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public Long getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Long minDuration) {
        this.minDuration = minDuration;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Long getSumDuration() {
        return sumDuration;
    }

    public void setSumDuration(Long sumDuration) {
        this.sumDuration = sumDuration;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobExecutionCounts [total=");
        builder.append(total);
        builder.append(", queued=");
        builder.append(queued);
        builder.append(", running=");
        builder.append(running);
        builder.append(", finished=");
        builder.append(finished);
        builder.append(", failed=");
        builder.append(failed);
        builder.append(", aborted=");
        builder.append(aborted);
        builder.append(", averageDuration=");
        builder.append(averageDuration);
        builder.append(", minDuration=");
        builder.append(minDuration);
        builder.append(", maxDuration=");
        builder.append(maxDuration);
        builder.append(", sumDuration=");
        builder.append(sumDuration);
        builder.append("]");
        return builder.toString();
    }

}
