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

    /**
     * Unique name of the job
     */
    String name();

    /**
     * A readable description of the job purpose.
     */
    String description();

    /**
     * Initial Status of the Job. Default is ACTIVE.
     */
    JobStatus status() default JobStatus.ACTIVE;

    /**
     * Number of threads for processing parallel work. Default is 1.
     */
    int threads() default 1;

    /**
     * Number of retries after the job faild by an exception. Default value is 0 (no retries).
     */
    int failRetries() default 0;

    /**
     * Delay to start a retry after a failed job exception. Default is 4000 milliseconds.
     */
    int retryDelay() default 4000;

    /**
     * Number of days after the job executions get deleted. Default is 30 days, if set to 0 no job execution will get deleted.
     */
    int daysUntilCLeanUp() default 30;

    /**
     * If <code>true</code> (default) a new job execution will only be added and saved into the queue if no other job with the same parameters exists in queue.
     */
    boolean uniqueInQueue() default true;

}
