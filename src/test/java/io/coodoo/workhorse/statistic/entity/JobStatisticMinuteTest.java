package io.coodoo.workhorse.statistic.entity;

import javax.persistence.*;
import java.util.*;
import java.lang.Long;
import java.lang.Object;
import java.time.LocalDateTime;
public class JobStatisticMinuteTest {
/**
 * Tests that query 'JobStatisticMinute.deleteAllByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testDeleteAllByJobIdQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticMinute.deleteAllByJobId")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticMinute.deleteAllByJobId does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'DELETE FROM JobStatisticMinute j WHERE j.jobId = :jobId' to '" + queryText + "'", "DELETE FROM JobStatisticMinute j WHERE j.jobId = :jobId", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticMinute.deleteAllByJobId'.
 *
 */
@org.junit.Test 
public void testDeleteAllByJobId()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticMinute.deleteAllByJobId")).willReturn(query);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	// Call
	io.coodoo.workhorse.statistic.entity.JobStatisticMinute.deleteAllByJobId(entityManager,jobId);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticMinute.deleteAllByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticMinute.deleteAllByJobId' still exist.
 *
 */
@org.junit.Test 
public void testDeleteAllByJobIdVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[2][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][0] = "j.jobId";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.deleteAllByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.deleteAllByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.deleteAllByJobId: The class "	+ className + " does not exist (anymore)");
	}
	}
}
/**
 * Tests that query 'JobStatisticMinute.findLatestByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testFindLatestByJobIdQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticMinute.findLatestByJobId")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticMinute.findLatestByJobId does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT j FROM JobStatisticMinute j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC' to '" + queryText + "'", "SELECT j FROM JobStatisticMinute j WHERE j.jobId = :jobId ORDER BY j.createdAt DESC", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticMinute.findLatestByJobId' - no result.
 *
 */
@org.junit.Test 
public void testFindLatestByJobIdEmptyResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticMinute.findLatestByJobId")).willReturn(query);
	@SuppressWarnings("rawtypes")
	List results = new ArrayList();
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	JobStatisticMinute result = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.findLatestByJobId(entityManager,jobId);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticMinute.findLatestByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertNull("Result should be null if list is empty", result);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticMinute.findLatestByJobId' - one result.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@org.junit.Test 
public void testFindLatestByJobIdOneResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticMinute.findLatestByJobId")).willReturn(query);
	List results = new java.util.ArrayList();
	JobStatisticMinute first = org.mockito.Mockito.mock(JobStatisticMinute.class);
	JobStatisticMinute second = org.mockito.Mockito.mock(JobStatisticMinute.class);
	results.add(first);
	results.add(second);
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	JobStatisticMinute result = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.findLatestByJobId(entityManager,jobId);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticMinute.findLatestByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertEquals("Result not the first of list.", first, result);
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticMinute.findLatestByJobId' still exist.
 *
 */
@org.junit.Test 
public void testFindLatestByJobIdVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[3][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][0] = "j.createdAt";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][2] = "createdAt";
	classesFieldsAndTypes[1][3] = "java.time.LocalDateTime";
	classesFieldsAndTypes[2][0] = "j.jobId";
	classesFieldsAndTypes[2][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.findLatestByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.findLatestByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.findLatestByJobId: The class "	+ className + " does not exist (anymore)");
	}
	}
}
/**
 * Tests that query 'JobStatisticMinute.summaryByJobId' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testSummaryByJobIdQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticMinute.summaryByJobId")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticMinute.summaryByJobId does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT new io.coodoo.workhorse.statistic.entity.JobStatisticSummary(SUM(j.durationCount), SUM(j.durationSum), MAX(j.durationMax), MIN(j.durationMin), AVG(j.durationAvg), SUM(j.finished), SUM(j.failed), SUM(j.schedule)) FROM JobStatisticMinute j WHERE j.jobId = :jobId AND j.from >= :from AND j.to <= :to' to '" + queryText + "'", "SELECT new io.coodoo.workhorse.statistic.entity.JobStatisticSummary(SUM(j.durationCount), SUM(j.durationSum), MAX(j.durationMax), MIN(j.durationMin), AVG(j.durationAvg), SUM(j.finished), SUM(j.failed), SUM(j.schedule)) FROM JobStatisticMinute j WHERE j.jobId = :jobId AND j.from >= :from AND j.to <= :to", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticMinute.summaryByJobId' - no result.
 *
 */
@org.junit.Test 
public void testSummaryByJobIdEmptyResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticMinute.summaryByJobId")).willReturn(query);
	@SuppressWarnings("rawtypes")
	List results = new ArrayList();
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	LocalDateTime from = null;
	org.mockito.BDDMockito.given(query.setParameter("from", from)).willReturn(query);
	LocalDateTime to = null;
	org.mockito.BDDMockito.given(query.setParameter("to", to)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	JobStatisticSummary result = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.summaryByJobId(entityManager,jobId, from, to);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticMinute.summaryByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("from",from);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("to",to);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertNull("Result should be null if list is empty", result);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticMinute.summaryByJobId' - one result.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@org.junit.Test 
public void testSummaryByJobIdOneResult()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticMinute.summaryByJobId")).willReturn(query);
	List results = new java.util.ArrayList();
	JobStatisticSummary first = org.mockito.Mockito.mock(JobStatisticSummary.class);
	JobStatisticSummary second = org.mockito.Mockito.mock(JobStatisticSummary.class);
	results.add(first);
	results.add(second);
	org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
	Long jobId = java.lang.Long.valueOf(0);
	org.mockito.BDDMockito.given(query.setParameter("jobId", jobId)).willReturn(query);
	LocalDateTime from = null;
	org.mockito.BDDMockito.given(query.setParameter("from", from)).willReturn(query);
	LocalDateTime to = null;
	org.mockito.BDDMockito.given(query.setParameter("to", to)).willReturn(query);
	org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
	// Call
	JobStatisticSummary result = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.summaryByJobId(entityManager,jobId, from, to);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticMinute.summaryByJobId");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobId",jobId);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("from",from);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("to",to);
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
	org.junit.Assert.assertEquals("Result not the first of list.", first, result);
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticMinute.summaryByJobId' still exist.
 *
 */
@org.junit.Test 
public void testSummaryByJobIdVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[12][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][0] = "j.durationAvg";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][2] = "durationAvg";
	classesFieldsAndTypes[1][3] = "java.lang.Long";
	classesFieldsAndTypes[2][0] = "j.durationCount";
	classesFieldsAndTypes[2][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[2][2] = "durationCount";
	classesFieldsAndTypes[2][3] = "java.lang.Integer";
	classesFieldsAndTypes[3][0] = "j.durationMax";
	classesFieldsAndTypes[3][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[3][2] = "durationMax";
	classesFieldsAndTypes[3][3] = "java.lang.Long";
	classesFieldsAndTypes[4][0] = "j.durationMin";
	classesFieldsAndTypes[4][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[4][2] = "durationMin";
	classesFieldsAndTypes[4][3] = "java.lang.Long";
	classesFieldsAndTypes[5][0] = "j.durationSum";
	classesFieldsAndTypes[5][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[5][2] = "durationSum";
	classesFieldsAndTypes[5][3] = "java.lang.Long";
	classesFieldsAndTypes[6][0] = "j.failed";
	classesFieldsAndTypes[6][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[6][2] = "failed";
	classesFieldsAndTypes[6][3] = "java.lang.Integer";
	classesFieldsAndTypes[7][0] = "j.finished";
	classesFieldsAndTypes[7][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[7][2] = "finished";
	classesFieldsAndTypes[7][3] = "java.lang.Integer";
	classesFieldsAndTypes[8][0] = "j.from";
	classesFieldsAndTypes[8][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[8][2] = "from";
	classesFieldsAndTypes[8][3] = "java.time.LocalDateTime";
	classesFieldsAndTypes[9][0] = "j.jobId";
	classesFieldsAndTypes[9][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[9][2] = "jobId";
	classesFieldsAndTypes[9][3] = "java.lang.Long";
	classesFieldsAndTypes[10][0] = "j.schedule";
	classesFieldsAndTypes[10][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[10][2] = "schedule";
	classesFieldsAndTypes[10][3] = "java.lang.Integer";
	classesFieldsAndTypes[11][0] = "j.to";
	classesFieldsAndTypes[11][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[11][2] = "to";
	classesFieldsAndTypes[11][3] = "java.time.LocalDateTime";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.summaryByJobId: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.summaryByJobId: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.summaryByJobId: The class "	+ className + " does not exist (anymore)");
	}
	}
	Object[][] constructors = new Object[1][2];
	constructors[0][0] = "io.coodoo.workhorse.statistic.entity.JobStatisticSummary";
	constructors[0][1] = 8;
	for (Object[] constructor : constructors) {
	String className = (String)constructor[0];
	int paramCount = (int)constructor[1];
	try {
	boolean foundExpectedSizeConstructor = false;
	Class<?> clazz = Class.forName(className);
	java.lang.reflect.Constructor<?>[] constrs = clazz.getConstructors();
	for (java.lang.reflect.Constructor<?> constr : constrs) {
	if (constr.getParameterTypes().length == paramCount) {
	foundExpectedSizeConstructor = true;
	break;
	}
	}
	if (!foundExpectedSizeConstructor) {
	org.junit.Assert.fail("Could not find matching constructor of class " + className + " having " + paramCount + " arguments for query JobStatisticMinute.summaryByJobId");
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking class " + className + " in query JobStatisticMinute.summaryByJobId: The constructed class " + className + " does not exist (anymore)");
	}
	}
}
/**
 * Tests that query 'JobStatisticMinute.deleteOlderThanDate' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods created from that query as they may be out-dated.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "null"})
@org.junit.Test 
public void testDeleteOlderThanDateQueryUnchanged()
{
	List annotations = new ArrayList();
	NamedQuery namedQueryAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQuery.class);
	if (namedQueryAnnotation == null) {
	NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.statistic.entity.JobStatisticMinute.class.getAnnotation(NamedQueries.class);
	if (namedQueriesAnnotation != null) {
	annotations.addAll(Arrays.asList(namedQueriesAnnotation.value())); }
	} else { annotations.add(namedQueryAnnotation); }
	NamedQuery queryUnderTest = null;
	for (Object obj : annotations) {
	NamedQuery query = (NamedQuery) obj;
	if (query.name().equals("JobStatisticMinute.deleteOlderThanDate")) {
	queryUnderTest = query;
	break;
	}
	}
	if (queryUnderTest == null) {
	org.junit.Assert.fail("Query JobStatisticMinute.deleteOlderThanDate does not exist anymore.");
	}
	String queryText = queryUnderTest.query();
	// Minor changes with whitespace are ignored
	queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
	while (queryText.contains("  ")) {
	queryText = queryText.replace("  ", " ");
	}
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'DELETE FROM JobStatisticMinute j WHERE j.from < :date' to '" + queryText + "'", "DELETE FROM JobStatisticMinute j WHERE j.from < :date", queryText);
}
/**
 * Tests that call and query are consistent for query 'JobStatisticMinute.deleteOlderThanDate'.
 *
 */
@org.junit.Test 
public void testDeleteOlderThanDate()
{
	Query query = org.mockito.Mockito.mock(Query.class);
	EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
	org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobStatisticMinute.deleteOlderThanDate")).willReturn(query);
	LocalDateTime date = null;
	org.mockito.BDDMockito.given(query.setParameter("date", date)).willReturn(query);
	// Call
	io.coodoo.workhorse.statistic.entity.JobStatisticMinute.deleteOlderThanDate(entityManager,date);
	// Verification
	org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobStatisticMinute.deleteOlderThanDate");
	org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("date",date);
	org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
}
/**
 * Tests that all classes and members/fields used in query 'JobStatisticMinute.deleteOlderThanDate' still exist.
 *
 */
@org.junit.Test 
public void testDeleteOlderThanDateVerifyFields()
{
	String[][] classesFieldsAndTypes = new String[2][4];
	classesFieldsAndTypes[0][0] = "j";
	classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][0] = "j.from";
	classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.statistic.entity.JobStatisticMinute";
	classesFieldsAndTypes[1][2] = "from";
	classesFieldsAndTypes[1][3] = "java.time.LocalDateTime";
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
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.deleteOlderThanDate: The field " + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
	}
	fieldFound = true;
	break;
	}
	}
	clazz = clazz.getSuperclass();
	} while (!fieldFound && clazz != null);
	if (!fieldFound) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.deleteOlderThanDate: The field " + className + "." + fieldName + " does not exist (anymore)");
	}
	}
	} catch (ClassNotFoundException e) {
	org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobStatisticMinute.deleteOlderThanDate: The class "	+ className + " does not exist (anymore)");
	}
	}
}
}