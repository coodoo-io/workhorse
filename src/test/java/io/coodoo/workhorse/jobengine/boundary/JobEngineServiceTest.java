package io.coodoo.workhorse.jobengine.boundary;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.coodoo.workhorse.jobengine.entity.JobExecution;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JobExecution.class)
public class JobEngineServiceTest {

    JobEngineService classUnderTest;

    @Mock
    EntityManager entityManager;

    @Before
    public void prepare() {
        classUnderTest = new JobEngineService();
        classUnderTest.entityManager = entityManager;
        PowerMockito.mockStatic(JobExecution.class);
    }

    @Test
    public void createJobExecution_parameterString_HashIsSetInJobExecution() {
        String parameters = "{meine parameter}";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), not(nullValue()));
    }

    @Test
    public void createJobExecution_parameterString_HashIsCorrect() {
        String parameters = "{meine parameter}";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), equalTo(parameters.hashCode()));
    }

    @Test
    public void createJobExecution_emptyParameterString_HashIsNullInJobExecution() {
        String parameters = "";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), is(nullValue()));
    }

    @Test
    public void createJobExecution_blankParameterString_HashIsNullInJobExecution() {
        String parameters = "         ";
        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, false);
        assertThat(jobExecution.getParametersHash(), is(nullValue()));
    }

    @Test
    public void createJobExecution_SameParameterOfQueuedJobsAndUniqueFlagTrue_NoNewJobIsCreatedExistandJobIsReturned() {

        String parameters = "{seasonId: 2}";

        JobExecution jobExecutionInQueue = new JobExecution();
        jobExecutionInQueue.setId(21l);
        when(JobExecution.getFirstCreatedByJobIdAndParametersHash(entityManager, 1l, parameters.hashCode())).thenReturn(jobExecutionInQueue);

        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, true);
        assertThat(jobExecution, not(nullValue()));
        assertThat(jobExecution.getId(), equalTo(21l));
    }

    @Test
    public void createJobExecution_NewParameterAndUniqueFlagTrue_NewJobIsCreatedAndReturned() {

        String parameters = "{seasonId: 2}";

        JobExecution jobExecutionInQueue = new JobExecution();
        jobExecutionInQueue.setId(21l);
        when(JobExecution.getFirstCreatedByJobIdAndParametersHash(entityManager, 1l, parameters.hashCode())).thenReturn(null);

        JobExecution jobExecution = classUnderTest.createJobExecution(1l, parameters, false, null, null, null, true);

        assertThat(jobExecution, not(nullValue()));
        assertThat(jobExecution.getJobId(), equalTo(1l));
    }

}
