package io.coodoo.workhorse.statistic.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class JobStatisticSummaryTest {

    @Test
    public void testJobStatisticSummary() throws Exception {

        JobStatisticSummary result = new JobStatisticSummary(null, null, null, null, null, null, null, null);

        assertNotNull(result);

        assertNull(result.getDurationMax());
        assertNull(result.getDurationMin());
        assertNull(result.getDurationSum());
        assertNull(result.getDurationAvg());

        assertEquals(0, result.getDurationCount().intValue());
        assertEquals(0, result.getFailed().intValue());
        assertEquals(0, result.getFinished().intValue());
        assertEquals(0, result.getSchedule().intValue());
    }

}
