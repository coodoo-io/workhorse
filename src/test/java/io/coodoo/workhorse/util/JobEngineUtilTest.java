package io.coodoo.workhorse.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JobEngineUtilTest {

    @Test
    public void testGetMessagesFromException() throws Exception {

        Exception exception = new Exception();

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals(exception.getClass().getName(), result);
    }

    @Test
    public void testGetMessagesFromException_message() throws Exception {

        String message = "DAFUQ!!!";
        Exception exception = new Exception("DAFUQ!!!");

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals(message, result);
    }

    @Test
    public void testGetMessagesFromException_causedByNullPointerException() throws Exception {

        NullPointerException nullPointerException = new NullPointerException();
        Exception exception = new Exception(nullPointerException);

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals(nullPointerException.getClass().getName(), result);
    }

    @Test
    public void testGetMessagesFromException_causedByNullPointerException_b() throws Exception {

        Exception exception = new Exception(new NullPointerException("b"));

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("b", result);
    }

    @Test
    public void testGetMessagesFromException_causedByNullPointerException_a_b() throws Exception {

        Exception exception = new Exception("a", new NullPointerException("b"));

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("a | b", result);
    }

    @Test
    public void testGetMessagesFromException_causedByNullPointerException_a() throws Exception {

        Exception exception = new Exception("a", new NullPointerException());

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("a", result);
    }

    @Test
    public void testGetMessagesFromException_NullPointerException() throws Exception {

        NullPointerException nullPointerException = new NullPointerException();

        String result = JobEngineUtil.getMessagesFromException(nullPointerException);

        assertEquals(nullPointerException.getClass().getName(), result);
    }

    @Test
    public void testGetMessagesFromException_NullPointerException_message() throws Exception {

        String message = "DAFUQ!!!";
        NullPointerException nullPointerException = new NullPointerException("DAFUQ!!!");

        String result = JobEngineUtil.getMessagesFromException(nullPointerException);

        assertEquals(message, result);
    }

    @Test
    public void testGetMessagesFromException_a_b_c_d_e() throws Exception {

        Exception exception = new Exception("a", new Exception("b", new Exception("c", new Exception("d", new Exception("e")))));

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("a | b | c | d | e", result);
    }

    @Test
    public void testGetMessagesFromException_a_b_b_c_c() throws Exception {

        Exception exception = new Exception("a", new Exception("b", new Exception("b", new Exception("c", new Exception("c")))));

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("a | b | c", result);
    }

    @Test
    public void testGetMessagesFromException_a_b_c_c_b() throws Exception {

        Exception exception = new Exception("a", new Exception("b", new Exception("c", new Exception("c", new Exception("b")))));

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("a | b | c", result);
    }

    @Test
    public void testGetMessagesFromException_a_b_c_NullPointerException() throws Exception {

        NullPointerException nullPointerException = new NullPointerException();
        Exception exception = new Exception("a", new Exception("b", new Exception("c", nullPointerException)));

        String result = JobEngineUtil.getMessagesFromException(exception);

        assertEquals("a | b | c", result);
    }

}
