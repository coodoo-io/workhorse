package io.coodoo.workhorse.jobengine.entity;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public enum JobExecutionStatus {

    /**
     * Execution is queued for processing.
     */
    QUEUED,

    /**
     * Execution is currently running, the doWork() Method is called.
     */
    RUNNING,

    /**
     * Execution finished without any error.
     */
    FINISHED,

    /**
     * The Execution failed because of an error.
     */
    FAILED,

    /**
     * The Execution was aborted by an user.
     */
    ABORTED;

}
