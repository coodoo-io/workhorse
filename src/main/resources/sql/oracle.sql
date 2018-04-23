CREATE TABLE workhorse_job (
  id number(19) NOT NULL,
  name varchar2(128) NOT NULL,
  description varchar2(2028) DEFAULT NULL,
  worker_class_name varchar2(512) NOT NULL,
  type varchar2(32) DEFAULT 'ADHOC' NOT NULL,
  status varchar2(32) DEFAULT 'ACTIVE' NOT NULL,
  threads number(10) DEFAULT '1' NOT NULL,
  fail_retries number(10) DEFAULT '0' NOT NULL,
  retry_delay number(10) DEFAULT '4000' check (retry_delay > 0) NOT NULL,
  unique_in_queue raw(1) DEFAULT b NOT NULL '1',
  days_until_clean_up number(10) DEFAULT '30' NOT NULL,
  created_at datetime2(0) NOT NULL,
  updated_at datetime2(0) DEFAULT NULL,
  version number(10) DEFAULT '0' NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT worker_class_name UNIQUE (worker_class_name)
);

-- Generate ID using sequence and trigger
CREATE SEQUENCE workhorse_job_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER workhorse_job_seq_tr
 BEFORE INSERT ON workhorse_job FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT workhorse_job_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE TABLE workhorse_execution (
  id number(19) NOT NULL,
  job_id number(19) NOT NULL,
  status varchar2(32) DEFAULT 'QUEUED' NOT NULL,
  started_at datetime2(0) DEFAULT NULL,
  ended_at datetime2(0) DEFAULT NULL,
  priority raw(1) DEFAULT b NOT NULL '0',
  maturity datetime2(0) DEFAULT NULL,
  chain_id number(19) DEFAULT NULL,
  chain_previous_execution_id number(19) DEFAULT NULL,
  duration number(19) DEFAULT NULL,
  parameters varchar(max),
  log clob varchar(max);

-- Generate ID using sequence and trigger
CREATE SEQUENCE workhorse_execution_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER workhorse_execution_seq_tr
 BEFORE INSERT ON workhorse_execution FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT workhorse_execution_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/,
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
  id number(19) NOT NULL,
  job_id number(19) NOT NULL,
  second varchar2(128) DEFAULT '0' NOT NULL,
  minute varchar2(128) DEFAULT '0' NOT NULL,
  hour varchar2(128) DEFAULT '0' NOT NULL,
  day_of_week varchar2(128) DEFAULT '*' NOT NULL,
  day_of_month varchar2(128) DEFAULT '*' NOT NULL,
  month varchar2(128) DEFAULT '*' NOT NULL,
  year varchar2(128) DEFAULT '*' NOT NULL,
  created_at datetime2(0) NOT NULL,
  updated_at datetime2(0) DEFAULT NULL,
  version number(10) DEFAULT '0' NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT job_id UNIQUE (job_id),
  CONSTRAINT fk_workhorse_schedule_job FOREIGN KEY (job_id) REFERENCES workhorse_job (id)
);

-- Generate ID using sequence and trigger
CREATE SEQUENCE workhorse_schedule_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER workhorse_schedule_seq_tr
 BEFORE INSERT ON workhorse_schedule FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT workhorse_schedule_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX fk_workhorse_schedule_job_idx ON workhorse_schedule (job_id);
