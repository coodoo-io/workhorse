

<!--
### Bug Fixes
### Features
### BREAKING CHANGES
-->

<a name="1.0.0"></a>

## 1.1.0 (2018-??-??)

### Features

* Annotation `@JobConfig` in now optional

### BREAKING CHANGES

* Changes in `JobWorker`
  * Method `doWork()` doesn’t need the `JobExecution` object anymore.
  * It isn’t necessary to cast the JobExecutionParameters object, just use `getParameters()`


<a name="1.0.0"></a>

## 1.0.0 (2018-04-18)

### Features

Initial release