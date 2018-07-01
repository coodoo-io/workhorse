package io.coodoo.workhorse.jobengine.boundary;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.coodoo.workhorse.jobengine.boundary.annotation.JobEngineEntityManager;
import io.coodoo.workhorse.jobengine.control.JobEngine;
import io.coodoo.workhorse.jobengine.control.JobEngineController;
import io.coodoo.workhorse.jobengine.control.JobQueuePoller;
import io.coodoo.workhorse.jobengine.control.JobScheduler;
import io.coodoo.workhorse.jobengine.entity.Job;
import io.coodoo.workhorse.jobengine.entity.JobExecution;
import io.coodoo.workhorse.jobengine.entity.JobExecutionStatus;
import io.coodoo.workhorse.jobengine.entity.JobSchedule;
import io.coodoo.workhorse.jobengine.entity.JobStatus;
import io.coodoo.workhorse.jobengine.entity.JobType;

/**
 * Provides basically CRUD functionality
 * 
 * @author coodoo GmbH (coodoo.io)
 */
@Stateless
public class JobEngineService {

    private final Logger log = LoggerFactory.getLogger(JobEngineService.class);

    @Inject
    JobEngine jobEngine;

    @Inject
    JobEngineController jobEngineController;

    @EJB
    JobQueuePoller jobQueuePoller;

    @EJB
    JobScheduler jobScheduler;

    @Inject
    @JobEngineEntityManager
    EntityManager entityManager;

    public void start(Integer interval) {

        log.info("Starting job engine...");

        jobEngineController.checkJobConfiguration();
        jobEngine.initializeMemoryQueues();
        jobQueuePoller.start(interval);
        getAllJobs().forEach(job -> jobScheduler.start(job));
    }

    public void stop() {

        log.info("Stopping job engine...");

        jobQueuePoller.stop();

        for (Job job : getAllJobs()) {
            jobScheduler.stop(job);
            jobEngine.clearMemoryQueue(job);
        }
    }

    public void activateJob(Job job) {

        log.info("Activate job {}", job.getName());

        updateJob(job.getId(), job.getName(), job.getDescription(), job.getWorkerClassName(), JobStatus.ACTIVE, job.getThreads(), job.getFailRetries());
        jobScheduler.start(job);
    }

    public void deactivateJob(Job job) {

        log.info("Deactivate job {}", job.getName());

        updateJob(job.getId(), job.getName(), job.getDescription(), job.getWorkerClassName(), JobStatus.INACTIVE, job.getThreads(), job.getFailRetries());
        jobScheduler.stop(job);
        jobEngine.clearMemoryQueue(job);
    }

    public boolean isRunning() {
        return jobQueuePoller.isRunning();
    }

    public List<Job> getAllJobs() {
        return Job.getAll(entityManager);
    }

    public Job getJobById(Long jobId) {
        return entityManager.find(Job.class, jobId);
    }

    public Job getJobByClassName(String className) {
        return Job.getByWorkerClassName(entityManager, className);
    }

    public Job updateJob(Long jobId, String name, String description, String workerClassName, JobStatus status, int threads, int failRetries) {
        Job job = getJobById(jobId);
        job.setName(name);
        job.setDescription(description);
        job.setWorkerClassName(workerClassName);
        job.setStatus(status);
        job.setThreads(threads);
        job.setFailRetries(failRetries);
        log.debug("Job updated: {}", job);
        return job;
    }

    public void deleteJob(Long jobId) {
        Job job = getJobById(jobId);
        int deletedJobExecutions = JobExecution.deleteAllByJobId(entityManager, jobId);
        if (JobType.SCHEDULED.equals(job.getType()) && getScheduleByJobId(jobId) != null) {
            deleteSchedule(jobId);
        }
        entityManager.remove(job);
        log.debug("Job removed (including {} executions): {}", deletedJobExecutions, job);
    }

    public JobExecution getJobExecutionById(Long jobExecutionId) {
        return entityManager.find(JobExecution.class, jobExecutionId);
    }

    public List<JobExecution> getJobExecutionChain(Long chainId) {
        return JobExecution.getChain(entityManager, chainId);
    }

    public JobExecution createJobExecution(Long jobId, String parameters, Boolean priority, LocalDateTime maturity, Long chainId, Long previousJobExecutionId,
                    boolean uniqueInQueue) {

        Integer parametersHash = null;
        if (parameters != null) {
            parameters.hashCode();
            if (parameters.isEmpty()) {
                parameters = null;
            }
        }
        if (uniqueInQueue) {
            // Prüfen ob es bereits eine Job Excecution mit diesn Parametern existiert und im Status QUEUED ist. Wenn ja diese zurückgeben.
            JobExecution equalQueuedJobExcecution = JobExecution.getFirstCreatedByJobIdAndParamters(entityManager, jobId, parameters);
            if (equalQueuedJobExcecution != null) {
                return equalQueuedJobExcecution;
            }
        }

        JobExecution jobExecution = new JobExecution();
        jobExecution.setJobId(jobId);
        jobExecution.setStatus(JobExecutionStatus.QUEUED);
        jobExecution.setParameters(parameters);
        jobExecution.setParametersHash(parametersHash);
        jobExecution.setFailRetry(0);
        jobExecution.setPriority(priority != null ? priority : false);
        jobExecution.setMaturity(maturity);
        jobExecution.setChainId(chainId);
        jobExecution.setChainPreviousExecutionId(previousJobExecutionId);

        entityManager.persist(jobExecution);
        log.debug("JobExecution created: {}", jobExecution);
        return jobExecution;
    }

    public JobExecution updateJobExecution(Long jobExecutionId, JobExecutionStatus status, String parameters, boolean priority, LocalDateTime maturity,
                    Long chainId, Long previousJobExecutionId, int fails) {
        JobExecution jobExecution = getJobExecutionById(jobExecutionId);
        jobExecution.setStatus(status);
        jobExecution.setParameters(parameters);
        jobExecution.setPriority(priority);
        jobExecution.setMaturity(maturity);
        jobExecution.setChainId(chainId);
        jobExecution.setChainPreviousExecutionId(previousJobExecutionId);
        jobExecution.setFailRetry(fails);
        log.debug("JobExecution updated: {}", jobExecution);
        return jobExecution;
    }

    public void deleteJobExecution(Long jobExecutionId) {
        JobExecution jobExecution = getJobExecutionById(jobExecutionId);
        entityManager.remove(jobExecution);
        log.debug("JobExecution removed: {}", jobExecution);
    }

    public void updateJobStatus(Long jobId, JobStatus status) {
        Job job = getJobById(jobId);
        job.setStatus(status);
        log.debug("Job status updated to: {}", status);
    }

    public void clearMemoryQueue(Long jobId) {
        jobEngine.clearMemoryQueue(getJobById(jobId));
    }

    public List<JobSchedule> getAllSchedules() {
        return JobSchedule.getAll(entityManager);
    }

    public JobSchedule getScheduleById(Long scheduleId) {
        return entityManager.find(JobSchedule.class, scheduleId);
    }

    public JobSchedule getScheduleByJobId(Long jobId) {
        return JobSchedule.getByJobId(entityManager, jobId);
    }

    public JobSchedule updateSchedule(Long jobId, String second, String minute, String hour, String dayOfWeek, String dayOfMonth, String month, String year) {
        JobSchedule jobSchedule = getScheduleByJobId(jobId);
        jobSchedule.setSecond(second);
        jobSchedule.setMinute(minute);
        jobSchedule.setHour(hour);
        jobSchedule.setDayOfWeek(dayOfWeek);
        jobSchedule.setDayOfMonth(dayOfMonth);
        jobSchedule.setMonth(month);
        jobSchedule.setYear(year);
        log.debug("Schedule updated: {}", jobSchedule);

        jobScheduler.start(getJobById(jobId));

        return jobSchedule;
    }

    public void deleteSchedule(Long jobId) {
        JobSchedule jobSchedule = getScheduleByJobId(jobId);

        Job job = getJobById(jobSchedule.getJobId());
        jobScheduler.stop(job);

        entityManager.remove(jobSchedule);
        log.debug("Schedule removed: {}", jobSchedule);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long currentJobExecutions(Long jobId, JobExecutionStatus jobExecutionStatus) {
        return JobExecution.countByJobIdAndStatus(entityManager, jobId, jobExecutionStatus);
    }

}
