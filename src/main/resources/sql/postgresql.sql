
CREATE SEQUENCE jobengine_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_job (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_job_id_seq'),
  name varchar(128) NOT NULL,
  description varchar(2028) DEFAULT NULL,
  tags varchar(1024) DEFAULT NULL,
  worker_class_name varchar(512) NOT NULL,
  parameters_class_name varchar(512) DEFAULT NULL,
  schedule varchar(128) DEFAULT NULL,
  status varchar(32) NOT NULL DEFAULT 'ACTIVE',
  threads int NOT NULL DEFAULT '1',
  max_per_minute int DEFAULT NULL,
  fail_retries int NOT NULL DEFAULT '0',
  retry_delay int check (retry_delay > 0) NOT NULL DEFAULT '4000',
  unique_in_queue boolean NOT NULL DEFAULT TRUE,
  days_until_clean_up int NOT NULL DEFAULT '30',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  version int NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  CONSTRAINT worker_class_name UNIQUE  (worker_class_name)
);


CREATE SEQUENCE jobengine_execution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_execution (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_execution_id_seq'),
  job_id bigint NOT NULL,
  status varchar(32) NOT NULL DEFAULT 'QUEUED',
  started_at timestamp(0) DEFAULT NULL,
  ended_at timestamp(0) DEFAULT NULL,
  priority boolean NOT NULL DEFAULT FALSE,
  maturity timestamp(0) DEFAULT NULL,
  batch_id bigint DEFAULT NULL,
  chain_id bigint DEFAULT NULL,
  chain_previous_execution_id bigint DEFAULT NULL,
  duration bigint DEFAULT NULL,
  parameters json,
  parameters_hash int DEFAULT NULL,
  log text,
  fail_retry int NOT NULL DEFAULT '0',
  fail_retry_execution_id bigint DEFAULT NULL,
  fail_message varchar(4096) DEFAULT NULL,
  fail_stacktrace text,
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
 CONSTRAINT fk_jobengine_job_execution_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_jobengine_job_execution_job_idx ON jobengine_execution (job_id);
CREATE INDEX idx_jobengine_job_execution_jobid_status ON jobengine_execution (job_id,status);
CREATE INDEX idx_jobengine_job_execution_poller ON jobengine_execution (job_id,status,parameters_hash);
CREATE INDEX idx_jobengine_job_execution__chain_id__chain_prev_exec_id ON jobengine_execution (chain_id,chain_previous_execution_id);
CREATE INDEX idx_jobengine_job_execution__batch_id_status ON jobengine_execution (batch_id,status);
CREATE INDEX idx_jobengine_job_execution__startet_at_status ON jobengine_execution (started_at,status);


CREATE SEQUENCE jobengine_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_log (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_log_id_seq'),
  message text,
  job_id bigint DEFAULT NULL,
  job_status varchar(32) DEFAULT NULL,
  by_user boolean NOT NULL DEFAULT FALSE,
  change_parameter varchar(128) DEFAULT NULL,
  change_old varchar(1024) DEFAULT NULL,
  change_new varchar(1024) DEFAULT NULL,
  host_name varchar(256) DEFAULT NULL,
  stacktrace text,
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
 CONSTRAINT fk_jobengine_job_log_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_jobengine_job_log_job_idx ON jobengine_log (job_id);
CREATE INDEX idx_jobengine_job_log_jobid_status ON jobengine_log (job_id,job_status);


CREATE SEQUENCE jobengine_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_config (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_config_id_seq'),
  time_zone varchar(64) DEFAULT NULL,
  job_queue_poller_interval int NOT NULL,
  job_queue_max int NOT NULL,
  job_queue_min int NOT NULL,
  zombie_recognition_time int NOT NULL,
  zombie_cure_status varchar(32) NOT NULL,
  days_until_statistic_minutes_deletion int NOT NULL,
  days_until_statistic_hours_deletion int NOT NULL,
  log_change varchar(128) DEFAULT NULL,
  log_time_formatter varchar(128) NOT NULL,
  log_info_marker varchar(128) DEFAULT NULL,
  log_warn_marker varchar(128) DEFAULT NULL,
  log_error_marker varchar(128) DEFAULT NULL,
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE SEQUENCE jobengine_statistic_minute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_statistic_minute (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_statistic_minute_id_seq'),
  job_id bigint NOT NULL,
  recorded_from timestamp(0) NOT NULL,
  recorded_to timestamp(0) NOT NULL,
  duration_count int NOT NULL,
  duration_sum bigint DEFAULT NULL,
  duration_max bigint DEFAULT NULL,
  duration_min bigint DEFAULT NULL,
  duration_avg bigint DEFAULT NULL,
  duration_median bigint DEFAULT NULL,
  queued int DEFAULT '0',
  finished int DEFAULT '0',
  failed int DEFAULT '0',
  schedule int DEFAULT '0',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
 CONSTRAINT fk_jobengine_job_statistic_minute_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_jobengine_job_statistic_minute_job_idx ON jobengine_statistic_minute (job_id);
CREATE INDEX idx_jobengine_job_statistic_minute__jobid__created_at ON jobengine_statistic_minute (job_id,created_at);
CREATE INDEX idx_jobengine_job_statistic_minute__jobid__from_to ON jobengine_statistic_minute (job_id,recorded_from,recorded_to);


CREATE SEQUENCE jobengine_statistic_hour_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_statistic_hour (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_statistic_hour_id_seq'),
  job_id bigint NOT NULL,
  recorded_from timestamp(0) NOT NULL,
  recorded_to timestamp(0) NOT NULL,
  duration_count int NOT NULL,
  duration_sum bigint DEFAULT NULL,
  duration_max bigint DEFAULT NULL,
  duration_min bigint DEFAULT NULL,
  duration_avg bigint DEFAULT NULL,
  finished int DEFAULT '0',
  failed int DEFAULT '0',
  schedule int DEFAULT '0',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
 CONSTRAINT fk_jobengine_job_statistic_hour_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_jobengine_job_statistic_hour_job_idx ON jobengine_statistic_hour (job_id);
CREATE INDEX idx_jobengine_job_statistic_hour__jobid__created_at ON jobengine_statistic_hour (job_id,created_at);
CREATE INDEX idx_jobengine_job_statistic_hour__jobid__from_to ON jobengine_statistic_hour (job_id,recorded_from,recorded_to);


CREATE SEQUENCE jobengine_statistic_day_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE jobengine_statistic_day (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_statistic_day_id_seq'),
  job_id bigint NOT NULL,
  recorded_from timestamp(0) NOT NULL,
  recorded_to timestamp(0) NOT NULL,
  duration_count int NOT NULL,
  duration_sum bigint DEFAULT NULL,
  duration_max bigint DEFAULT NULL,
  duration_min bigint DEFAULT NULL,
  duration_avg bigint DEFAULT NULL,
  finished int DEFAULT '0',
  failed int DEFAULT '0',
  schedule int DEFAULT '0',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
 CONSTRAINT fk_jobengine_job_statistic_day_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_jobengine_job_statistic_day_job_idx ON jobengine_statistic_day (job_id);
CREATE INDEX idx_jobengine_job_statistic_day__jobid__created_at ON jobengine_statistic_day (job_id,created_at);
CREATE INDEX idx_jobengine_job_statistic_day__jobid__from_to ON jobengine_statistic_day (job_id,recorded_from,recorded_to);


CREATE VIEW jobengine_log_view AS
SELECT log.id,
  log.message,
  log.job_id,
  job.name AS job_name,
  job.description AS job_description,
  log.job_status AS job_status,
  job.fail_retries AS job_fail_retries,
  job.threads AS job_threads,
  log.by_user,
  log.change_parameter,
  log.change_old,
  log.change_new,
  log.host_name,
  CASE WHEN log.stacktrace IS NULL THEN FALSE ELSE TRUE END AS stacktrace,
  log.updated_at,
  log.created_at
FROM jobengine_log log
LEFT JOIN jobengine_job job ON log.job_id = job.id;


CREATE VIEW jobengine_execution_view AS
SELECT ex.id,
  ex.job_id,
  job.name AS job_name,
  job.description AS job_description,
  job.status AS job_status,
  job.fail_retries AS job_fail_retries,
  job.threads AS job_threads,
  ex.status,
  ex.started_at,
  ex.ended_at,
  ex.priority,
  ex.maturity,
  ex.batch_id,
  ex.chain_id,
  ex.chain_previous_execution_id,
  ex.duration,
  ex.parameters,
  ex.fail_retry,
  ex.fail_retry_execution_id,
  ex.fail_message,
  ex.updated_at,
  ex.created_at
FROM jobengine_execution ex
LEFT JOIN jobengine_job job ON ex.job_id = job.id;


CREATE VIEW jobengine_job_count_view AS
SELECT 
  job.id,
  job.name,
  job.description,
  job.tags,
  job.worker_class_name,
  job.parameters_class_name,
  job.schedule,
  job.status,
  job.threads,
  job.max_per_minute,
  job.fail_retries,
  job.retry_delay,
  job.unique_in_queue,
  job.days_until_clean_up,
  job.created_at,
  job.updated_at,
  job.version,
  COUNT(*) total,
  SUM(CASE WHEN ex.status = 'QUEUED'   THEN 1 ELSE 0 END) queued,
  SUM(CASE WHEN ex.status = 'RUNNING'  THEN 1 ELSE 0 END) running
FROM jobengine_job job
LEFT OUTER JOIN jobengine_execution ex ON job.id = ex.job_id
GROUP BY job.id;
