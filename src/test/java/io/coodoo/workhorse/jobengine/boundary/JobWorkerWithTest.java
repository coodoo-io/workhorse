package io.coodoo.workhorse.jobengine.boundary;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
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

    private static String STRING;
    private static Long LONG;
    private static Pojo POJO;
    private static List<String> STRING_LIST;
    private static List<Long> LONG_LIST;
    private static List<Pojo> POJO_LIST;
    private static Set<String> STRING_SET;
    private static Map<Long, String> LONG_STRING_MAP;

    public class TypeString extends JobWorkerWith<String> {
        @Override
        public void doWork(String parameters) throws Exception {
            assertEquals(STRING, parameters);
        }
    }
    public class TypeLong extends JobWorkerWith<Long> {
        @Override
        public void doWork(Long parameters) throws Exception {
            assertEquals(LONG, parameters);
        }
    }
    public class TypePojo extends JobWorkerWith<Pojo> {
        @Override
        public void doWork(Pojo parameters) throws Exception {
            assertEquals(POJO, parameters);
        }
    }
    public class TypeListLong extends JobWorkerWith<List<Long>> {
        @Override
        public void doWork(List<Long> parameters) throws Exception {
            assertEquals(LONG_LIST.toString(), parameters.toString());
        }
    }
    public class TypeListString extends JobWorkerWith<List<String>> {
        @Override
        public void doWork(List<String> parameters) throws Exception {
            assertEquals(STRING_LIST.toString(), parameters.toString());
        }
    }
    public class TypeListPojo extends JobWorkerWith<List<Pojo>> {
        @Override
        public void doWork(List<Pojo> parameters) throws Exception {
            assertEquals(POJO_LIST.toString(), parameters.toString());
        }
    }
    public class TypeSetString extends JobWorkerWith<Set<String>> {
        @Override
        public void doWork(Set<String> parameters) throws Exception {
            assertEquals(STRING_SET.toString(), parameters.toString());
        }
    }
    public class TypeMapLongString extends JobWorkerWith<Map<Long, String>> {
        @Override
        public void doWork(Map<Long, String> parameters) throws Exception {
            assertEquals(LONG_STRING_MAP.toString(), parameters.toString());
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

    @InjectMocks
    private TypeSetString jobWorkerWithSetString = new TypeSetString();

    @InjectMocks
    private TypeMapLongString jobWorkerWithMapLongString = new TypeMapLongString();

    @Before
    public void setUp() {
        STRING = ";)";
        LONG = new Long(83L);
        POJO = new Pojo();
        STRING_LIST = new ArrayList<>();
        LONG_LIST = new ArrayList<>();
        POJO_LIST = new ArrayList<>();
        STRING_SET = new HashSet<>();
        LONG_STRING_MAP = new HashMap<>();
    }

    @Test
    public void testGetParametersString() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(STRING);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        String result = jobWorkerWithString.getParameters(jobExecution);

        assertEquals(STRING, result);
    }

    @Test
    public void testGetParametersClassString() throws Exception {

        Class<?> result = jobWorkerWithString.getParametersClass();

        assertEquals(String.class, result);
    }

    @Test
    public void testDoWorkString() throws Exception {

        jobWorkerWithString.doWork(STRING);
    }

    @Test
    public void testDoWorkExecutionString() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING));
        jobWorkerWithString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersLong() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(LONG);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        Long result = jobWorkerWithLong.getParameters(jobExecution);

        assertEquals(LONG, result);
    }

    @Test
    public void testGetParametersClassLong() throws Exception {

        Class<?> result = jobWorkerWithLong.getParametersClass();

        assertEquals(Long.class, result);
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

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersPojo() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(POJO);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        Pojo result = jobWorkerWithPojo.getParameters(jobExecution);

        assertEquals(POJO, result);
    }

    @Test
    public void testGetParametersClassPojo() throws Exception {

        Class<?> result = jobWorkerWithPojo.getParametersClass();

        assertEquals(Pojo.class, result);
    }

    @Test
    public void testDoWorkPojo() throws Exception {

        POJO = new Pojo();

        jobWorkerWithPojo.doWork(POJO);
    }

    @Test
    public void testDoWorkExecutionPojoEmpty() throws Exception {

        POJO = new Pojo();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(POJO));
        jobWorkerWithPojo.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionPojoFull() throws Exception {

        POJO = new Pojo();
        POJO.i = -2;
        POJO.io = new Integer(32456);
        POJO.setL(987654L);
        POJO.setLo(new Long(-1));
        POJO.s = "Stringily";
        POJO.b = true;
        POJO.bo = Boolean.FALSE;
        POJO.d = new Date();
        POJO.lt = LocalTime.now();
        POJO.ldt = LocalDateTime.now();
        Pojo mojoPojo = new Pojo();
        mojoPojo.setL(LONG);
        POJO.mp = mojoPojo;
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("");
        list.add(null);
        POJO.ls = list;
        Map<Long, String> map = new HashMap<>();
        map.put(0L, "value");
        map.put(1L, null);
        POJO.mls = map;
        POJO.ia = new int[] {10, 20, 30, 40, 50, 60, 71, 80, 90, 91};

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(POJO));
        jobWorkerWithPojo.doWork(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListString() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING_LIST));
        jobWorkerWithListString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersListString() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(STRING_LIST);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        List<String> result = jobWorkerWithListString.getParameters(jobExecution);

        assertEquals(STRING_LIST, result);
    }

    @Test
    public void testGetParametersClassListString() throws Exception {

        Class<?> result = jobWorkerWithListString.getParametersClass();

        assertEquals(List.class, result);
    }

    @Test
    public void testDoWorkListString() throws Exception {

        jobWorkerWithListString.doWork(STRING_LIST);
    }

    @Test
    public void testDoWorkExecutionListStringEmpty() throws Exception {

        STRING_LIST = new ArrayList<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING_LIST));
        jobWorkerWithListString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListStringFull() throws Exception {

        STRING_LIST = new ArrayList<>();
        STRING_LIST.add("x");
        STRING_LIST.add("");
        STRING_LIST.add(null);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING_LIST));
        jobWorkerWithListString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListLong() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG_LIST));
        jobWorkerWithListLong.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersListLong() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(LONG_LIST);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        List<Long> result = jobWorkerWithListLong.getParameters(jobExecution);

        assertEquals(LONG_LIST, result);
    }

    @Test
    public void testGetParametersClassListLong() throws Exception {

        Class<?> result = jobWorkerWithListLong.getParametersClass();

        assertEquals(List.class, result);
    }

    @Test
    public void testDoWorkListLong() throws Exception {

        jobWorkerWithListLong.doWork(LONG_LIST);
    }

    @Test
    public void testDoWorkExecutionListLongEmpty() throws Exception {

        LONG_LIST = new ArrayList<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG_LIST));
        jobWorkerWithListLong.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListLongFull() throws Exception {

        LONG_LIST = new ArrayList<>();
        LONG_LIST.add(-1L);
        LONG_LIST.add(0L);
        LONG_LIST.add(null);
        LONG_LIST.add(1L);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG_LIST));
        jobWorkerWithListLong.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionListPojo() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(POJO_LIST));
        jobWorkerWithListPojo.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersListPojo() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(POJO_LIST);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        List<Pojo> result = jobWorkerWithListPojo.getParameters(jobExecution);

        assertEquals(POJO_LIST, result);
    }

    @Test
    public void testDoWorkListPojo() throws Exception {

        jobWorkerWithListPojo.doWork(POJO_LIST);
    }

    @Test
    public void testDoWorkExecutionListPojoEmpty() throws Exception {

        POJO_LIST = new ArrayList<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(POJO_LIST));
        jobWorkerWithListPojo.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Ignore // Lists only work with primitive Java objects...
    @Test
    public void testDoWorkExecutionListPojoFull() throws Exception {

        POJO = new Pojo();
        POJO.i = -2;
        POJO.io = new Integer(32456);
        POJO.setL(987654L);
        POJO.setLo(new Long(-1));
        POJO.s = "Stringily";
        POJO.b = true;
        POJO.bo = Boolean.FALSE;
        POJO.d = new Date();
        POJO.lt = LocalTime.now();
        POJO.ldt = LocalDateTime.now();
        Pojo mojoPojo = new Pojo();
        mojoPojo.setL(LONG);
        POJO.mp = mojoPojo;
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("");
        list.add(null);
        POJO.ls = list;
        Map<Long, String> map = new HashMap<>();
        map.put(0L, "value");
        map.put(1L, null);
        POJO.mls = map;
        POJO.ia = new int[] {10, 20, 30, 40, 50, 60, 71, 80, 90, 91};

        POJO_LIST = new ArrayList<>();
        POJO_LIST.add(POJO);
        POJO_LIST.add(null);
        POJO_LIST.add(new Pojo());

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(POJO_LIST));
        jobWorkerWithListPojo.doWork(jobExecution);
    }

    @Test
    public void testDoWorkExecutionSetString() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING_SET));
        jobWorkerWithSetString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersSetString() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(STRING_SET);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        Set<String> result = jobWorkerWithSetString.getParameters(jobExecution);

        assertEquals(STRING_SET, result);
    }

    @Test
    public void testGetParametersClassSetString() throws Exception {

        Class<?> result = jobWorkerWithSetString.getParametersClass();

        assertEquals(Set.class, result);
    }

    @Test
    public void testDoWorkSetString() throws Exception {

        jobWorkerWithSetString.doWork(STRING_SET);
    }

    @Test
    public void testDoWorkExecutionSetStringEmpty() throws Exception {

        STRING_SET = new HashSet<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING_SET));
        jobWorkerWithSetString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionSetStringFull() throws Exception {

        STRING_SET = new HashSet<>();
        STRING_SET.add("x");
        STRING_SET.add("");
        STRING_SET.add(null);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(STRING_SET));
        jobWorkerWithSetString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionMapLongString() throws Exception {

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG_STRING_MAP));
        jobWorkerWithMapLongString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testGetParametersMapLongString() throws Exception {

        String parameters = JobEngineUtil.parametersToJson(LONG_STRING_MAP);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(parameters);

        Map<Long, String> result = jobWorkerWithMapLongString.getParameters(jobExecution);

        assertEquals(LONG_STRING_MAP, result);
    }

    @Test
    public void testGetParametersClassMapLongString() throws Exception {

        Class<?> result = jobWorkerWithMapLongString.getParametersClass();

        assertEquals(Map.class, result);
    }

    @Test
    public void testDoWorkMapLongString() throws Exception {

        jobWorkerWithMapLongString.doWork(LONG_STRING_MAP);
    }

    @Test
    public void testDoWorkExecutionMapLongStringEmpty() throws Exception {

        LONG_STRING_MAP = new HashMap<>();

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG_STRING_MAP));
        jobWorkerWithMapLongString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }

    @Test
    public void testDoWorkExecutionMapLongStringFull() throws Exception {

        LONG_STRING_MAP = new HashMap<>();
        LONG_STRING_MAP.put(-1L, "x");
        LONG_STRING_MAP.put(0L, "");
        LONG_STRING_MAP.put(1L, null);

        JobExecution jobExecution = new JobExecution();
        jobExecution.setParameters(JobEngineUtil.parametersToJson(LONG_STRING_MAP));
        jobWorkerWithMapLongString.doWork(jobExecution);

        verify(jobContext).init(jobExecution);
    }
}
