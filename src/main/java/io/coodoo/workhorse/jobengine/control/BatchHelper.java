package io.coodoo.workhorse.jobengine.control;

import java.time.LocalDateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import io.coodoo.workhorse.jobengine.boundary.JobEngineService;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.util.JobEngineUtil;

/**
 * To do this kind of hacks, we need some basic transaction management <br>
 * TODO we need some better mechanism for this...
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class BatchHelper {

    @Inject
    JobEngineService jobEngineService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public JobExecution createFirstInBatch(Long jobId, Object parameters, Boolean priority, LocalDateTime maturity, boolean uniqueInQueue) {

        String parametersJson = JobEngineUtil.parametersToJson(parameters);

        // mark as batch with pseudo ID (-1), so the poller wont draft it to early
        return jobEngineService.createJobExecution(jobId, parametersJson, priority, maturity, -1L, null, null, uniqueInQueue);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void activateFirstInBatch(Long jobExecutionId) {

        // remove pseudo IDs (-1), so the poller can draft it
        JobExecution jobExecution = jobEngineService.getJobExecutionById(jobExecutionId);
        jobExecution.setBatchId(jobExecutionId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public JobExecution createFirstInChain(Long jobId, Object parameters, Boolean priority, LocalDateTime maturity, boolean uniqueInQueue) {

        String parametersJson = JobEngineUtil.parametersToJson(parameters);

        // mark as chained with pseudo IDs (-1), so the poller wont draft it to early
        return jobEngineService.createJobExecution(jobId, parametersJson, priority, maturity, null, -1L, -1L, uniqueInQueue);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void activateFirstInChain(Long jobExecutionId) {

        // remove pseudo IDs (-1), so the poller can draft it
        JobExecution jobExecution = jobEngineService.getJobExecutionById(jobExecutionId);
        jobExecution.setChainId(jobExecutionId);
        jobExecution.setChainPreviousExecutionId(null);
    }

}
