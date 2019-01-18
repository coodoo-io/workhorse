package io.coodoo.workhorse.jobengine.control.event;

import io.coodoo.workhorse.jobengine.entity.Job;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class AllJobsDoneEvent {

    private Job job;

    public AllJobsDoneEvent(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
