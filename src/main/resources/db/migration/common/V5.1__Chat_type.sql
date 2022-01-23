ALTER TABLE chat_entity
    ADD COLUMN type TEXT DEFAULT 'UNKNOWN' NOT NULL;

UPDATE chat_entity SET type = 'PRIVATE'
WHERE chat_id IN (SELECT user_id FROM user_entity);