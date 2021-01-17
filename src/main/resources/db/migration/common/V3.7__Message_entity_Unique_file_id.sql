ALTER TABLE message_entity
    ADD COLUMN file_unique_id VARCHAR(4096);

UPDATE message_entity
SET file_unique_id = file_id
WHERE file_id IS NOT NULL
  AND file_unique_id IS NULL;