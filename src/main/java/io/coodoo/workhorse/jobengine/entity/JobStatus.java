package io.coodoo.workhorse.jobengine.entity;

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
     * Error occurred while checking or processing the job
     */
    ERROR;

}
