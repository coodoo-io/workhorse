package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import io.coodoo.workhorse.jobengine.control.BaseJobWorker;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.util.JobEngineUtil;

/**
 * Job worker class to define the creation and execution of jobs. Your job needs parameters? See {@link JobWorkerWith}!
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public abstract class JobWorker extends BaseJobWorker {

    public abstract void doWork() throws Exception;

    public void doWork(JobExecution jobExecution) throws Exception {

        this.jobContext.init(jobExecution);

        doWork();
    }

    /**
     * <i>This is an access point to get the job engine started with a new job execution.</i><br>
     * <br>
     * 
     * This creates a {@link JobExecution} object that gets added to the job engine to be executed as soon as possible.
     * 
     * @param priority priority queuing
     * @param maturity specified time for the execution
     * @return job execution ID
     */
    public Long createJobExecution(Boolean priority, LocalDateTime maturity) {
        return create(null, priority, maturity, null, null, null).getId();
    }

    /**
     * <i>This is an access point to get the job engine started with a new job execution.</i><br>
     * <br>
     * 
     * This creates a {@link JobExecution} object that gets added to the job engine to be executed as soon as possible.
     * 
     * @param priority priority queuing
     * @param delayValue time to wait
     * @param delayUnit what kind of time to wait
     * @return job execution ID
     */
    public Long createJobExecution(Boolean priority, Long delayValue, ChronoUnit delayUnit) {
        return create(null, priority, JobEngineUtil.delayToMaturity(delayValue, delayUnit), null, null, null).getId();
    }

    /**
     * <i>Convenience method to create a job execution</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the priority queue of the job engine to be treated first class.
     * 
     * @return job execution ID
     */
    public Long createPriorityJobExecution() {
        return create(null, true, null, null, null, null).getId();
    }

    /**
     * <i>Convenience method to create a job execution</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the job engine after the given delay.
     * 
     * @param delayValue time to wait
     * @param delayUnit what kind of time to wait
     * @return job execution ID
     */
    public Long createDelayedJobExecution(Long delayValue, ChronoUnit delayUnit) {
        return create(null, false, JobEngineUtil.delayToMaturity(delayValue, delayUnit), null, null, null).getId();
    }

    /**
     * <i>Convenience method to create a job execution</i><br>
     * <br>
     * This creates a {@link JobExecution} object that gets added to the job engine at a specified time.
     * 
     * @param maturity specified time for the execution
     * @return job execution ID
     */
    public Long createPlannedJobExecution(LocalDateTime maturity) {
        return create(null, false, maturity, null, null, null).getId();
    }

}
