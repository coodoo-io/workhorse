package io.coodoo.workhorse.jobengine.boundary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

@RunWith(MockitoJUnitRunner.class)
public class JobContextTest {

    @Mock
    private Job job;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private JobContext jobContext;

    // @Test
    // public void testInit() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testGetJobExecution() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testGetJobId() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testGetJobExecutionId() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testGetLog() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }

    @Test
    public void testLogLine() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logLine(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("xxx"));
        assertEquals(3, result.length());
    }

    @Test
    public void testLogLine_3lines() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logLine(message);
        jobContext.logLine(message);
        jobContext.logLine(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("xxx"));
        assertEquals(11, result.length());

        assertEquals("xxxxxxxxx", result.replace(System.lineSeparator(), ""));
    }

    @Test
    public void testLogLineWithTimestamp() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logLineWithTimestamp(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("] xxx"));
        assertEquals(18, result.length());
        assertFalse(result.contains(System.lineSeparator()));
    }

    @Test
    public void testLogInfo() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        Logger logger = mock(Logger.class);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logInfo(logger, message);

        verify(logger).info(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("] xxx"));
        assertEquals(18, result.length());
    }

    @Test
    public void testLogWarn() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        Logger logger = mock(Logger.class);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logWarn(logger, message);

        verify(logger).warn(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("] [WARN] xxx"));
        assertEquals(25, result.length());
    }

    @Test
    public void testLogWarn_2lines() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        Logger logger = mock(Logger.class);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logWarn(logger, message);
        jobContext.logWarn(logger, message);

        verify(logger, times(2)).warn(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("] [WARN] xxx"));
        assertTrue(result.contains(System.lineSeparator()));
        assertEquals(51, result.length());
    }

    @Test
    public void testLogError() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        Logger logger = mock(Logger.class);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logError(logger, message);

        verify(logger).error(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("] [ERROR] xxx"));
        assertEquals(26, result.length());
    }

    @Test
    public void testLogError_2lines() throws Exception {

        given(jobExecution.getLog()).willReturn(null);
        Logger logger = mock(Logger.class);
        String message = "xxx";

        jobContext.init(jobExecution);
        jobContext.logError(logger, message);
        jobContext.logError(logger, message);

        verify(logger, times(2)).error(message);

        String result = jobContext.getLog();

        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("] [ERROR] xxx"));
        assertTrue(result.contains(System.lineSeparator()));
        assertEquals(53, result.length());
    }

}
