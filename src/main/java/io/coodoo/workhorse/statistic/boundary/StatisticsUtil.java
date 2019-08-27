package io.coodoo.workhorse.statistic.boundary;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.DoubleStream;

import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public final class StatisticsUtil {

    private StatisticsUtil() {}

    public static Long median(Collection<Long> values) {

        if (!values.isEmpty()) {
            DoubleStream sorted = values.stream().mapToDouble(d -> d).sorted();
            double median = values.size() % 2 == 0 ? sorted.skip(values.size() / 2 - 1).limit(2).average().getAsDouble()
                            : sorted.skip(values.size() / 2).findFirst().getAsDouble();
            return doubleToLong(median);
        }
        return null;
    }

    public static Long longToLong(Long value) {
        if (value == null || value == 0 || value == Long.MAX_VALUE || value == Long.MIN_VALUE) {
            return null;
        }
        return value;
    }

    public static Long doubleToLong(Double value) {
        if (value == null || value == 0) {
            return null;
        }
        return value.longValue();
    }

    public static long toEpochMilli(LocalDateTime time) {
        return time.atZone(JobEngineConfig.TIME_ZONE).toInstant().toEpochMilli();
    }
}
