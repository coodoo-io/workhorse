package io.coodoo.workhorse.jobengine.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;

import io.coodoo.framework.jpa.entity.AbstractIdOccCreatedUpdatedAtEntity;
import io.coodoo.framework.listing.boundary.annotation.ListingFilterAsString;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Entity
@Table(name = "jobengine_job")
@NamedQueries({

                @NamedQuery(name = "Job.getAll", query = "SELECT job FROM Job job"),
                @NamedQuery(name = "Job.getByName", query = "SELECT job FROM Job job WHERE job.name=:name"),
                @NamedQuery(name = "Job.getByWorkerClassName", query = "SELECT job FROM Job job WHERE job.workerClassName=:workerClassName"),
                @NamedQuery(name = "Job.getAllByStatus", query = "SELECT job FROM Job job WHERE job.status=:status"),
                @NamedQuery(name = "Job.countAllByStatus", query = "SELECT COUNT(job) FROM Job job WHERE job.status=:status"),
                @NamedQuery(name = "Job.getAllScheduled", query = "SELECT job FROM Job job WHERE job.schedule IS NOT NULL")

})
public class Job extends AbstractIdOccCreatedUpdatedAtEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    @Convert(converter = StringListConverter.class)
    @ListingFilterAsString
    private List<String> tags = new ArrayList<>();

    @Column(name = "worker_class_name")
    private String workerClassName;

    @Column(name = "parameters_class_name")
    private String parametersClassName;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "threads")
    private int threads;

    @Column(name = "max_per_minute")
    private Integer maxPerMinute;

    @Column(name = "fail_retries")
    private int failRetries;

    @Column(name = "retry_delay")
    private int retryDelay;

    @Column(name = "days_until_clean_up")
    private int daysUntilCleanUp;

    @Column(name = "unique_in_queue")
    private boolean uniqueInQueue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getWorkerClassName() {
        return workerClassName;
    }

    public void setWorkerClassName(String workerClassName) {
        this.workerClassName = workerClassName;
    }

    public String getParametersClassName() {
        return parametersClassName;
    }

    public void setParametersClassName(String parametersClassName) {
        this.parametersClassName = parametersClassName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public Integer getMaxPerMinute() {
        return maxPerMinute;
    }

    public void setMaxPerMinute(Integer maxPerMinute) {
        this.maxPerMinute = maxPerMinute;
    }

    public int getFailRetries() {
        return failRetries;
    }

    public void setFailRetries(int failRetries) {
        this.failRetries = failRetries;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public int getDaysUntilCleanUp() {
        return daysUntilCleanUp;
    }

    public void setDaysUntilCleanUp(int daysUntilCleanUp) {
        this.daysUntilCleanUp = daysUntilCleanUp;
    }

    public boolean isUniqueInQueue() {
        return uniqueInQueue;
    }

    public void setUniqueInQueue(boolean uniqueInQueue) {
        this.uniqueInQueue = uniqueInQueue;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        StringBuilder builder = new StringBuilder();
        builder.append("Job [id=");
        builder.append(id);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", updatedAt=");
        builder.append(updatedAt);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", tags=");
        builder.append(tags != null ? tags.subList(0, Math.min(tags.size(), maxLen)) : null);
        builder.append(", workerClassName=");
        builder.append(workerClassName);
        builder.append(", parametersClassName=");
        builder.append(parametersClassName);
        builder.append(", schedule=");
        builder.append(schedule);
        builder.append(", status=");
        builder.append(status);
        builder.append(", threads=");
        builder.append(threads);
        builder.append(", maxPerMinute=");
        builder.append(maxPerMinute);
        builder.append(", failRetries=");
        builder.append(failRetries);
        builder.append(", retryDelay=");
        builder.append(retryDelay);
        builder.append(", daysUntilCleanUp=");
        builder.append(daysUntilCleanUp);
        builder.append(", uniqueInQueue=");
        builder.append(uniqueInQueue);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Executes the query 'Job.getAllByStatus' returning a list of result objects.
     *
     * @param entityManager the entityManager
     * @param status the status
     * @return List of result objects
     */
    @SuppressWarnings("unchecked")
    public static List<Job> getAllByStatus(EntityManager entityManager, JobStatus status) {
        Query query = entityManager.createNamedQuery("Job.getAllByStatus");
        query = query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Executes the query 'Job.getByName' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param name the name
     * @return the result
     */
    public static Job getByName(EntityManager entityManager, String name) {
        Query query = entityManager.createNamedQuery("Job.getByName");
        query = query.setParameter("name", name);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (Job) results.get(0);
    }

    /**
     * Executes the query 'Job.getByWorkerClassName' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param workerClassName the workerClassName
     * @return the result
     */
    public static Job getByWorkerClassName(EntityManager entityManager, String workerClassName) {
        Query query = entityManager.createNamedQuery("Job.getByWorkerClassName");
        query = query.setParameter("workerClassName", workerClassName);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (Job) results.get(0);
    }

    /**
     * Executes the query 'Job.getAll' returning a list of result objects.
     *
     * @param entityManager the entityManager
     * @return List of result objects
     */
    @SuppressWarnings("unchecked")
    public static List<Job> getAll(EntityManager entityManager) {
        Query query = entityManager.createNamedQuery("Job.getAll");
        return query.getResultList();
    }

    /**
     * Executes the query 'Job.countAllByStatus' returning one/the first object or null if nothing has been found.
     *
     * @param entityManager the entityManager
     * @param status the status
     * @return the result
     */
    public static Long countAllByStatus(EntityManager entityManager, JobStatus status) {
        Query query = entityManager.createNamedQuery("Job.countAllByStatus");
        query = query.setParameter("status", status);
        query = query.setMaxResults(1);
        @SuppressWarnings("rawtypes")
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return (Long) results.get(0);
    }

    /**
     * Executes the query 'Job.getAllScheduled' returning a list of result objects.
     *
     * @param entityManager the entityManager
     * @return List of result objects
     */
    @SuppressWarnings("unchecked")
    public static List<Job> getAllScheduled(EntityManager entityManager) {
        Query query = entityManager.createNamedQuery("Job.getAllScheduled");
        return query.getResultList();
    }

}
