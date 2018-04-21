package io.coodoo.workhorse.jobengine.boundary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.coodoo.workhorse.jobengine.entity.JobStatus;

/**
 * Initial / default job settings
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JobConfig {

    public static final String JOB_CONFIG_DESCRIPTION = "";
    public static final int JOB_CONFIG_THREADS = 1;
    public static final int JOB_CONFIG_FAIL_RETRIES = 0;
    public static final int JOB_CONFIG_RETRY_DELAY = 4000;
    public static final int JOB_CONFIG_DAYS_UNTIL_CLEANUP = 30;
    public static final boolean JOB_CONFIG_UNIQUE_IN_QUEUE = true;

    /**
     * @return Unique name of the job
     */
    String name();

    /**
     * @return A readable description of the job purpose.
     */
    String description() default JOB_CONFIG_DESCRIPTION;

    /**
     * @return Initial Status of the Job. Default is ACTIVE.
     */
    JobStatus status() default JobStatus.ACTIVE;

    /**
     * @return Number of threads for processing parallel work. Default is 1.
     */
    int threads() default JOB_CONFIG_THREADS;

    /**
     * @return Number of retries after the job faild by an exception. Default value is 0 (no retries).
     */
    int failRetries() default JOB_CONFIG_FAIL_RETRIES;

    /**
     * @return Delay to start a retry after a failed job exception. Default is 4000 milliseconds.
     */
    int retryDelay() default JOB_CONFIG_RETRY_DELAY;

    /**
     * @return Number of days after the job executions get deleted. Default is 30 days, if set to 0 no job execution will get deleted.
     */
    int daysUntilCleanUp() default JOB_CONFIG_DAYS_UNTIL_CLEANUP;

    /**
     * @return If <code>true</code> (default) a new job execution will only be added and saved into the queue if no other job with the same parameters exists in
     *         queue.
     */
    boolean uniqueInQueue() default JOB_CONFIG_UNIQUE_IN_QUEUE;

}
