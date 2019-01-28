

<!--
### Bug Fixes
### Features
### BREAKING CHANGES
-->

<a name="1.1.3"></a>

## 1.1.3 (2019-01-28)

### Features

* Lists of simple Java types can now be used as JobExecution parameters


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