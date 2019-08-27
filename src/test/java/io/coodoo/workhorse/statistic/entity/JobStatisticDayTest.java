package io.coodoo.workhorse.statistic.entity;

import javax.persistence.*;
import java.util.*;
import java.lang.Long;
import java.lang.Object;
import java.time.LocalDateTime;
public class JobStatisticDayTest {
/**
 * Tests that query 'JobStatisticDay.deleteAllByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testDeleteAllByJobIdQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticDay.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticDay.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticDay.deleteAllByJobId")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticDay.deleteAllByJobId does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'DELETE FROM JobStatisticDay j WHERE j.jobId = :jobId' to '" + queryText + "'", "DELETE FROM JobStatisticDay j WHERE j.jobId = :jobId", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticDay.deleteAllByJobId'.
 *
 */
@org.junit.Test 
public void testDeleteAllByJobId()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticDay.deleteAllByJobId")).willReturn(query);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	// Call
	io.coodoo.workhorse.statistic.entity.JobStatisticDay.deleteAllByJobId(entityManager,jobId);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticDay.deleteAllByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticDay.deleteAllByJobId' still exist.
 *
 */
@org.junit.Test 
public void testDeleteAllByJobIdVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[2][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[1][0] = "j.jobId";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.deleteAllByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.deleteAllByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.deleteAllByJobId: The class "	+ className + " does not exist (anymore)");
	}
	}
}
/**
 * Tests that query 'JobStatisticDay.findLatestByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testFindLatestByJobIdQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticDay.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticDay.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticDay.findLatestByJobId")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticDay.findLatestByJobId does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT j FROM JobStatisticDay j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC' to '" + queryText + "'", "SELECT j FROM JobStatisticDay j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticDay.findLatestByJobId' - no result.
 *
 */
@org.junit.Test 
public void testFindLatestByJobIdEmptyResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticDay.findLatestByJobId")).willReturn(query);
	@SuppressWarnings("rawtypes")
	List results = new ArrayList();
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	JobStatisticDay result = io.coodoo.workhorse.statistic.entity.JobStatisticDay.findLatestByJobId(entityManager,jobId);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticDay.findLatestByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertNull("Result should be null if list is empty", result);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticDay.findLatestByJobId' - one result.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@org.junit.Test 
public void testFindLatestByJobIdOneResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticDay.findLatestByJobId")).willReturn(query);
	List results = new java.util.ArrayList();
	JobStatisticDay first = org.mockito.Mockito.mock(JobStatisticDay.class);
	JobStatisticDay second = org.mockito.Mockito.mock(JobStatisticDay.class);
	results.add(first);
	results.add(second);
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	JobStatisticDay result = io.coodoo.workhorse.statistic.entity.JobStatisticDay.findLatestByJobId(entityManager,jobId);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticDay.findLatestByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertEquals("Result not the first of list.", first, result);
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticDay.findLatestByJobId' still exist.
 *
 */
@org.junit.Test 
public void testFindLatestByJobIdVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[3][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[1][0] = "j.createdAt";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[1][2] = "createdAt";
	classesFieldsAndTypes[1][3] = "java.time.LocalDateTime";
	classesFieldsAndTypes[2][0] = "j.jobId";
	classesFieldsAndTypes[2][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[2][2] = "jobId";
	classesFieldsAndTypes[2][3] = "java.lang.Long";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.findLatestByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.findLatestByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.findLatestByJobId: The class "	+ className + " does not exist (anymore)");
	}
	}
}
/**
 * Tests that query 'JobStatisticDay.deleteOldByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testDeleteOldByJobIdQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticDay.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticDay.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticDay.deleteOldByJobId")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticDay.deleteOldByJobId does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'DELETE FROM JobStatisticDay j WHERE j.jobId = :jobId AND j.from < :date' to '" + queryText + "'", "DELETE FROM JobStatisticDay j WHERE j.jobId = :jobId AND j.from < :date", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticDay.deleteOldByJobId'.
 *
 */
@org.junit.Test 
public void testDeleteOldByJobId()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticDay.deleteOldByJobId")).willReturn(query);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	LocalDateTime date = null;
	org.mockito.BDDMockito.given(query.setParameter("date", date)).willReturn(query);
	// Call
	io.coodoo.workhorse.statistic.entity.JobStatisticDay.deleteOldByJobId(entityManager,jobId, date);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticDay.deleteOldByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("date",date);
	org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticDay.deleteOldByJobId' still exist.
 *
 */
@org.junit.Test 
public void testDeleteOldByJobIdVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[3][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[1][0] = "j.from";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[1][2] = "from";
	classesFieldsAndTypes[1][3] = "java.time.LocalDateTime";
	classesFieldsAndTypes[2][0] = "j.jobId";
	classesFieldsAndTypes[2][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticDay";
	classesFieldsAndTypes[2][2] = "jobId";
	classesFieldsAndTypes[2][3] = "java.lang.Long";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.deleteOldByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.deleteOldByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticDay.deleteOldByJobId: The class "	+ className + " does not exist (anymore)");
	}
	}
}
}