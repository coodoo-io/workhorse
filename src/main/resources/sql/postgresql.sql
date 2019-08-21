
-- TODO: jobengine_job_count_view
-- TODO: jobengine_execution_view

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
  parameters text,
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


CREATE TABLE jobengine_statistic (
  id bigint NOT NULL DEFAULT NEXTVAL ('jobengine_statistic_id_seq'),
  job_id bigint NOT NULL,
  duration_avg bigint DEFAULT NULL,
  duration_median bigint DEFAULT NULL,
  queued int DEFAULT '0',
  finished int DEFAULT '0',
  failed int DEFAULT '0',
  schedule int DEFAULT '0',
  created_at timestamp(0) NOT NULL,
  updated_at timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
 CONSTRAINT fk_jobengine_job_statistic_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE INDEX fk_jobengine_job_statistic_job_idx ON jobengine_statistic (job_id);
CREATE INDEX idx_jobengine_job_statistic_jobid_status ON jobengine_statistic (job_id,created_at);
