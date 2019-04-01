package io.coodoo.workhorse.jobengine.boundary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.coodoo.workhorse.jobengine.boundary.JobWorker;
import io.coodoo.workhorse.jobengine.boundary.JobWorkerWith;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.JobType;

/**
 * <strong>Initial job configuration.</strong> An new {@link JobWorker} or {@link JobWorkerWith} implementation will be detected while initialization and gets
 * written into the database. This annotation can be used to provide initial configuration to the resulting {@link Job}.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitialJobConfig {

    public static final int JOB_CONFIG_THREADS = 1;
    public static final int JOB_CONFIG_MAX_PER_MINUTE = 0;
    public static final int JOB_CONFIG_FAIL_RETRIES = 0;
    public static final int JOB_CONFIG_RETRY_DELAY = 4000;
    public static final int JOB_CONFIG_DAYS_UNTIL_CLEANUP = 30;
    public static final boolean JOB_CONFIG_UNIQUE_IN_QUEUE = true;

    /**
     * @return Unique name of the job
     */
    String name() default "";

    /**
     * @return A readable description of the job purpose.
     */
    String description() default "";

    /**
     * @return A comma-separated tag list to help organize the jobs.
     */
    String tags() default "";

    /**
     * @return Initial Status of the Job. Default is ACTIVE.
     */
    JobStatus status() default JobStatus.ACTIVE;

    /**
     * @return Unix-like CRON expressions to provide scheduled job executions. If this is set, the job will be automatically of type {@link JobType#SCHEDULED}
     */
    String schedule() default "";

    /**
     * @return Number of threads for processing parallel work. Default is 1.
     */
    int threads() default JOB_CONFIG_THREADS;

    /**
     * @return Limit of execution throughput per minute. Default is null (no limitation)
     */
    int maxPerMinute() default JOB_CONFIG_MAX_PER_MINUTE;

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
