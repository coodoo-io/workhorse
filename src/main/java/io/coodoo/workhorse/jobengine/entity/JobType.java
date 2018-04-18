package io.coodoo.workhorse.jobengine.entity;

public enum JobType {

    /**
     * Job executions will go directly into the queue for rapid execution
     */
    ON_DEMAND,

    /**
     * Job executions will go into the queue by schedule
     */
    SCHEDULED,

    /**
     * Organized job executions // TODO
     */
    BATCH,

    /**
     * Job engine internal system
     */
    SYSTEM;

    public boolean hasSchedule() {
        switch (this) {
            case SCHEDULED:
            case SYSTEM:
                return true;
            default:
                return false;
        }
    }

}
