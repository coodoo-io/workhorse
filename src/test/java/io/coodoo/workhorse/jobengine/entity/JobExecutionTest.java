package io.coodoo.workhorse.jobengine.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

public class JobExecutionTest {
    /**
     * Tests that query 'JobExecution.updateStatusRunning' has not changed since this test had been created. If this test fails, you should consider
     * re-generating ALL methods created from that query as they may be out-dated.
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes", "null"})
    @org.junit.Test
    public void testUpdateStatusRunningQueryUnchanged() {
        List annotations = new ArrayList();
        NamedQuery namedQueryAnnotation = io.coodoo.workhorse.jobengine.entity.JobExecution.class.getAnnotation(NamedQuery.class);
        if (namedQueryAnnotation == null) {
            NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.jobengine.entity.JobExecution.class.getAnnotation(NamedQueries.class);
            if (namedQueriesAnnotation != null) {
                annotations.addAll(Arrays.asList(namedQueriesAnnotation.value()));
            }
        } else {
            annotations.add(namedQueryAnnotation);
        }
        NamedQuery queryUnderTest = null;
        for (Object obj : annotations) {
            NamedQuery query = (NamedQuery) obj;
            if (query.name().equals("JobExecution.updateStatusRunning")) {
                queryUnderTest = query;
                break;
            }
        }
        if (queryUnderTest == null) {
            org.junit.Assert.fail("Query JobExecution.updateStatusRunning does not exist anymore.");
        }
        String queryText = queryUnderTest.query();
        // Minor changes with whitespace are ignored
        queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
        while (queryText.contains("  ")) {
            queryText = queryText.replace("  ", " ");
        }
        org.junit.Assert.assertEquals(
                        "There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'UPDATE JobExecution j SET j.status = 'RUNNING', j.startedAt = :startedAt, j.updatedAt = :startedAt WHERE j.id = :jobExecutionId' to '"
                                        + queryText + "'",
                        "UPDATE JobExecution j SET j.status = 'RUNNING', j.startedAt = :startedAt, j.updatedAt = :startedAt WHERE j.id = :jobExecutionId",
                        queryText);
    }

    /**
     * Tests that call and query are consistent for query 'JobExecution.updateStatusRunning'.
     *
     */
    @org.junit.Test
    public void testUpdateStatusRunning() {
        Query query = org.mockito.Mockito.mock(Query.class);
        EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
        org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobExecution.updateStatusRunning")).willReturn(query);
        LocalDateTime startedAt = null;
        org.mockito.BDDMockito.given(query.setParameter("startedAt", startedAt)).willReturn(query);
        Long jobExecutionId = java.lang.Long.valueOf(1);
        org.mockito.BDDMockito.given(query.setParameter("jobExecutionId", jobExecutionId)).willReturn(query);
        // Call
        io.coodoo.workhorse.jobengine.entity.JobExecution.updateStatusRunning(entityManager, startedAt, jobExecutionId);
        // Verification
        org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobExecution.updateStatusRunning");
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("startedAt", startedAt);
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobExecutionId", jobExecutionId);
        org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
    }

    /**
     * Tests that all classes and members/fields used in query 'JobExecution.updateStatusRunning' still exist.
     *
     */
    @org.junit.Test
    public void testUpdateStatusRunningVerifyFields() {
        String[][] classesFieldsAndTypes = new String[5][4];
        classesFieldsAndTypes[0][0] = "j";
        classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[1][0] = "j.id";
        classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[1][2] = "id";
        classesFieldsAndTypes[1][3] = "java.lang.Long";
        classesFieldsAndTypes[2][0] = "j.startedAt";
        classesFieldsAndTypes[2][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[2][2] = "startedAt";
        classesFieldsAndTypes[2][3] = "java.time.LocalDateTime";
        classesFieldsAndTypes[3][0] = "j.status";
        classesFieldsAndTypes[3][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[3][2] = "status";
        classesFieldsAndTypes[3][3] = "io.coodoo.workhorse.jobengine.entity.JobExecutionStatus";
        classesFieldsAndTypes[4][0] = "j.updatedAt";
        classesFieldsAndTypes[4][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[4][2] = "updatedAt";
        classesFieldsAndTypes[4][3] = "java.time.LocalDateTime";
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
                                    org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobExecution.updateStatusRunning: The field "
                                                    + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
                                }
                                fieldFound = true;
                                break;
                            }
                        }
                        clazz = clazz.getSuperclass();
                    } while (!fieldFound && clazz != null);
                    if (!fieldFound) {
                        org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobExecution.updateStatusRunning: The field " + className + "."
                                        + fieldName + " does not exist (anymore)");
                    }
                }
            } catch (ClassNotFoundException e) {
                org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobExecution.updateStatusRunning: The class " + className
                                + " does not exist (anymore)");
            }
        }
    }

    /**
     * Tests that query 'JobExecution.updateStatusFinished' has not changed since this test had been created. If this test fails, you should consider
     * re-generating ALL methods created from that query as they may be out-dated.
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes", "null"})
    @org.junit.Test
    public void testUpdateStatusFinishedQueryUnchanged() {
        List annotations = new ArrayList();
        NamedQuery namedQueryAnnotation = io.coodoo.workhorse.jobengine.entity.JobExecution.class.getAnnotation(NamedQuery.class);
        if (namedQueryAnnotation == null) {
            NamedQueries namedQueriesAnnotation = io.coodoo.workhorse.jobengine.entity.JobExecution.class.getAnnotation(NamedQueries.class);
            if (namedQueriesAnnotation != null) {
                annotations.addAll(Arrays.asList(namedQueriesAnnotation.value()));
            }
        } else {
            annotations.add(namedQueryAnnotation);
        }
        NamedQuery queryUnderTest = null;
        for (Object obj : annotations) {
            NamedQuery query = (NamedQuery) obj;
            if (query.name().equals("JobExecution.updateStatusFinished")) {
                queryUnderTest = query;
                break;
            }
        }
        if (queryUnderTest == null) {
            org.junit.Assert.fail("Query JobExecution.updateStatusFinished does not exist anymore.");
        }
        String queryText = queryUnderTest.query();
        // Minor changes with whitespace are ignored
        queryText = queryText.trim().replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
        while (queryText.contains("  ")) {
            queryText = queryText.replace("  ", " ");
        }
        org.junit.Assert.assertEquals(
                        "There's a change in the query string. Generated methods may not fit to the query anymore. Change from 'UPDATE JobExecution j SET j.status = 'FINISHED', j.endedAt = :endedAt, j.duration = :duration, j.log = :log, j.updatedAt = :endedAt WHERE j.id = :jobExecutionId' to '"
                                        + queryText + "'",
                        "UPDATE JobExecution j SET j.status = 'FINISHED', j.endedAt = :endedAt, j.duration = :duration, j.log = :log, j.updatedAt = :endedAt WHERE j.id = :jobExecutionId",
                        queryText);
    }

    /**
     * Tests that call and query are consistent for query 'JobExecution.updateStatusFinished'.
     *
     */
    @org.junit.Test
    public void testUpdateStatusFinished() {
        Query query = org.mockito.Mockito.mock(Query.class);
        EntityManager entityManager = org.mockito.Mockito.mock(EntityManager.class);
        org.mockito.BDDMockito.given(entityManager.createNamedQuery("JobExecution.updateStatusFinished")).willReturn(query);
        LocalDateTime endedAt = null;
        org.mockito.BDDMockito.given(query.setParameter("endedAt", endedAt)).willReturn(query);
        Long duration = java.lang.Long.valueOf(1);
        org.mockito.BDDMockito.given(query.setParameter("duration", duration)).willReturn(query);
        String log = "2";
        org.mockito.BDDMockito.given(query.setParameter("log", log)).willReturn(query);
        Long jobExecutionId = java.lang.Long.valueOf(3);
        org.mockito.BDDMockito.given(query.setParameter("jobExecutionId", jobExecutionId)).willReturn(query);
        // Call
        io.coodoo.workhorse.jobengine.entity.JobExecution.updateStatusFinished(entityManager, endedAt, duration, log, jobExecutionId);
        // Verification
        org.mockito.BDDMockito.verify(entityManager, org.mockito.Mockito.times(1)).createNamedQuery("JobExecution.updateStatusFinished");
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("endedAt", endedAt);
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("duration", duration);
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("log", log);
        org.mockito.BDDMockito.verify(query, org.mockito.Mockito.times(1)).setParameter("jobExecutionId", jobExecutionId);
        org.mockito.BDDMockito.verify(query, org.mockito.BDDMockito.times(1)).executeUpdate();
    }

    /**
     * Tests that all classes and members/fields used in query 'JobExecution.updateStatusFinished' still exist.
     *
     */
    @org.junit.Test
    public void testUpdateStatusFinishedVerifyFields() {
        String[][] classesFieldsAndTypes = new String[7][4];
        classesFieldsAndTypes[0][0] = "j";
        classesFieldsAndTypes[0][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[1][0] = "j.duration";
        classesFieldsAndTypes[1][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[1][2] = "duration";
        classesFieldsAndTypes[1][3] = "java.lang.Long";
        classesFieldsAndTypes[2][0] = "j.endedAt";
        classesFieldsAndTypes[2][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[2][2] = "endedAt";
        classesFieldsAndTypes[2][3] = "java.time.LocalDateTime";
        classesFieldsAndTypes[3][0] = "j.id";
        classesFieldsAndTypes[3][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[3][2] = "id";
        classesFieldsAndTypes[3][3] = "java.lang.Long";
        classesFieldsAndTypes[4][0] = "j.log";
        classesFieldsAndTypes[4][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[4][2] = "log";
        classesFieldsAndTypes[4][3] = "java.lang.String";
        classesFieldsAndTypes[5][0] = "j.status";
        classesFieldsAndTypes[5][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[5][2] = "status";
        classesFieldsAndTypes[5][3] = "io.coodoo.workhorse.jobengine.entity.JobExecutionStatus";
        classesFieldsAndTypes[6][0] = "j.updatedAt";
        classesFieldsAndTypes[6][1] = "io.coodoo.workhorse.jobengine.entity.JobExecution";
        classesFieldsAndTypes[6][2] = "updatedAt";
        classesFieldsAndTypes[6][3] = "java.time.LocalDateTime";
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
                                    org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobExecution.updateStatusFinished: The field "
                                                    + clazz.getName() + "." + field + " does not have the type " + fieldType + " (anymore)");
                                }
                                fieldFound = true;
                                break;
                            }
                        }
                        clazz = clazz.getSuperclass();
                    } while (!fieldFound && clazz != null);
                    if (!fieldFound) {
                        org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobExecution.updateStatusFinished: The field " + className + "."
                                        + fieldName + " does not exist (anymore)");
                    }
                }
            } catch (ClassNotFoundException e) {
                org.junit.Assert.fail("Error checking path " + fieldPath + " in query JobExecution.updateStatusFinished: The class " + className
                                + " does not exist (anymore)");
            }
        }
    }
}
