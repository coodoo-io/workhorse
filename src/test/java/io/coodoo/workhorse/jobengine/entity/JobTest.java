package io.coodoo.workhorse.jobengine.entity;

import javax.persistence.*;
import java.util.*;
import io.coodoo.workhorse.jobengine.entity.Job;
public class JobTest {
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
	org.junit.Assert.assertEquals("There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT job FROM Job job WHERE job.type=io.coodoo.workhorse.jobengine.entity.JobType.SCHEDULED AND job.schedule IS NOT NULL' to '" + queryText + "'", "SELECT job FROM Job job WHERE job.type=io.coodoo.workhorse.jobengine.entity.JobType.SCHEDULED AND job.schedule IS NOT NULL", queryText);
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
}