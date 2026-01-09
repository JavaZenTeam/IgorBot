ALTER TABLE message_task
    ADD COLUMN message_thread_id integer null;

ALTER TABLE message_entity
    ADD COLUMN message_thread_id integer null;
