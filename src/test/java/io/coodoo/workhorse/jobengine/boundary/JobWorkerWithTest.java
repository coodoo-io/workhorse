package io.coodoo.workhorse.jobengine.boundary;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

@RunWith(MockitoJUnitRunner.class)
public class JobWorkerWithTest {

    private static final String STRONG = ";)";
    private static final Long SO_LONG = new Long(83L);
    private static MojoPojo MOJO_POJO;

    public class TypeString extends JobWorkerWith<String> {
        @Override
        public void doWork(String parameters) throws Exception {
            assertEquals(STRONG, parameters);
        }
    }

    public class TypeLong extends JobWorkerWith<Long> {
        @Override
        public void doWork(Long parameters) throws Exception {
            assertEquals(SO_LONG, parameters);
        }
    }

    public class TypePojo extends JobWorkerWith<MojoPojo> {
        @Override
        public void doWork(MojoPojo parameters) throws Exception {
            assertEquals(MOJO_POJO, parameters);
        }
    }

    @Mock
    private JobContext jobContext;

    @Mock
    private JobEngineService jobEngineService;

    @InjectMocks
    private TypeString jobWorkerWithString = new TypeString();

    @InjectMocks
    private TypeLong jobWorkerWithLong = new TypeLong();

    @InjectMocks
    private TypePojo jobWorkerWithPojo = new TypePojo();

    @Test
    public void testDoWorkString() throws Exception {

        jobWorkerWithString.doWork(STRONG);
    }

    @Test
    public void testDoWorkExecutionString() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRONG));
        jobWorkerWithString.doWork(jobExecution);
    }

    @Test
    public void testDoWorkLong() throws Exception {

        jobWorkerWithLong.doWork(SO_LONG);
    }

    @Test
    public void testDoWorkExecutionLong() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(SO_LONG));
        jobWorkerWithLong.doWork(jobExecution);
    }

    @Test
    public void testDoWorkPojo() throws Exception {

        MOJO_POJO = new MojoPojo();

        jobWorkerWithPojo.doWork(MOJO_POJO);
    }

    @Test
    public void testDoWorkExecutionPojoEmpty() throws Exception {

        MOJO_POJO = new MojoPojo();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(MOJO_POJO));
        jobWorkerWithPojo.doWork(jobExecution);
    }

    @Test
    public void testDoWorkExecutionPojoFull() throws Exception {

        MOJO_POJO = new MojoPojo();
        MOJO_POJO.i = -2;
        MOJO_POJO.io = new Integer(32456);
        MOJO_POJO.setL(987654L);
        MOJO_POJO.setLo(new Long(-1));
        MOJO_POJO.s = "Stringily";
        MOJO_POJO.b = true;
        MOJO_POJO.bo = Boolean.FALSE;
        MOJO_POJO.d = new Date();
        MOJO_POJO.lt = LocalTime.now();
        MOJO_POJO.ldt = LocalDateTime.now();
        MojoPojo mojoPojo = new MojoPojo();
        mojoPojo.setL(SO_LONG);
        MOJO_POJO.mp = mojoPojo;
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("");
        list.add(null);
        MOJO_POJO.ls = list;
        Map<Long, String> map = new HashMap<>();
        map.put(0L, "value");
        map.put(1L, null);
        MOJO_POJO.mls = map;
        MOJO_POJO.ia = new int[] {10, 20, 30, 40, 50, 60, 71, 80, 90, 91};

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(MOJO_POJO));
        jobWorkerWithPojo.doWork(jobExecution);
    }

}
