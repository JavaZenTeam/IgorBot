ALTER TABLE message_task
    ADD COLUMN repeat_interval varchar(255) null;
ALTER TABLE message_task
    ADD COLUMN repeat_count integer null;