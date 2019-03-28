package io.coodoo.workhorse.jobengine.control.job;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.coodoo.workhorse.jobengine.boundary.JobContext;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.entity.Job;

@RunWith(MockitoJUnitRunner.class)
public class JobExecutionCleanupWorkerTest {

    @Mock
    private JobContext jobContext;

    @Mock
    private JobEngineController jobEngineController;

    @Mock
    private JobEngineService jobEngineService;

    @InjectMocks
    private JobExecutionCleanupWorker jobExecutionCleanupWorker;

    @Test
    public void testDoWork() throws Exception {

        List<Job> jobs = new ArrayList<>();

        Job jobA = new Job();
        jobA.setId(1L);
        jobA.setName("Job A");
        jobA.setDaysUntilCleanUp(1);
        jobs.add(jobA);

        Job jobB = new Job();
        jobB.setId(2L);
        jobB.setName("Job B");
        jobB.setDaysUntilCleanUp(30);
        jobs.add(jobB);

        given(jobEngineService.getAllJobs()).willReturn(jobs);

        int deletedA = 83;
        int deletedB = 77;
        given(jobEngineController.deleteOlderJobExecutions(jobA.getId(), jobA.getDaysUntilCleanUp())).willReturn(deletedA);
        given(jobEngineController.deleteOlderJobExecutions(jobB.getId(), jobB.getDaysUntilCleanUp())).willReturn(deletedB);

        jobExecutionCleanupWorker.doWork();

        verify(jobContext, times(1)).logInfo(any(), eq("Deleted | Days | Job ID | Job Name"));
        verify(jobContext, times(1)).logInfo(any(), eq("     83 |    1 |      1 | Job A"));
        verify(jobContext, times(1)).logInfo(any(), eq("     77 |   30 |      2 | Job B"));
        verify(jobContext, times(1)).logInfo(any(), eq("Deleted 160 job executions"));
    }

    @Test
    public void testDoWork_noCleanUp() throws Exception {

        List<Job> jobs = new ArrayList<>();

        Job jobA = new Job();
        jobA.setId(1L);
        jobA.setName("Job A");
        jobA.setDaysUntilCleanUp(1);
        jobs.add(jobA);

        Job jobB = new Job();
        jobB.setId(2L);
        jobB.setName("Job B");
        jobB.setDaysUntilCleanUp(0);
        jobs.add(jobB);

        given(jobEngineService.getAllJobs()).willReturn(jobs);

        int deletedA = 83;
        given(jobEngineController.deleteOlderJobExecutions(jobA.getId(), jobA.getDaysUntilCleanUp())).willReturn(deletedA);

        jobExecutionCleanupWorker.doWork();

        verify(jobEngineController, never()).deleteOlderJobExecutions(jobB.getId(), jobB.getDaysUntilCleanUp());

        verify(jobContext, times(1)).logInfo(any(), eq("Deleted | Days | Job ID | Job Name"));
        verify(jobContext, times(1)).logInfo(any(), eq("     83 |    1 |      1 | Job A"));
        verify(jobContext, times(1)).logInfo(any(), eq("      - |    - |      2 | Job B"));
        verify(jobContext, times(1)).logInfo(any(), eq("Deleted 83 job executions"));
    }

    @Test
    public void testDoWork_cleanUpError() throws Exception {

        List<Job> jobs = new ArrayList<>();

        Job jobA = new Job();
        jobA.setId(1L);
        jobA.setName("Job A");
        jobA.setDaysUntilCleanUp(1);
        jobs.add(jobA);

        Job jobB = new Job();
        jobB.setId(2L);
        jobB.setName("Job B");
        jobB.setDaysUntilCleanUp(30);
        jobs.add(jobB);

        given(jobEngineService.getAllJobs()).willReturn(jobs);

        int deletedA = 83;
        given(jobEngineController.deleteOlderJobExecutions(jobA.getId(), jobA.getDaysUntilCleanUp())).willReturn(deletedA);

        RuntimeException throwable = new RuntimeException("ZONK");
        given(jobEngineController.deleteOlderJobExecutions(jobB.getId(), jobB.getDaysUntilCleanUp())).willThrow(throwable);

        jobExecutionCleanupWorker.doWork();

        verify(jobContext, times(1)).logInfo(any(), eq("Deleted | Days | Job ID | Job Name"));
        verify(jobContext, times(1)).logInfo(any(), eq("     83 |    1 |      1 | Job A"));
        verify(jobContext, times(1)).logError(any(), eq("Could not delete executions for job (ID 2) ': ZONK"), any());
        verify(jobContext, times(1)).logInfo(any(), eq("Deleted 83 job executions"));
    }

}
