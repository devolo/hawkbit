ALTER TABLE sp_action ADD initiated_by VARCHAR(64) NOT NULL;
ALTER TABLE sp_target_filter_query ADD auto_assign_initiated_by VARCHAR(64);
