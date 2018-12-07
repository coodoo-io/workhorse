

<!--
### Bug Fixes
### Features
### BREAKING CHANGES
-->

<a name="1.1.0"></a>

## 1.1.0 (2018-??-??)

### Features

* Annotation `@JobConfig` in now optional
* Simple logging on job execution level
  * Add log entries in the `doWork()` method of `JobWorker` using `logLine(message)` or `logLineWithTimestamp(message)`
  * Anywhere in the code, inject and use `JobLogger` for logging, provided that you are in the context of `doWork()`

### BREAKING CHANGES

* Changes in `JobWorker`
  * Method `doWork()` doesn’t need the `JobExecution` object anymore.
  * It isn’t necessary to cast the JobExecutionParameters object, just use `getParameters()`
* Changes in the database schema
  * Table `jobengine_execution` got a new column `log`

### Bug Fixes

* Fixed vulnerability in Lib com.fasterxml.jackson.core:jackson-databind by raising the the version from 2.9.4 to 2.9.5

<a name="1.0.0"></a>

## 1.0.0 (2018-04-18)

### Features

Initial release