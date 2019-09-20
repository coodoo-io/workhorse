package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.coodoo.workhorse.statistic.boundary.StatisticsUtil;
import io.coodoo.workhorse.statistic.entity.JobStatisticMinute;
import io.coodoo.workhorse.util.JobEngineUtil;

public class MemoryCount {

    public int size = 60;
    public AtomicInteger index;
    public LocalDateTime[] time;
    public AtomicInteger[] queued;
    public AtomicInteger[] finished;
    public AtomicInteger[] failed;
    public AtomicInteger[] scheduleTriggers;
    public Map<Long, Long> durations[];

    @SuppressWarnings("unchecked")
    public MemoryCount() {

        index = new AtomicInteger(0);
        time = new LocalDateTime[size];
        queued = new AtomicInteger[size];
        finished = new AtomicInteger[size];
        failed = new AtomicInteger[size];
        scheduleTriggers = new AtomicInteger[size];
        durations = new ConcurrentHashMap[size];

        for (int i = 0; i < size; i++) {
            queued[i] = new AtomicInteger(0);
            finished[i] = new AtomicInteger(0);
            failed[i] = new AtomicInteger(0);
            scheduleTriggers[i] = new AtomicInteger(0);
            durations[i] = new ConcurrentHashMap<Long, Long>();
        }
    }

    public JobStatisticMinute collectAndIterate(Long jobId, int currentlyQueued) {

        LocalDateTime timestamp = JobEngineUtil.timestamp();
        // set timestamp before leaving this position
        time[index.get()] = timestamp;
        queued[index.get()].set(currentlyQueued);

        // iterate
        int indexToPersist = index.getAndIncrement();
        if (size == index.get()) {
            index.set(0);
        }

        time[index.get()] = null;
        queued[index.get()].set(0);
        finished[index.get()].set(0);
        failed[index.get()].set(0);
        scheduleTriggers[index.get()].set(0);
        durations[index.get()] = new ConcurrentHashMap<Long, Long>();

        if (queued[indexToPersist].get() > 0 || finished[indexToPersist].get() > 0 || failed[indexToPersist].get() > 0
                        || scheduleTriggers[indexToPersist].get() > 0) {

            LongSummaryStatistics durationStatistics = durations[indexToPersist].values().stream().mapToLong(d -> d).summaryStatistics();

            JobStatisticMinute jobStatisticMinute = new JobStatisticMinute();
            jobStatisticMinute.setJobId(jobId);
            jobStatisticMinute.setFrom(timestamp.minusSeconds(59));
            jobStatisticMinute.setTo(timestamp);
            jobStatisticMinute.setQueued(queued[indexToPersist].get());
            jobStatisticMinute.setFinished(finished[indexToPersist].get());
            jobStatisticMinute.setFailed(failed[indexToPersist].get());
            jobStatisticMinute.setSchedule(scheduleTriggers[indexToPersist].get());
            jobStatisticMinute.setDurationCount(new Long(durationStatistics.getCount()).intValue());
            jobStatisticMinute.setDurationSum(durationStatistics.getSum());
            jobStatisticMinute.setDurationMin(StatisticsUtil.longToLong(durationStatistics.getMin()));
            jobStatisticMinute.setDurationMax(StatisticsUtil.longToLong(durationStatistics.getMax()));
            jobStatisticMinute.setDurationAvg(StatisticsUtil.doubleToLong(durationStatistics.getAverage()));
            jobStatisticMinute.setDurationMedian(StatisticsUtil.median(durations[indexToPersist].values()));

            return jobStatisticMinute;
        }
        return null;
    }

    public void incrementFinished(Long jobExecutionId, Long duration) {
        finished[index.get()].incrementAndGet();
        durations[index.get()].put(jobExecutionId, duration);
    }

    public void incrementFailed(Long jobExecutionId, Long duration) {
        failed[index.get()].incrementAndGet();
        durations[index.get()].put(jobExecutionId, duration);
    }

    public void incrementScheduleTriggers() {
        scheduleTriggers[index.get()].incrementAndGet();
    }

}
