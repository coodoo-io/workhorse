

<!--
### Bug Fixes
### Features
### BREAKING CHANGES
-->

<a name="1.3.2"></a>

## 1.3.2 (2019-XX-XX)

### Features

* 

### BREAKING CHANGES

* 

### Bug Fixes

* 


<a name="1.3.1"></a>

## 1.3.1 (2019-05-15)

### Features

* Gave insight to the memory queues via `JobEngineService.getJobEngineInfo()` to see what's going on there

### BREAKING CHANGES

* Removed `JobEngineStatisticsService` since it was not needed anymore

### Bug Fixes

* JobExecutions now get removed from memory queue in case their status change or they get deleted
* Made logging via `JobContext` null-save
* Fixed long overseen `JobThread.getActiveJobExecution()` so in any case the actual JobExecution is given back



<a name="1.3.0"></a>

## 1.3.0 (2019-04-17)

### Features

* New optional annotation `@InitialJobConfig` to provide initial job configuration. This annotation can be used to provide initial configuration to the resulting job. This also leaves `@JobConfig` and `@JobScheduleConfig` as deprecated!
* Also `scheduledJobExecutionCreation()` is deprecated now and got replaced by `onSchedule()`
* Get the scheduled times by the CRON expression
* `logError` now also accepts a throwable for the server log
* Reworked system job `JobExecutionCleanupWorker` to clear old execution for all jobs in one execution
* Dubious way to manually trigger `JobWorkerWith.scheduledJobExecutionCreation`: `JobEngineService.triggerScheduledJobExecutionCreation()`
* If an execution is stuck in status RUNNING and doesn't change, it has became a zombie! Now we found a cure, see `JobEngineConfig`!

### BREAKING CHANGES

* Table `jobengine_schedule` is no more! The schedule CRON expression is from now a plain string, available in the job
* Table `jobengine_job` got new columns `schedule` and `tags` - [see](https://github.com/coodoo-io/workhorse/tree/master/src/main/resources/sql)
* `JobEngineService.updateJob()` added parameters `type`, `schedule`, `tags`, `retryDelay`, `daysUntilCleanUp` and `uniqueInQueue`

### Bug Fixes

* Made system job `JobExecutionCleanupWorker` follow its schedule (it never ran before...)


### Database migration

MySQL

```
ALTER TABLE jobengine_job 
ADD COLUMN tags VARCHAR(1024) NULL DEFAULT NULL AFTER description,
ADD COLUMN schedule VARCHAR(128) NULL DEFAULT NULL AFTER type;

UPDATE jobengine_job j INNER JOIN jobengine_schedule s ON j.id = s.job_id
SET schedule = CONCAT(s.second, ' ', s.minute, ' ', s.hour, ' ', s.day_of_month, ' ', s.month, ' ', s.day_of_week);

DROP TABLE jobengine_schedule;
```


<a name="1.1.2"></a>

## 1.2.0 (2019-03-08)

### Features

* There are Batch-Jobs! Like the chained jobs you can group a bunch of executions as batch and monitor them by `getJobExecutionChainInfo()` (also chains `getJobExecutionChainInfo()`)
* There are also new callback methods regarding chained and batch jobs in `JobWorker`: 
  * `onFinishedBatch()`/`onFinishedChain()` gets called after all job executions of the batch/chain are done
  * `onFailedBatch()`/`onFailedChain()` gets called after a batch/chain failed
* Limit throughput by defining the `maxPerMinute` attribute in the job  
* Basic configuration that can be changed in the implementation, see `JobEngineConfig`
* Yet some other logging convenience methods
* Access to the parameters object via `getParameters()` in `JobWorkerWith`
* Questionable possibility to get a `JobWorker` instance via `JobEngineService.getJobWorker(Job)`

### BREAKING CHANGES

* `JobEngineService.start(Integer interval)` lost its interval parameter - it is now defined in `JobEngineConfig.JOB_QUEUE_POLLER_INTERVAL`
* `JobEngineService.updateJobExecution()` lost its parameters `chainId` and `previousJobExecutionId`
* `JobEngineService.activateJob()` and `JobEngineService.deactivateJob()` now just take the `jobId`, not the whole job object anymore
* `JobWorker.onChainFinished(Long jobExecutionId)` changed name to `onFinishedChain` got extended with the parameter `chainId`
* Table `jobengine_job` got a new nullable column `max_per_pinute` - [see](https://github.com/coodoo-io/workhorse/tree/master/src/main/resources/sql)
* New nullable column `batch_id` in `jobengine_execution` to serve for batch executions - [see](https://github.com/coodoo-io/workhorse/tree/master/src/main/resources/sql)

### Bug Fixes

* `LocalDate` and `LocalDateTime` are now possible to use as or in JobExecution parameters

<a name="1.1.2"></a>

## 1.1.2 (2019-01-19)

### Features

* Added new callback methods the `JobWorker`: 
  * `onAllJobExecutionsDone()` gets called after all job executions in the queue are done
  * `onJobError()` gets called after the job went into status ERROR

### Bug Fixes

* Fixed NullPointerException caused by uninitialized JobContext in case of an execution retry


<a name="1.1.1"></a>

## 1.1.1 (2019-01-18)

### Features

* Added `getJobId()` to the `JobWorker` 
* Created logging convenience methods `logInfo` and `logError` to `JobWorker` and `JobContext` that allows to log into the execution and the server log at once.
* New job status `NO_WORKER` in case the `JobWorker` implementation is missing

### Bug Fixes

* MySQL script: Reduced `worker_class_name` column length to 255 to avoid *Error Code: 1071. Specified key was too long; max key length is 767 bytes*
* Fixed vulnerability in library com.fasterxml.jackson.core:jackson-databind by raising the the version from 2.9.5 to 2.9.8
* Added a `serialVersionUID` to all entities to avoid conflicts possible caused by accessing the persistence by different JVMs 


<a name="1.1.0"></a>

## 1.1.0 (2018-12-10)

### Features

* Annotation `@JobConfig` in now optional
* No interface for job execution parameters is needed anymore
* Simple logging on job execution level
  * Add log entries in the `doWork()` method of `JobWorker` using `logLine(message)` or `logLineWithTimestamp(message)`
  * Anywhere in the code, inject and use `JobLogger` for logging, provided that you are in the context of `doWork()`

### Breaking changes

* Changes in `JobWorker`
  * Method `doWork()` doesn’t need the `JobExecution` object anymore.
  * It isn’t necessary to cast the JobExecutionParameters object, just use `getParameters()`
* Changes in the database schema
  * Table `jobengine_execution` got a new column `log`

### Bug Fixes

* Fixed vulnerability in library com.fasterxml.jackson.core:jackson-databind by raising the the version from 2.9.4 to 2.9.5

<a name="1.0.0"></a>

## 1.0.0 (2018-04-18)

### Features

Initial release