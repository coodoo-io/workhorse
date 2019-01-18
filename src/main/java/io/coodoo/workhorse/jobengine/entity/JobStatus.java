package io.coodoo.workhorse.jobengine.entity;

import io.coodoo.workhorse.jobengine.boundary.JobWorker;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public enum JobStatus {

    /**
     * Job is in service
     */
    ACTIVE,

    /**
     * Job is not in service
     */
    INACTIVE,

    /**
     * Error occurred while processing the job
     */
    ERROR,

    /**
     * The {@link JobWorker} implementation is missing
     */
    NO_WORKER,

    ;

}
