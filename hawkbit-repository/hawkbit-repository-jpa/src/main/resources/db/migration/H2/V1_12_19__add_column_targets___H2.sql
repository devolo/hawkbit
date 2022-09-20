ALTER TABLE sp_target
    ADD COLUMN requires_cleanup TINYINT(1) DEFAULT '0' NOT NULL;