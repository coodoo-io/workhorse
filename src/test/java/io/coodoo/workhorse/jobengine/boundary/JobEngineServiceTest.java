package io.coodoo.workhorse.jobengine.boundary;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import io.coodoo.workhorse.jobengine.control.JobEngine;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.control.JobQueuePoller;
import io.coodoo.workhorse.jobengine.control.JobScheduler;
import io.coodoo.workhorse.jobengine.entity.AnquGenericMockUtil;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

@RunWith(MockitoJUnitRunner.class)
public class JobEngineServiceTest {

    @Mock
    Logger logger;

    @Mock
    JobEngine jobEngine;

    @Mock
    JobEngineController jobEngineController;

    @Mock
    JobQueuePoller jobQueuePoller;

    @Mock
    JobScheduler jobScheduler;

    @Mock
    EntityManager entityManager;

    @InjectMocks
    JobEngineService classUnderTest;

    @Test
    public void createJobExecution_parameterString_HashIsSetInJobExecution() {
        String parameters = "{meine parameter}";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), not(nullValue()));
    }

    @Test
    public void createJobExecution_parameterString_HashIsCorrect() {
        String parameters = "{meine parameter}";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), equalTo(parameters.hashCode()));
    }

    @Test
    public void createJobExecution_emptyParameterString_HashIsNullInJobExecution() {
        String parameters = "";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), is(nullValue()));
    }

    @Test
    public void createJobExecution_blankParameterString_HashIsNullInJobExecution() {
        String parameters = "         ";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), is(nullValue()));
    }

    @Test
    public void createJobExecution_SameParameterOfQueuedJobsAndUniqueFlagTrue_NoNewJobIsCreatedExistandJobIsReturned() {

        String parameters = "{seasonId: 2}";

        JobExecution jobExecutionInQueue = new JobExecution();
        jobExecutionInQueue.setId(21l);

        AnquGenericMockUtil.mockAnyActionAnyQuery(entityManager, jobExecutionInQueue);

        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, null, true);
        assertThat(jobExecution, not(nullValue()));
        assertThat(jobExecution.getId(), equalTo(21l));
    }

    @Test
    public void createJobExecution_NewParameterAndUniqueFlagTrue_NewJobIsCreatedAndReturned() {

        String parameters = "{seasonId: 2}";
        AnquGenericMockUtil.mockAnyActionAnyQuery(entityManager, null);

        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, null, true);

        assertThat(jobExecution, not(nullValue()));
        assertThat(jobExecution.getJobId(), equalTo(1l));
    }

    @Test
    public void testGetNextScheduledTimes() throws Exception {

        Long jobId = 1L;
        int times = 5;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getNextScheduledTimes(jobId, times, startTime);

        assertNotNull(result);
        assertEquals(times, result.size());

        assertEquals(LocalDateTime.parse("2019-03-09T19:20:30"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-10T07:20:30"), result.get(1));
        assertEquals(LocalDateTime.parse("2019-03-10T19:20:30"), result.get(2));
        assertEquals(LocalDateTime.parse("2019-03-11T07:20:30"), result.get(3));
        assertEquals(LocalDateTime.parse("2019-03-11T19:20:30"), result.get(4));
    }

    @Test
    public void testGetNextScheduledTimes_schedule() throws Exception {

        int times = 5;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        String schedule = "30 20 7,19 * * *";

        List<LocalDateTime> result = classUnderTest.getNextScheduledTimes(schedule, times, startTime);

        assertNotNull(result);
        assertEquals(times, result.size());

        assertEquals(LocalDateTime.parse("2019-03-09T19:20:30"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-10T07:20:30"), result.get(1));
        assertEquals(LocalDateTime.parse("2019-03-10T19:20:30"), result.get(2));
        assertEquals(LocalDateTime.parse("2019-03-11T07:20:30"), result.get(3));
        assertEquals(LocalDateTime.parse("2019-03-11T19:20:30"), result.get(4));
    }

    @Test
    public void testGetNextScheduledTimes_noSchedule() throws Exception {

        Long jobId = 1L;
        int times = 5;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        given(entityManager.find(Job.class, jobId)).willReturn(null);

        try {
            classUnderTest.getNextScheduledTimes(jobId, times, startTime);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testGetNextScheduledTimes_noStartTime() throws Exception {

        Long jobId = 1L;
        int times = 5;
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getNextScheduledTimes(jobId, times, null);

        assertNotNull(result);
        assertEquals(times, result.size());
    }

    @Test
    public void testGetNextScheduledTimes_negativeTimes() throws Exception {

        Long jobId = 1L;
        int times = -5;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getNextScheduledTimes(jobId, times, startTime);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetNextScheduledTimes_zeroTimes() throws Exception {

        Long jobId = 1L;
        int times = 0;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getNextScheduledTimes(jobId, times, startTime);

        assertNotNull(result);
        assertEquals(times, result.size());
    }

    @Test
    public void testGetScheduledTimes() throws Exception {

        Long jobId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2019, 3, 11, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getScheduledTimes(jobId, startTime, endTime);

        assertNotNull(result);
        assertEquals(5, result.size());

        assertEquals(LocalDateTime.parse("2019-03-09T19:20:30"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-10T07:20:30"), result.get(1));
        assertEquals(LocalDateTime.parse("2019-03-10T19:20:30"), result.get(2));
        assertEquals(LocalDateTime.parse("2019-03-11T07:20:30"), result.get(3));
        assertEquals(LocalDateTime.parse("2019-03-11T19:20:30"), result.get(4));
    }

    @Test
    public void testGetScheduledTimes_schedule() throws Exception {

        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2019, 3, 11, 12, 0, 0, 0);
        String schedule = "33 22 1,23 * * *";

        List<LocalDateTime> result = classUnderTest.getScheduledTimes(schedule, startTime, endTime);

        assertNotNull(result);
        assertEquals(5, result.size());

        assertEquals(LocalDateTime.parse("2019-03-09T23:22:33"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-10T01:22:33"), result.get(1));
        assertEquals(LocalDateTime.parse("2019-03-10T23:22:33"), result.get(2));
        assertEquals(LocalDateTime.parse("2019-03-11T01:22:33"), result.get(3));
        assertEquals(LocalDateTime.parse("2019-03-11T23:22:33"), result.get(4));
    }

    @Test
    public void testGetScheduledTimes_noSchedule() throws Exception {

        Long jobId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2019, 3, 11, 12, 0, 0, 0);
        given(entityManager.find(Job.class, jobId)).willReturn(null);

        try {
            classUnderTest.getScheduledTimes(jobId, startTime, endTime);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testGetScheduledTimes_noStartTime() throws Exception {

        Long jobId = 1L;
        LocalDateTime endTime = JobEngineUtil.timestamp().plusDays(1);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getScheduledTimes(jobId, null, endTime);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testGetScheduledTimes_noEndTime() throws Exception {

        Long jobId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getScheduledTimes(jobId, startTime, null);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(LocalDateTime.parse("2019-03-09T19:20:30"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-10T07:20:30"), result.get(1));
        assertEquals(LocalDateTime.parse("2019-03-10T19:20:30"), result.get(2));
    }

    @Test
    public void testGetScheduledTimes_oneYear() throws Exception {

        Long jobId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2018, 3, 9, 12, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 7,19 * * *");

        List<LocalDateTime> result = classUnderTest.getScheduledTimes(jobId, startTime, endTime);

        assertNotNull(result);
        assertEquals(731, result.size());

        assertEquals(LocalDateTime.parse("2018-03-09T19:20:30"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-09T19:20:30"), result.get(730));
    }

    @Test
    public void testGetScheduledTimes_oneYearEveryHour() throws Exception {

        Long jobId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2018, 3, 9, 12, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2019, 3, 9, 12, 0, 0, 0);
        prepareSchedule(jobId, "30 20 * * * *");

        List<LocalDateTime> result = classUnderTest.getScheduledTimes(jobId, startTime, endTime);

        assertNotNull(result);
        assertEquals(8761, result.size());

        assertEquals(LocalDateTime.parse("2018-03-09T12:20:30"), result.get(0));
        assertEquals(LocalDateTime.parse("2019-03-09T12:20:30"), result.get(8760));
    }

    private void prepareSchedule(Long jobId, String schedule) {

        Job job = new Job();
        job.setSchedule(schedule);

        given(entityManager.find(Job.class, jobId)).willReturn(job);
    }

}
