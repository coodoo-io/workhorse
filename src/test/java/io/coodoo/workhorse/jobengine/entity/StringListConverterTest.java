package io.coodoo.workhorse.jobengine.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringListConverterTest {

    private static StringListConverter stringListConverter = new StringListConverter();

    @Test
    public void testConvertToDatabaseColumn() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        String result = stringListConverter.convertToDatabaseColumn(list);

        assertEquals("A,B,C", result);
    }

    @Test
    public void testConvertToDatabaseColumn_empty() throws Exception {

        List<String> list = new ArrayList<>();

        String result = stringListConverter.convertToDatabaseColumn(list);

        assertNull(result);
    }

    @Test
    public void testConvertToDatabaseColumn_null() throws Exception {

        List<String> list = null;

        String result = stringListConverter.convertToDatabaseColumn(list);

        assertNull(result);
    }

    @Test
    public void testConvertToDatabaseColumn_emptyValue() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("");

        String result = stringListConverter.convertToDatabaseColumn(list);

        assertNull(result);
    }

    @Test
    public void testConvertToDatabaseColumn_emptyValueAtEnd() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("");

        String result = stringListConverter.convertToDatabaseColumn(list);

        assertEquals("A", result);
    }

    @Test
    public void testConvertToEntityAttribute() throws Exception {

        String joined = "A,B,C";

        List<String> result = stringListConverter.convertToEntityAttribute(joined);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("C", result.get(2));
    }

    @Test
    public void testConvertToEntityAttribute_separatorAtEnd() throws Exception {

        String joined = "A,B,";

        List<String> result = stringListConverter.convertToEntityAttribute(joined);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
    }

    @Test
    public void testConvertToEntityAttribute_onlySeparators() throws Exception {

        String joined = ",,";

        List<String> result = stringListConverter.convertToEntityAttribute(joined);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testConvertToEntityAttribute_empty() throws Exception {

        String joined = "";

        List<String> result = stringListConverter.convertToEntityAttribute(joined);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testConvertToEntityAttribute_null() throws Exception {

        String joined = null;

        List<String> result = stringListConverter.convertToEntityAttribute(joined);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
