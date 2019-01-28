package io.coodoo.workhorse.jobengine.control;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.coodoo.workhorse.jobengine.entity.Job;

/**
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobExecutor {

    private static Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<Long> execute(Job job, JobThread jobThread) {

        MDC.put("key", job.getName());

        long t1 = System.currentTimeMillis();

        jobThread.run(job);

        long t2 = System.currentTimeMillis();
        if (logger.isTraceEnabled()) {
            logger.trace("Thread duration: " + (t2 - t1));
        }

        MDC.remove("key");

        return new AsyncResult<Long>(new Long(t2 - t1));
    }
}
