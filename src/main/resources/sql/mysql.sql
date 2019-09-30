
CREATE TABLE jobengine_job (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(128) COLLATE utf8_bin NOT NULL,
  description varchar(2028) COLLATE utf8_bin DEFAULT NULL,
  tags varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  worker_class_name varchar(255) COLLATE utf8_bin NOT NULL,
  parameters_class_name varchar(255) COLLATE utf8_bin DEFAULT NULL,
  schedule varchar(128) COLLATE utf8_bin DEFAULT NULL,
  status varchar(32) COLLATE utf8_bin NOT NULL DEFAULT 'ACTIVE',
  threads int(4) NOT NULL DEFAULT '1',
  max_per_minute int(6) DEFAULT NULL,
  fail_retries int(4) NOT NULL DEFAULT '0',
  retry_delay int(11) unsigned NOT NULL DEFAULT '4000',
  unique_in_queue bit(1) NOT NULL DEFAULT b'1',
  days_until_clean_up int(4) NOT NULL DEFAULT '30',
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  version int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY worker_class_name (worker_class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE jobengine_execution (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  job_id bigint(20) NOT NULL,
  status varchar(32) COLLATE utf8_bin NOT NULL DEFAULT 'QUEUED',
  started_at datetime DEFAULT NULL,
  ended_at datetime DEFAULT NULL,
  priority bit(1) NOT NULL DEFAULT b'0',
  maturity datetime DEFAULT NULL,
  batch_id bigint(20) DEFAULT NULL,
  chain_id bigint(20) DEFAULT NULL,
  chain_previous_execution_id bigint(20) DEFAULT NULL,
  duration bigint(20) DEFAULT NULL,
  parameters mediumtext COLLATE utf8_bin,
  parameters_hash int(11) DEFAULT NULL,
  log mediumtext COLLATE utf8_bin,
  fail_retry int(4) NOT NULL DEFAULT '0',
  fail_retry_execution_id bigint(20) DEFAULT NULL,
  fail_message varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  fail_stacktrace mediumtext COLLATE utf8_bin,
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_jobengine_job_execution_job_idx (job_id),
  KEY idx_jobengine_job_execution__jobid__status (job_id,status),
  KEY idx_jobengine_job_execution__poller (job_id,status,parameters_hash),
  KEY idx_jobengine_job_execution__chain_id__chain_prev_exec_id (chain_id,chain_previous_execution_id),
  KEY idx_jobengine_job_execution__batch_id_status (batch_id,status),
  KEY idx_jobengine_job_execution__startet_at_status (started_at,status),
  CONSTRAINT fk_jobengine_job_execution_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE jobengine_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  time_zone varchar(64) COLLATE utf8_bin DEFAULT NULL,
  job_queue_poller_interval int(6) NOT NULL,
  job_queue_max int(6) NOT NULL,
  job_queue_min int(6) NOT NULL,
  zombie_recognition_time int(6) NOT NULL,
  zombie_cure_status varchar(32) COLLATE utf8_bin NOT NULL,
  days_until_statistic_minutes_deletion int(6) NOT NULL,
  days_until_statistic_hours_deletion int(6) NOT NULL,
  log_change varchar(128) COLLATE utf8_bin DEFAULT NULL,
  log_time_formatter varchar(128) COLLATE utf8_bin NOT NULL,
  log_info_marker varchar(128) COLLATE utf8_bin DEFAULT NULL,
  log_warn_marker varchar(128) COLLATE utf8_bin DEFAULT NULL,
  log_error_marker varchar(128) COLLATE utf8_bin DEFAULT NULL,
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE jobengine_log (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  message mediumtext COLLATE utf8_bin DEFAULT NULL,
  job_id bigint(20) DEFAULT NULL,
  job_status varchar(32) COLLATE utf8_bin DEFAULT NULL,
  by_user bit(1) NOT NULL DEFAULT b'0',
  change_parameter varchar(128) COLLATE utf8_bin DEFAULT NULL,
  change_old varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  change_new varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  host_name varchar(256) COLLATE utf8_bin DEFAULT NULL,
  stacktrace mediumtext COLLATE utf8_bin DEFAULT NULL,
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_jobengine_job_log_job_idx (job_id),
  KEY idx_jobengine_job_log__jobid__status (job_id,job_status),
  CONSTRAINT fk_jobengine_job_log_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE jobengine_statistic_minute (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  job_id bigint(20) NOT NULL,
  recorded_from datetime NOT NULL,
  recorded_to datetime NOT NULL,
  duration_count int(4) NOT NULL,
  duration_sum bigint(20) DEFAULT NULL,
  duration_max bigint(20) DEFAULT NULL,
  duration_min bigint(20) DEFAULT NULL,
  duration_avg bigint(20) DEFAULT NULL,
  duration_median bigint(20) DEFAULT NULL,
  queued int(4) DEFAULT 0,
  finished int(4) DEFAULT 0,
  failed int(4) DEFAULT 0,
  schedule int(4) DEFAULT 0,
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_jobengine_job_statistic_minute_job_idx (job_id),
  KEY idx_jobengine_job_statistic_minute__jobid__created_at (job_id,created_at),
  KEY idx_jobengine_job_statistic_minute__jobid__from_to (job_id,recorded_from,recorded_to),
  CONSTRAINT fk_jobengine_job_statistic_minute_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE jobengine_statistic_hour (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  job_id bigint(20) NOT NULL,
  recorded_from datetime NOT NULL,
  recorded_to datetime NOT NULL,
  duration_count int(4) NOT NULL,
  duration_sum bigint(20) DEFAULT NULL,
  duration_max bigint(20) DEFAULT NULL,
  duration_min bigint(20) DEFAULT NULL,
  duration_avg bigint(20) DEFAULT NULL,
  finished int(4) DEFAULT 0,
  failed int(4) DEFAULT 0,
  schedule int(4) DEFAULT 0,
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_jobengine_job_statistic_hour_job_idx (job_id),
  KEY idx_jobengine_job_statistic_hour__jobid__created_at (job_id,created_at),
  KEY idx_jobengine_job_statistic_hour__jobid__from_to (job_id,recorded_from,recorded_to),
  CONSTRAINT fk_jobengine_job_statistic_hour_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE jobengine_statistic_day (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  job_id bigint(20) NOT NULL,
  recorded_from datetime NOT NULL,
  recorded_to datetime NOT NULL,
  duration_count int(4) NOT NULL,
  duration_sum bigint(20) DEFAULT NULL,
  duration_max bigint(20) DEFAULT NULL,
  duration_min bigint(20) DEFAULT NULL,
  duration_avg bigint(20) DEFAULT NULL,
  finished int(4) DEFAULT 0,
  failed int(4) DEFAULT 0,
  schedule int(4) DEFAULT 0,
  created_at datetime NOT NULL,
  updated_at datetime DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_jobengine_job_statistic_day_job_idx (job_id),
  KEY idx_jobengine_job_statistic_day__jobid__created_at (job_id,created_at),
  KEY idx_jobengine_job_statistic_day__jobid__from_to (job_id,recorded_from,recorded_to),
  CONSTRAINT fk_jobengine_job_statistic_day_job FOREIGN KEY (job_id) REFERENCES jobengine_job (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
  IF(log.stacktrace IS NULL, FALSE, TRUE) AS stacktrace,
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
	j.*,
	COUNT(*) total,
    SUM(CASE WHEN e.status = 'QUEUED'   THEN 1 ELSE 0 END) queued,
    SUM(CASE WHEN e.status = 'RUNNING'  THEN 1 ELSE 0 END) running
FROM jobengine_job j
LEFT OUTER JOIN jobengine_execution e ON j.id = e.job_id
GROUP BY j.id;