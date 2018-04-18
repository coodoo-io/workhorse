[logo]: https://github.com/coodoo-io/workhorse/raw/master/workhorse.png "Workhorse: Java EE Job Engine for background jobs and business critical tasks"

# Workhorse ![alt text][logo]

> Java EE Job Engine for background jobs and business critical tasks

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [Configuration](#configuration)
- [Changelog](#changelog)
- [Maintainers](#maintainers)
- [Contribute](#contribute)
- [License](#license)


## Background


## Install

1. Add the following dependency to your project ([published on Maven Central](http://search.maven.org/#artifactdetails%7Cio.coodoo%7Cworkhorse%7C1.0.0%7Cjar)):

```xml
<dependency>
    <groupId>io.coodoo</groupId>
    <artifactId>workhorse</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Create the database tables (This is a MySQL example).
         
   ```sql
    CREATE TABLE jobengine_job (
      id bigint(20) NOT NULL AUTO_INCREMENT,
      name varchar(128) NOT NULL,
      description varchar(2028) DEFAULT NULL,
      worker_class_name varchar(512) NOT NULL,
      type varchar(32) NOT NULL DEFAULT 'ADHOC',
      status varchar(32) NOT NULL DEFAULT 'ACTIVE',
      threads int(4) NOT NULL DEFAULT '1',
      fail_retries int(4) NOT NULL DEFAULT '0',
      retry_delay int(11) unsigned NOT NULL DEFAULT '4000',
      unique_in_queue bit(1) NOT NULL DEFAULT b'1',
      days_until_clean_up int(4) NOT NULL DEFAULT '30',
      created_at datetime NOT NULL,
      updated_at datetime DEFAULT NULL,
      version int(11) NOT NULL DEFAULT '0',
      PRIMARY KEY (id),
      UNIQUE KEY worker_class_name (worker_class_name)
    );

    CREATE TABLE jobengine_execution (
      id bigint(20) NOT NULL AUTO_INCREMENT,
      job_id bigint(20) NOT NULL,
      status varchar(32) NOT NULL DEFAULT 'QUEUED',
      started_at datetime DEFAULT NULL,
      ended_at datetime DEFAULT NULL,
      priority bit(1) NOT NULL DEFAULT b'0',
      maturity datetime DEFAULT NULL,
      chain_id bigint(20) DEFAULT NULL,
      chain_previous_execution_id bigint(20) DEFAULT NULL,
      duration bigint(20) DEFAULT NULL,
      parameters text,
      fail_retry int(4) NOT NULL DEFAULT '0',
      fail_retry_execution_id bigint(20) DEFAULT NULL,
      created_at datetime NOT NULL,
      updated_at datetime DEFAULT NULL,
      fail_message varchar(4096) DEFAULT NULL,
      fail_stacktrace text,
      PRIMARY KEY (id),
      KEY fk_jobengine_job_execution_job_idx (job_id),
      KEY idx_jobengine_job_execution_jobid_status (job_id,status),
      KEY fk_jobengine_job_execution_jobid_status (job_id,status),
      KEY idx_jobengine_job_execution_poller (job_id,status,maturity,chain_previous_execution_id),
      KEY idx_jobengine_job_execution_status (status),
      CONSTRAINT fk_jobengine_job_execution_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

    CREATE TABLE jobengine_schedule (
      id bigint(20) NOT NULL AUTO_INCREMENT,
      job_id bigint(20) NOT NULL,
      second varchar(128) NOT NULL DEFAULT '0',
      minute varchar(128) NOT NULL DEFAULT '0',
      hour varchar(128) NOT NULL DEFAULT '0',
      day_of_week varchar(128) NOT NULL DEFAULT '*',
      day_of_month varchar(128) NOT NULL DEFAULT '*',
      month varchar(128) NOT NULL DEFAULT '*',
      year varchar(128) NOT NULL DEFAULT '*',
      created_at datetime NOT NULL,
      updated_at datetime DEFAULT NULL,
      version int(11) NOT NULL DEFAULT '0',
      PRIMARY KEY (id),
      UNIQUE KEY job_id (job_id),
      KEY fk_jobengine_schedule_job_idx (job_id),
      CONSTRAINT fk_jobengine_schedule_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );
   ```
                 
3. Add the JPA entities to your persistence.xml:

   ```xml
    <class>io.coodoo.workhorse.jobengine.entity.Job</class>
    <class>io.coodoo.workhorse.jobengine.entity.JobExecution</class>
    <class>io.coodoo.workhorse.jobengine.entity.JobSchedule</class>
   ```
4. To provide the EntityManager you have to implement a `@AuditEntityManager` CDI producer.

   ```java
    @Stateless
    public class JobEngineEntityManagerProducer {
    
        @PersistenceContext
        private EntityManager entityManager;
    
        @Produces
        @JobEngineEntityManager
        public EntityManager getEntityManager() {
            return entityManager;
        }
    }
    ```
    *This is necessary to avoid trouble when it comes to different persistence contexts.*




## Usage


## Configuration


## Changelog

All release changes can be viewed on our [changelog](./CHANGELOG.md).

## Maintainers

[coodoo](https://github.com/orgs/coodoo-io/people)

## Contribute

Pull requests and [issues](https://github.com/coodoo-io/workhorse/issues) are welcome.

## License

[Apache-2.0 © coodoo GmbH](./LICENSE)

Workhorse Logo: [http://www.how-to-draw-funny-cartoons.com](http://www.how-to-draw-funny-cartoons.com)