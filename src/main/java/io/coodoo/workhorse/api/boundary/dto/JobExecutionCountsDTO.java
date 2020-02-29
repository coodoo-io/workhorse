package io.coodoo.workhorse.api.boundary.dto;

/**
 * @deprecated Got rid of entity <code>JobExecutionCounts</code> for using native MySql syntax and therefore also
 *             <code>JobEngineApiService.getJobExecutionCounts</code>. To provide this functionality we will use the <em>terms</em> and <em>stats</em> feature
 *             of coodoo-listing v1.6.0
 */
@Deprecated
public class JobExecutionCountsDTO {

    public Long total;
    public Long queued;
    public Long running;
    public Long finished;
    public Long failed;
    public Long aborted;
    public Long averageDuration;

}
