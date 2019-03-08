package io.coodoo.workhorse.jobengine.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Test;

import io.coodoo.workhorse.jobengine.entity.JobSchedule;

public class JobEngineUtilTest {

    @Test
    public void testTimestamp() throws Exception {
        LocalDateTime result = JobEngineUtil.timestamp();
        assertNotNull(result);
    }

    @Test
    public void testToCronExpressionJobSchedule() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("30", "20", "7,19", "*", "*", "*", "*");

        String result = JobEngineUtil.toCronExpression(jobSchedule);

        assertEquals("30 20 7,19 * * *", result);
    }

    @Test
    public void testToCronExpressionJobSchedule_pseudoValues() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("second", "minute", "hour", "dayOfWeek", "dayOfMonth", "month", "year");

        String result = JobEngineUtil.toCronExpression(jobSchedule);

        assertEquals("second minute hour dayOfWeek dayOfMonth month", result);
    }

    @Test
    public void testToCronExpressionJobSchedule_null() throws Exception {

        String result = JobEngineUtil.toCronExpression(null);

        assertNull(result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_true_true() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("30", "20", "7,19", "*", "*", "*", "*");

        String result = JobEngineUtil.toCronExpression(jobSchedule, true, true);

        assertEquals("30 20 7,19 * * * *", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_pseudoValues_true_true() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("second", "minute", "hour", "dayOfWeek", "dayOfMonth", "month", "year");

        String result = JobEngineUtil.toCronExpression(jobSchedule, true, true);

        assertEquals("second minute hour dayOfWeek dayOfMonth month year", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_false_false() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("30", "20", "7,19", "*", "*", "*", "*");

        String result = JobEngineUtil.toCronExpression(jobSchedule, false, false);

        assertEquals("20 7,19 * * *", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_pseudoValues_false_false() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("second", "minute", "hour", "dayOfWeek", "dayOfMonth", "month", "year");

        String result = JobEngineUtil.toCronExpression(jobSchedule, false, false);

        assertEquals("minute hour dayOfWeek dayOfMonth month", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_false_true() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("30", "20", "7,19", "*", "*", "*", "*");

        String result = JobEngineUtil.toCronExpression(jobSchedule, false, true);

        assertEquals("20 7,19 * * * *", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_pseudoValues_false_true() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("second", "minute", "hour", "dayOfWeek", "dayOfMonth", "month", "year");

        String result = JobEngineUtil.toCronExpression(jobSchedule, false, true);

        assertEquals("minute hour dayOfWeek dayOfMonth month year", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_true_false() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("30", "20", "7,19", "*", "*", "*", "*");

        String result = JobEngineUtil.toCronExpression(jobSchedule, true, false);

        assertEquals("30 20 7,19 * * *", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_pseudoValues_true_false() throws Exception {

        JobSchedule jobSchedule = prepareSchedule("second", "minute", "hour", "dayOfWeek", "dayOfMonth", "month", "year");

        String result = JobEngineUtil.toCronExpression(jobSchedule, true, false);

        assertEquals("second minute hour dayOfWeek dayOfMonth month", result);
    }

    @Test
    public void testToCronExpressionJobScheduleBooleanBoolean_null() throws Exception {

        String result = JobEngineUtil.toCronExpression(null, true, true);

        assertNull(result);
    }

    private JobSchedule prepareSchedule(String second, String minute, String hour, String dayOfWeek, String dayOfMonth, String month, String year) {
        JobSchedule jobSchedule = mock(JobSchedule.class);
        when(jobSchedule.getSecond()).thenReturn(second);
        when(jobSchedule.getMinute()).thenReturn(minute);
        when(jobSchedule.getHour()).thenReturn(hour);
        when(jobSchedule.getDayOfWeek()).thenReturn(dayOfWeek);
        when(jobSchedule.getDayOfMonth()).thenReturn(dayOfMonth);
        when(jobSchedule.getMonth()).thenReturn(month);
        when(jobSchedule.getYear()).thenReturn(year);
        return jobSchedule;
    }

}
