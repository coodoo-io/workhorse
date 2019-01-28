package io.coodoo.workhorse.jobengine.boundary;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.coodoo.workhorse.jobengine.control.JobEngineUtil;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

@RunWith(MockitoJUnitRunner.class)
public class JobWorkerWithTest {

    private static String STRONG = ";)";
    private static Long LONG = new Long(83L);
    private static MojoPojo MOJO_POJO;
    private static List<String> LOST_STRONG = new ArrayList<>();
    private static List<Long> LOST_LONG = new ArrayList<>();
    private static List<MojoPojo> LOST_POJO = new ArrayList<>();

    public class TypeString extends JobWorkerWith<String> {
        @Override
        public void doWork(String parameters) throws Exception {
            assertEquals(STRONG, parameters);
        }
    }
    public class TypeLong extends JobWorkerWith<Long> {
        @Override
        public void doWork(Long parameters) throws Exception {
            assertEquals(LONG, parameters);
        }
    }
    public class TypePojo extends JobWorkerWith<MojoPojo> {
        @Override
        public void doWork(MojoPojo parameters) throws Exception {
            assertEquals(MOJO_POJO, parameters);
        }
    }
    public class TypeListLong extends JobWorkerWith<List<Long>> {
        @Override
        public void doWork(List<Long> parameters) throws Exception {
            assertEquals(LOST_LONG.toString(), parameters.toString());
        }
    }
    public class TypeListString extends JobWorkerWith<List<String>> {
        @Override
        public void doWork(List<String> parameters) throws Exception {
            assertEquals(LOST_STRONG.toString(), parameters.toString());
        }
    }
    public class TypeListPojo extends JobWorkerWith<List<MojoPojo>> {
        @Override
        public void doWork(List<MojoPojo> parameters) throws Exception {
            assertEquals(LOST_POJO.toString(), parameters.toString());
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

    @InjectMocks
    private TypeListString jobWorkerWithListString = new TypeListString();

    @InjectMocks
    private TypeListLong jobWorkerWithListLong = new TypeListLong();

    @InjectMocks
    private TypeListPojo jobWorkerWithListPojo = new TypeListPojo();

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

        jobWorkerWithLong.doWork(LONG);
    }

    @Test
    public void testDoWorkExecutionLong() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG));
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
        mojoPojo.setL(LONG);
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

    @Test
    public void testDoWorkListString() throws Exception {

        jobWorkerWithListString.doWork(LOST_STRONG);
    }

    @Test
    public void testDoWorkExecutionListStringEmpty() throws Exception {

        LOST_STRONG = new ArrayList<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LOST_STRONG));
        jobWorkerWithListString.doWork(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListStringFull() throws Exception {

        LOST_STRONG = new ArrayList<>();
        LOST_STRONG.add("x");
        LOST_STRONG.add("");
        LOST_STRONG.add(null);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LOST_STRONG));
        jobWorkerWithListString.doWork(jobExecution);
    }

    @Test
    public void testDoWorkListLong() throws Exception {

        jobWorkerWithListLong.doWork(LOST_LONG);
    }

    @Test
    public void testDoWorkExecutionListLongEmpty() throws Exception {

        LOST_LONG = new ArrayList<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LOST_LONG));
        jobWorkerWithListLong.doWork(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListLongFull() throws Exception {

        LOST_LONG = new ArrayList<>();
        LOST_LONG.add(-1L);
        LOST_LONG.add(0L);
        LOST_LONG.add(null);
        LOST_LONG.add(1L);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LOST_LONG));
        jobWorkerWithListLong.doWork(jobExecution);
    }

    @Test
    public void testDoWorkListPojo() throws Exception {

        jobWorkerWithListPojo.doWork(LOST_POJO);
    }

    @Test
    public void testDoWorkExecutionListPojoEmpty() throws Exception {

        LOST_POJO = new ArrayList<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LOST_POJO));
        jobWorkerWithListPojo.doWork(jobExecution);
    }

    @Ignore // Lists only work with primitive Java objects...
    @Test
    public void testDoWorkExecutionListPojoFull() throws Exception {

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
        mojoPojo.setL(LONG);
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

        LOST_POJO = new ArrayList<>();
        LOST_POJO.add(MOJO_POJO);
        LOST_POJO.add(null);
        LOST_POJO.add(new MojoPojo());

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LOST_POJO));
        jobWorkerWithListPojo.doWork(jobExecution);
    }

}
