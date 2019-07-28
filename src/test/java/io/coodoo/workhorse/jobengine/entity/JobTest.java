package io.coodoo.workhorse.jobengine.entity;

import javax.persistence.*;
import java.util.*;
import io.coodoo.workhorse.jobengine.entity.Job;
public class JobTest {
/**
 * Tests that query 'Job.countAllByStatus' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testCountAllByStatusQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.jobengine.entity.Job.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.jobengine.entity.Job.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("Job.countAllByStatus")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query Job.countAllByStatus does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT COUNT(job) FROM Job job WHERE job.status=:status' to '" + queryText + "'", "SELECT COUNT(job) FROM Job job WHERE job.status=:status", queryText);
}
/**
 * Tests that call and query are consistent for query 'Job.countAllByStatus' - no result.
 *
 */
@org.junit.Test 
public void testCountAllByStatusEmptyResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("Job.countAllByStatus")).willReturn(query);
	@SuppressWarnings("rawtypes")
	List results = new ArrayList();
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	JobStatus status = JobStatus.values().length <= 0 ? null : JobStatus.values()[0];
	org.mockito.BDDMockito.given(query.setParameter("status", status)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	Long result = io.coodoo.workhorse.jobengine.entity.Job.countAllByStatus(entityManager,status);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("Job.countAllByStatus");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("status",status);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertNull("Result should be null if list is empty", result);
}
/**
 * Tests that call and query are consistent for query 'Job.countAllByStatus' - one result.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@org.junit.Test 
public void testCountAllByStatusOneResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("Job.countAllByStatus")).willReturn(query);
	List results = new java.util.ArrayList();
	Long first = java.lang.Long.valueOf(0);
	Long second = java.lang.Long.valueOf(1);
	results.add(first);
	results.add(second);
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	JobStatus status = JobStatus.values().length <= 0 ? null : JobStatus.values()[0];
	org.mockito.BDDMockito.given(query.setParameter("status", status)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	Long result = io.coodoo.workhorse.jobengine.entity.Job.countAllByStatus(entityManager,status);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("Job.countAllByStatus");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("status",status);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertEquals("Result not the first of list.", first, result);
}
/**
 * Tests that all classes and members/fields used in query 'Job.countAllByStatus' still exist.
 *
 */
@org.junit.Test 
public void testCountAllByStatusVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[2][4];
	classesFieldsAndTypes[0][0] = "job";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.jobengine.entity.Job";
	classesFieldsAndTypes[1][0] = "job.status";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.jobengine.entity.Job";
	classesFieldsAndTypes[1][2] = "status";
	classesFieldsAndTypes[1][3] = "io.coodoo.workhorse.jobengine.entity.JobStatus";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Job.countAllByStatus: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Job.countAllByStatus: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Job.countAllByStatus: The class "	+ className + " does not exist (anymore)");
	}
	}
}
/**
 * Tests that query 'Job.getAllScheduled' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testGetAllScheduledQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.jobengine.entity.Job.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.jobengine.entity.Job.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("Job.getAllScheduled")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query Job.getAllScheduled does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT job FROM Job job WHERE job.schedule IS NOT NULL' to '" + queryText + "'", "SELECT job FROM Job job WHERE job.schedule IS NOT NULL", queryText);
}
/**
 * Tests that call and query are consistent for query 'Job.getAllScheduled'.
 *
 */
@org.junit.Test 
public void testGetAllScheduled()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("Job.getAllScheduled")).willReturn(query);
	// Call
	io.coodoo.workhorse.jobengine.entity.Job.getAllScheduled(entityManager);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("Job.getAllScheduled");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
}
/**
 * Tests that all classes and members/fields used in query 'Job.getAllScheduled' still exist.
 *
 */
@org.junit.Test 
public void testGetAllScheduledVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[2][4];
	classesFieldsAndTypes[0][0] = "job";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.jobengine.entity.Job";
	classesFieldsAndTypes[1][0] = "job.schedule";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.jobengine.entity.Job";
	classesFieldsAndTypes[1][2] = "schedule";
	classesFieldsAndTypes[1][3] = "java.lang.String";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Job.getAllScheduled: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Job.getAllScheduled: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query Job.getAllScheduled: The class "	+ className + " does not exist (anymore)");
	}
	}
}
}