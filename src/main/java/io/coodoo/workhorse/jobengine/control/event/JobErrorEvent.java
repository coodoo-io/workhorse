package io.coodoo.workhorse.jobengine.control.event;

import io.coodoo.workhorse.jobengine.entity.Job;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobErrorEvent {

    private Job job;

    private Throwable throwable;

    public JobErrorEvent(Job job, Throwable throwable) {
        super();
        this.job = job;
        this.throwable = throwable;
    }

    public Job getJob() {
        return job;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
