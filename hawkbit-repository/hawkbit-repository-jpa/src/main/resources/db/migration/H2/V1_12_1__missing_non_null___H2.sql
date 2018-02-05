ALTER TABLE sp_action_status_messages CHANGE COLUMN detail_message detail_message varchar(512) not null;
ALTER TABLE sp_action CHANGE COLUMN distribution_set distribution_set bigint not null;
ALTER TABLE sp_action CHANGE COLUMN target target bigint not null;
ALTER TABLE sp_action CHANGE COLUMN status status integer not null;
ALTER TABLE sp_action_status CHANGE COLUMN target_occurred_at target_occurred_at bigint not null;
ALTER TABLE sp_action_status CHANGE COLUMN status status integer not null;
ALTER TABLE sp_rollout CHANGE COLUMN distribution_set distribution_set bigint not null;
ALTER TABLE sp_rollout CHANGE COLUMN status status integer not null;
ALTER TABLE sp_rolloutgroup CHANGE COLUMN rollout rollout bigint not null;
ALTER TABLE sp_rolloutgroup CHANGE COLUMN status status integer not null;
ALTER TABLE sp_artifact CHANGE COLUMN sha1_hash sha1_hash varchar(40) not null;
ALTER TABLE sp_target CHANGE COLUMN controller_id controller_id varchar(64) not null;