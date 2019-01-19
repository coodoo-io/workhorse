package io.coodoo.workhorse.jobengine.control.event;

import io.coodoo.workhorse.jobengine.entity.Job;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class AllJobExecutionsDoneEvent {

    private Job job;

    public AllJobExecutionsDoneEvent(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
