package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;

import io.coodoo.workhorse.jobengine.entity.JobStatistic;

public class MemoryCount {

    public int size = 60; // TODO jobengineconfig
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

    public JobStatistic collectAndIterate(Long jobId, int currentlyQueued) {

        // set timestamp before leaving this position
        time[index.get()] = JobEngineUtil.timestamp();
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

            JobStatistic jobStatistic = new JobStatistic();
            jobStatistic.setJobId(jobId);
            jobStatistic.setQueued(queued[indexToPersist].get());
            jobStatistic.setFinished(finished[indexToPersist].get());
            jobStatistic.setFailed(failed[indexToPersist].get());
            jobStatistic.setSchedule(scheduleTriggers[indexToPersist].get());
            jobStatistic.setDurationAvg(durationAvg(indexToPersist));
            jobStatistic.setDurationMedian(durationMedian(indexToPersist));
            return jobStatistic;
        }
        return null;
    }

    private Long durationAvg(int index) {

        OptionalDouble average = durations[index].values().stream().mapToLong(d -> d).average();
        if (average.isPresent()) {
            return new Double(average.getAsDouble()).longValue();
        }
        return null;
    }

    private Long durationMedian(int index) {

        Collection<Long> values = durations[index].values();
        if (!values.isEmpty()) {
            DoubleStream sorted = values.stream().mapToDouble(d -> d).sorted();
            double median = values.size() % 2 == 0 ? sorted.skip(values.size() / 2 - 1).limit(2).average().getAsDouble()
                            : sorted.skip(values.size() / 2).findFirst().getAsDouble();
            return new Double(median).longValue();
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
