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

@Stateless
public class JobExecutor {

    private static Logger log = LoggerFactory.getLogger(JobExecutor.class);

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<Long> execute(Job job, JobThread jobThread) {

        MDC.put("key", job.getName());

        long t1 = System.currentTimeMillis();

        // TODO: Zeiten in Job Entitiy

        jobThread.run(job);
        long t2 = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("Thread duration: " + (t2 - t1));
        }

        MDC.remove("key");

        return new AsyncResult<Long>(new Long(t2 - t1));
    }
}
