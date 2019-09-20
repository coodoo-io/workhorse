package io.coodoo.workhorse.log.entity;

import javax.persistence.*;

import java.util.*;
import java.lang.Long;
import java.lang.Object;
public class LogTest {

    /**
     * Tests that query 'Log.deleteAllByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes", "null"})
    @org.junit.Test 
    public void testDeleteAllByJobIdQueryUnchanged()
    {
    	List annotations = new ArrayList();
    	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.log.entity.Log.class.getAnnotation(NamedQuery.class);
    	if (namedQueryAnnotation == null) {
    	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.log.entity.Log.class.getAnnotation(NamedQueries.class);
    	if (namedQueriesAnnotation != null) {
    	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
    	} else { annotations.add(namedQueryAnnotation); }
    	NamedQuery queryUnderTest = null;
    	for (Object obj : annotations) {
    	NamedQuery query = (NamedQuery) obj;
    	if (query.name().equals("Log.deleteAllByJobId")) {
    	queryUnderTest = query;
    	break;
    	}
    	}
    	if (queryUnderTest == null) {
    	org.junit.Assert.fail("Query Log.deleteAllByJobId does not exist anymore.");
    	}
    	String queryText = queryUnderTest.query();
    	// Minor changes with whitespace are ignored
    	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    	while (queryText.contains("  ")) {
    	queryText = queryText.replace("  ", " ");
    	}
    	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'DELETE FROM Log j WHERE j.jobId = :jobId' to '" + queryText + "'", "DELETE FROM Log j WHERE j.jobId = :jobId", queryText);
    }

    /**
     * Tests that call and query are consistent for query 'Log.deleteAllByJobId'.
     *
     */
    @org.junit.Test 
    public void testDeleteAllByJobId()
    {
    	Query query = org.mockito.Mockito.mock(Query.class);
    	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
    	org.mockito.BDDMockito.given(entityManager.createNamedQuery("Log.deleteAllByJobId")).willReturn(query);
    	Long jobId = java.lang.Long.valueOf(0);
    	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
    	// Call
    	io.coodoo.workhorse.log.entity.Log.deleteAllByJobId(entityManager,jobId);
    	// Verification
    	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("Log.deleteAllByJobId");
    	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
    	org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
    }

    /**
     * Tests that all classes and members/fields used in query 'Log.deleteAllByJobId' still exist.
     *
     */
    @org.junit.Test 
    public void testDeleteAllByJobIdVerifyFields()
    {
    	String[][] classesFieldsAndTypes = new String[2][4];
    	classesFieldsAndTypes[0][0] = "j";
    	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.log.entity.Log";
    	classesFieldsAndTypes[1][0] = "j.jobId";
    	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.log.entity.Log";
    	classesFieldsAndTypes[1][2] = "jobId";
    	classesFieldsAndTypes[1][3] = "java.lang.Long";
    	for (String[] testcase : classesFieldsAndTypes) {
    	String fieldPath = testcase[0];
    	String className = testcase[1];
    	String fieldName = testcase[2];
    	String fieldType = testcase[3];
    	try {
    	Class<?> clazz = Class.forName(className);
    	if (fieldName != null) {
    	boolean fieldFound = false;
    	do {
    	for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
    	if (field.getName().equals(fieldName)) {
    	if (fieldType != null && !field.getType().getName().equals(fieldType)) {
    	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Log.deleteAllByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
    	}
    	fieldFound = true;
    	break;
    	}
    	}
    	clazz = clazz.getSuperclass();
    	} while (!fieldFound && clazz != null);
    	if (!fieldFound) {
    	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Log.deleteAllByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
    	}
    	}
    	} catch (ClassNotFoundException e) {
    	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Log.deleteAllByJobId: The class "	+ className + " does not exist (anymore)");
    	}
    	}
    }
}