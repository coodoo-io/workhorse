package io.coodoo.workhorse.api.dto;

import io.coodoo.framework.jpa.boundary.entity.dto.RevisionDatesEntityDTO;
import io.coodoo.workhorse.jobengine.entity.JobStatistic;

/**
 * @author coodoo GmbH (coodoo.io)
 */
public class JobStatisticDTO extends RevisionDatesEntityDTO {

    public Long jobId;
    public Long durationAvg;
    public Long durationMedian;
    public Integer queued;
    public Integer finished;
    public Integer failed;
    public Integer schedule;

    public JobStatisticDTO() {}

    public JobStatisticDTO(JobStatistic jobStatistic) {
        super(jobStatistic);
        this.jobId = jobStatistic.getJobId();
        this.durationAvg = jobStatistic.getDurationAvg();
        this.durationMedian = jobStatistic.getDurationMedian();
        this.queued = jobStatistic.getQueued();
        this.finished = jobStatistic.getFinished();
        this.failed = jobStatistic.getFailed();
        this.schedule = jobStatistic.getSchedule();
    }

}
