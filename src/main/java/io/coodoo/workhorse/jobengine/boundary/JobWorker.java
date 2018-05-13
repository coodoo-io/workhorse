package io.coodoo.workhorse.jobengine.boundary;

import io.coodoo.workhorse.jobengine.control.BaseJobWorker;
import io.coodoo.workhorse.jobengine.entity.JobExecution;

/**
 * Job worker class to define the creation and execution of jobs.
 * 
 * @author coodoo GmbH (coodoo.io)
 */
public abstract class JobWorker extends BaseJobWorker {

    public abstract void doWork() throws Exception;

    public void doWork(JobExecution jobExecution) throws Exception {

        this.jobContext.init(jobExecution);

        doWork();
    }

}
