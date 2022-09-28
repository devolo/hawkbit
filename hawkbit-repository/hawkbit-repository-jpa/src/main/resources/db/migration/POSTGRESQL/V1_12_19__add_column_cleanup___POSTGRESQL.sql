ALTER TABLE sp_rollout
    ADD COLUMN is_cleaned_up TINYINT(1) DEFAULT '0' NOT NULL;