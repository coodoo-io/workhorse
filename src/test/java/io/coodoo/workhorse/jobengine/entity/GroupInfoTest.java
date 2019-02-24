package io.coodoo.workhorse.jobengine.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GroupInfoTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    private Long id = 0L;

    @Test
    public void testGroupInfo_running() {

        LocalDateTime time = LocalDateTime.parse("12:00:00 01.01.2019", FORMATTER);

        List<JobExecutionInfo> executionInfos = new ArrayList<>();
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.FINISHED, time.plusSeconds(-2), time.plusSeconds(-1), 1000L, null));
        executionInfos.add(new JobExecutionInfo(2L, JobExecutionStatus.RUNNING, time.plusSeconds(-1), null, null, null));
        executionInfos.add(new JobExecutionInfo(3L, JobExecutionStatus.QUEUED, null, null, null, null));

        GroupInfo groupInfo = new GroupInfo(id, executionInfos);

        assertEquals(id, groupInfo.getId());
        assertEquals(JobExecutionStatus.RUNNING, groupInfo.getStatus());

        assertEquals(3, groupInfo.getSize());
        assertEquals(1, groupInfo.getQueued());
        assertEquals(1, groupInfo.getRunning());
        assertEquals(1, groupInfo.getFinished());
        assertEquals(0, groupInfo.getFailed());
        assertEquals(0, groupInfo.getAborted());

        assertEquals(time.plusSeconds(-2L), groupInfo.getStartedAt());
        assertNull(groupInfo.getEndedAt());
        assertEquals(33, groupInfo.getProgress());

        assertEquals(1000L, groupInfo.getDuration().longValue());
        assertEquals(3000L, groupInfo.getExpectedDuration().longValue());
        assertEquals(time.plusSeconds(1), groupInfo.getExpectedEnd());

        assertEquals(executionInfos, groupInfo.getExecutionInfos());
    }

    @Test
    public void testGroupInfo_finished() {

        LocalDateTime time = LocalDateTime.parse("12:00:00 01.01.2019", FORMATTER);

        List<JobExecutionInfo> executionInfos = new ArrayList<>();
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.FINISHED, time.plusSeconds(-7), time.plusSeconds(-6), 1000L, null));
        executionInfos.add(new JobExecutionInfo(2L, JobExecutionStatus.FINISHED, time.plusSeconds(-6), time.plusSeconds(-4), 2000L, null));
        executionInfos.add(new JobExecutionInfo(3L, JobExecutionStatus.FINISHED, time.plusSeconds(-4), time.plusSeconds(0), 4000L, null));

        GroupInfo groupInfo = new GroupInfo(id, executionInfos);

        assertEquals(id, groupInfo.getId());
        assertEquals(JobExecutionStatus.FINISHED, groupInfo.getStatus());

        assertEquals(3, groupInfo.getSize());
        assertEquals(0, groupInfo.getQueued());
        assertEquals(0, groupInfo.getRunning());
        assertEquals(3, groupInfo.getFinished());
        assertEquals(0, groupInfo.getFailed());
        assertEquals(0, groupInfo.getAborted());

        assertEquals(time.plusSeconds(-7), groupInfo.getStartedAt());
        assertEquals(time.plusSeconds(0), groupInfo.getEndedAt());
        assertEquals(100, groupInfo.getProgress());

        assertEquals(7000L, groupInfo.getDuration().longValue());
        assertNull(groupInfo.getExpectedDuration());
        assertNull(groupInfo.getExpectedEnd());

        assertEquals(executionInfos, groupInfo.getExecutionInfos());
    }

    @Test
    public void testGroupInfo_queued() {

        List<JobExecutionInfo> executionInfos = new ArrayList<>();
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.QUEUED, null, null, null, null));
        executionInfos.add(new JobExecutionInfo(2L, JobExecutionStatus.QUEUED, null, null, null, null));
        executionInfos.add(new JobExecutionInfo(3L, JobExecutionStatus.QUEUED, null, null, null, null));

        GroupInfo groupInfo = new GroupInfo(id, executionInfos);

        assertEquals(id, groupInfo.getId());
        assertEquals(JobExecutionStatus.QUEUED, groupInfo.getStatus());

        assertEquals(3, groupInfo.getSize());
        assertEquals(3, groupInfo.getQueued());
        assertEquals(0, groupInfo.getRunning());
        assertEquals(0, groupInfo.getFinished());
        assertEquals(0, groupInfo.getFailed());
        assertEquals(0, groupInfo.getAborted());

        assertNull(groupInfo.getStartedAt());
        assertNull(groupInfo.getEndedAt());
        assertEquals(0, groupInfo.getProgress());

        assertEquals(0, groupInfo.getDuration().longValue());
        assertNull(groupInfo.getExpectedDuration());
        assertNull(groupInfo.getExpectedEnd());

        assertEquals(executionInfos, groupInfo.getExecutionInfos());
    }

    @Test
    public void testGroupInfo_noExecutions() {

        List<JobExecutionInfo> executionInfos = new ArrayList<>();

        GroupInfo groupInfo = new GroupInfo(id, executionInfos);

        assertEquals(id, groupInfo.getId());
        assertNull(groupInfo.getStatus());

        assertEquals(0, groupInfo.getSize());
        assertEquals(0, groupInfo.getQueued());
        assertEquals(0, groupInfo.getRunning());
        assertEquals(0, groupInfo.getFinished());
        assertEquals(0, groupInfo.getFailed());
        assertEquals(0, groupInfo.getAborted());

        assertNull(groupInfo.getStartedAt());
        assertNull(groupInfo.getEndedAt());
        assertEquals(0, groupInfo.getProgress());

        assertEquals(0, groupInfo.getDuration().longValue());
        assertNull(groupInfo.getExpectedDuration());
        assertNull(groupInfo.getExpectedEnd());

        assertEquals(executionInfos, groupInfo.getExecutionInfos());
    }

    @Test
    public void testGroupInfo_desaster() {

        LocalDateTime time = LocalDateTime.parse("12:00:00 01.01.2019", FORMATTER);

        List<JobExecutionInfo> executionInfos = new ArrayList<>();
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.FAILED, time.plusSeconds(-9), time.plusSeconds(-9), 835L, null));
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.ABORTED, time.plusSeconds(-8), time.plusSeconds(-6), 500L, null));
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.FINISHED, time.plusSeconds(-2), time.plusSeconds(-1), 1000L, null));
        executionInfos.add(new JobExecutionInfo(2L, JobExecutionStatus.RUNNING, time.plusSeconds(-1), null, null, null));
        executionInfos.add(new JobExecutionInfo(3L, JobExecutionStatus.QUEUED, null, null, null, null));

        GroupInfo groupInfo = new GroupInfo(id, executionInfos);

        assertEquals(id, groupInfo.getId());
        assertEquals(JobExecutionStatus.RUNNING, groupInfo.getStatus());

        assertEquals(5, groupInfo.getSize());
        assertEquals(1, groupInfo.getQueued());
        assertEquals(1, groupInfo.getRunning());
        assertEquals(1, groupInfo.getFinished());
        assertEquals(1, groupInfo.getFailed());
        assertEquals(1, groupInfo.getAborted());

        assertEquals(time.plusSeconds(-9L), groupInfo.getStartedAt());
        assertNull(groupInfo.getEndedAt());
        assertEquals(60, groupInfo.getProgress());

        assertEquals(1835, groupInfo.getDuration().longValue());
        assertEquals(3055, groupInfo.getExpectedDuration().longValue());
        assertEquals(time, groupInfo.getExpectedEnd());

        assertEquals(executionInfos, groupInfo.getExecutionInfos());
    }

    @Test
    public void testGroupInfo_aborted() {

        LocalDateTime time = LocalDateTime.parse("12:00:00 01.01.2019", FORMATTER);

        List<JobExecutionInfo> executionInfos = new ArrayList<>();
        executionInfos.add(new JobExecutionInfo(1L, JobExecutionStatus.ABORTED, time.plusSeconds(-8), time.plusSeconds(-6), 500L, null));

        GroupInfo groupInfo = new GroupInfo(id, executionInfos);

        assertEquals(id, groupInfo.getId());
        assertEquals(JobExecutionStatus.ABORTED, groupInfo.getStatus());

        assertEquals(1, groupInfo.getSize());
        assertEquals(0, groupInfo.getQueued());
        assertEquals(0, groupInfo.getRunning());
        assertEquals(0, groupInfo.getFinished());
        assertEquals(0, groupInfo.getFailed());
        assertEquals(1, groupInfo.getAborted());

        assertEquals(time.plusSeconds(-8L), groupInfo.getStartedAt());
        assertNull(groupInfo.getEndedAt());
        assertEquals(100, groupInfo.getProgress());

        assertEquals(500, groupInfo.getDuration().longValue());
        assertNull(groupInfo.getExpectedDuration());
        assertNull(groupInfo.getExpectedEnd());

        assertEquals(executionInfos, groupInfo.getExecutionInfos());
    }

}
