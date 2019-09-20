package io.coodoo.workhorse.log.boundary;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.coodoo.workhorse.jobengine.boundary.JobEngineConfig;
import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.log.entity.Log;

@RunWith(MockitoJUnitRunner.class)
public class JobEngineLogServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private JobEngineService jobEngineService;

    @InjectMocks
    private JobEngineLogService jobEngineLogService;

    @Test
    public void testLogChange() throws Exception {

        Long jobId = 1L;
        JobStatus jobStatus = JobStatus.ACTIVE;
        String changeParameter = "Parameter";
        String changeOld = "old";
        String changeNew = "new";
        String message = null;

        String expected = String.format(JobEngineConfig.LOG_CHANGE, changeParameter, changeOld, changeNew);

        Log log = jobEngineLogService.logChange(jobId, jobStatus, changeParameter, changeOld, changeNew, message);

        assertEquals(expected, log.getMessage());
    }

}
