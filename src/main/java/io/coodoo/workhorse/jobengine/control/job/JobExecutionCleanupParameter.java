package io.coodoo.workhorse.jobengine.control.job;

import io.coodoo.workhorse.jobengine.boundary.JobExecutionParameters;

public class JobExecutionCleanupParameter implements JobExecutionParameters {

    public Long jobId;

    public String jobName;

    public int minDaysOld;

    public JobExecutionCleanupParameter() {}

    public JobExecutionCleanupParameter(Long jobId, String jobName, int minDaysOld) {
        super();
        this.jobId = jobId;
        this.jobName = jobName;
        this.minDaysOld = minDaysOld;
    }

}
