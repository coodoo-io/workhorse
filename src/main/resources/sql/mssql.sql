CREATE TABLE workhorse_job (
  id bigint NOT NULL IDENTITY,
  name varchar(128) NOT NULL,
  description varchar(2028) DEFAULT NULL,
  worker_class_name varchar(512) NOT NULL,
  type varchar(32) NOT NULL DEFAULT 'ADHOC',
  status varchar(32) NOT NULL DEFAULT 'ACTIVE',
  threads int NOT NULL DEFAULT '1',
  fail_retries int NOT NULL DEFAULT '0',
  retry_delay int check (retry_delay > 0) NOT NULL DEFAULT '4000',
  unique_in_queue binary(1) NOT NULL DEFAULT b'1',
  days_until_clean_up int NOT NULL DEFAULT '30',
  created_at datetime2(0) NOT NULL,
  updated_at datetime2(0) DEFAULT NULL,
  version int NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT worker_class_name UNIQUE (worker_class_name)
);

CREATE TABLE workhorse_execution (
  id bigint NOT NULL IDENTITY,
  job_id bigint NOT NULL,
  status varchar(32) NOT NULL DEFAULT 'QUEUED',
  started_at datetime2(0) DEFAULT NULL,
  ended_at datetime2(0) DEFAULT NULL,
  priority binary(1) NOT NULL DEFAULT b'0',
  maturity datetime2(0) DEFAULT NULL,
  chain_id bigint DEFAULT NULL,
  chain_previous_execution_id bigint DEFAULT NULL,
  duration bigint DEFAULT NULL,
  parameters varchar(max),
  log varchar(max) varchar(max),
  fail_retry int NOT NULL DEFAULT '0',
  fail_retry_execution_id bigint DEFAULT NULL,
  fail_message varchar(4096) DEFAULT NULL,
  fail_stacktrace varchar(max),
  created_at datetime2(0) NOT NULL,
  updated_at datetime2(0) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_workhorse_job_execution_job FOREIGN KEY (job_id) REFERENCES workhorse_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_workhorse_job_execution_job_idx ON workhorse_execution (job_id);
CREATE INDEX idx_workhorse_job_execution_jobid_status ON workhorse_execution (job_id,status);
CREATE INDEX fk_workhorse_job_execution_jobid_status ON workhorse_execution (job_id,status);
CREATE INDEX idx_workhorse_job_execution_poller ON workhorse_execution (job_id,status,maturity,chain_previous_execution_id);
CREATE INDEX idx_workhorse_job_execution_status ON workhorse_execution (status);

CREATE TABLE workhorse_schedule (
  id bigint NOT NULL IDENTITY,
  job_id bigint NOT NULL,
  second varchar(128) NOT NULL DEFAULT '0',
  minute varchar(128) NOT NULL DEFAULT '0',
  hour varchar(128) NOT NULL DEFAULT '0',
  day_of_week varchar(128) NOT NULL DEFAULT '*',
  day_of_month varchar(128) NOT NULL DEFAULT '*',
  month varchar(128) NOT NULL DEFAULT '*',
  year varchar(128) NOT NULL DEFAULT '*',
  created_at datetime2(0) NOT NULL,
  updated_at datetime2(0) DEFAULT NULL,
  version int NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT job_id UNIQUE (job_id),
  CONSTRAINT fk_workhorse_schedule_job FOREIGN KEY (job_id) REFERENCES workhorse_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_workhorse_schedule_job_idx ON workhorse_schedule (job_id);
