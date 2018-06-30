
CREATE SEQUENCE jobengine_job_seq;

CREATE TABLE jobengine_job (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_job_seq'),
  name varchar(128) NOT NULL,
  description varchar(2028) DEFAULT NULL,
  worker_class_name varchar(512) NOT NULL,
  type varchar(32) NOT NULL DEFAULT 'ADHOC',
  status varchar(32) NOT NULL DEFAULT 'ACTIVE',
  threads int NOT NULL DEFAULT '1',
  fail_retries int NOT NULL DEFAULT '0',
  retry_delay int check (retry_delay > 0) NOT NULL DEFAULT '4000',
  unique_in_queue boolean NOT NULL DEFAULT TRUE,
  days_until_clean_up int NOT NULL DEFAULT '30',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  version int NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT worker_class_name UNIQUE  (worker_class_name)
)  ;

CREATE SEQUENCE jobengine_schedule_seq;

CREATE TABLE jobengine_schedule (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_schedule_seq'),
  job_id bigint NOT NULL,
  second varchar(128) NOT NULL DEFAULT '0',
  minute varchar(128) NOT NULL DEFAULT '0',
  hour varchar(128) NOT NULL DEFAULT '0',
  day_of_week varchar(128) NOT NULL DEFAULT '*',
  day_of_month varchar(128) NOT NULL DEFAULT '*',
  month varchar(128) NOT NULL DEFAULT '*',
  year varchar(128) NOT NULL DEFAULT '*',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  version int NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT job_id UNIQUE  (job_id)
 ,
  CONSTRAINT fk_jobengine_schedule_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
)  ;

CREATE INDEX fk_jobengine_schedule_job_idx ON jobengine_schedule (job_id);

CREATE SEQUENCE jobengine_execution_seq;

CREATE TABLE jobengine_execution (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_execution_seq'),
  job_id bigint NOT NULL,
  status varchar(32) NOT NULL DEFAULT 'QUEUED',
  started_at timestamp(0) DEFAULT NULL,
  ended_at timestamp(0) DEFAULT NULL,
  priority boolean NOT NULL DEFAULT FALSE,
  maturity timestamp(0) DEFAULT NULL,
  chain_id bigint DEFAULT NULL,
  chain_previous_execution_id bigint DEFAULT NULL,
  duration bigint DEFAULT NULL,
  parameters text,
  parameters_hash int DEFAULT NULL,
  log text,
  fail_retry int NOT NULL DEFAULT '0',
  fail_retry_execution_id bigint DEFAULT NULL,
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  fail_message varchar(4096) DEFAULT NULL,
  fail_stacktrace text,
  PRIMARY KEY (id)
 ,
  CONSTRAINT fk_jobengine_job_execution_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
)  ;

CREATE INDEX fk_jobengine_job_execution_job_idx ON jobengine_execution (job_id);
CREATE INDEX idx_jobengine_job_execution_jobid_status ON jobengine_execution (job_id,status);
CREATE INDEX idx_jobengine_job_execution_poller ON jobengine_execution (job_id,status,parameters_hash);
CREATE INDEX idx_jobengine_job_execution__chain_id__chain_prev_exec_id (chain_id,chain_previous_execution_id);
