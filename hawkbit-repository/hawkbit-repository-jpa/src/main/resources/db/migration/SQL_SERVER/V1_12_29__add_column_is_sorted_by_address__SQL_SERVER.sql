ALTER TABLE sp_rollout
    ADD COLUMN is_sorted_by_address BOOLEAN DEFAULT '0' NOT NULL;