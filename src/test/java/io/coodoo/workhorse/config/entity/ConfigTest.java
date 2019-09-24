package io.coodoo.workhorse.config.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

public class ConfigTest {
    /**
     * Tests that query 'Config.getConfig' has not changed since this test had been created. If this test fails, you should consider re-generating ALL methods
     * created from that query as they may be out-dated.
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes", "null"})
    @org.junit.Test
    public void testGetConfigQueryUnchanged() {
        List annotations = new ArrayList();
        NamedQuery namedQueryAnnotation = io.coodoo.workhorse.config.entity.Config.class.getAnnotation(NamedQuery.class);
        if (namedQueryAnnotation == null) {
            NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.config.entity.Config.class.getAnnotation(NamedQueries.class);
            if (namedQueriesAnnotation != null) {
                annotations.addAll(Arrays.asList(namedQueriesAnnotation.value()));
            }
        } else {
            annotations.add(namedQueryAnnotation);
        }
        NamedQuery queryUnderTest = null;
        for (Object obj : annotations) {
            NamedQuery query = (NamedQuery) obj;
            if (query.name().equals("Config.getConfig")) {
                queryUnderTest = query;
                break;
            }
        }
        if (queryUnderTest == null) {
            org.junit.Assert.fail("Query Config.getConfig does not exist anymore.");
        }
        String queryText = queryUnderTest.query();
        // Minor changes with whitespace are ignored
        queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
        while (queryText.contains("  ")) {
            queryText = queryText.replace("  ", " ");
        }
        org.junit.Assert.assertEquals(
                        "There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'SELECT c FROM Config c' to '"
                                        + queryText + "'",
                        "SELECT c FROM Config c", queryText);
    }

    /**
     * Tests that call and query are consistent for query 'Config.getConfig' - no result.
     *
     */
    @org.junit.Test
    public void testGetConfigEmptyResult() {
        Query query = org.mockito.Mockito.mock(Query.class);
        EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
        org.mockito.BDDMockito.given(entityManager.createNamedQuery("Config.getConfig")).willReturn(query);
        @SuppressWarnings("rawtypes")
        List results = new ArrayList();
        org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
        org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
        // Call
        Config result = io.coodoo.workhorse.config.entity.Config.getConfig(entityManager);
        // Verification
        org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("Config.getConfig");
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
        org.junit.Assert.assertNull("Result should be null if list is empty", result);
    }

    /**
     * Tests that call and query are consistent for query 'Config.getConfig' - one result.
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @org.junit.Test
    public void testGetConfigOneResult() {
        Query query = org.mockito.Mockito.mock(Query.class);
        EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
        org.mockito.BDDMockito.given(entityManager.createNamedQuery("Config.getConfig")).willReturn(query);
        List results = new java.util.ArrayList();
        Config first = org.mockito.Mockito.mock(Config.class);
        Config second = org.mockito.Mockito.mock(Config.class);
        results.add(first);
        results.add(second);
        org.mockito.BDDMockito.given(query.getResultList()).willReturn(results);
        org.mockito.BDDMockito.given(query.setMaxResults(1)).willReturn(query);
        // Call
        Config result = io.coodoo.workhorse.config.entity.Config.getConfig(entityManager);
        // Verification
        org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("Config.getConfig");
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).getResultList();
        org.junit.Assert.assertEquals("Result not the first of list.", first, result);
    }
}
