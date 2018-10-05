ALTER TABLE bot_usage_log
  ADD COLUMN target_chat_id BIGINT;
ALTER TABLE bot_usage_log
  ADD COLUMN source_chat_id BIGINT;

UPDATE bot_usage_log
SET target_chat_id = chat_id, source_chat_id = chat_id;
ALTER TABLE bot_usage_log
  ALTER COLUMN target_chat_id SET NOT NULL;
ALTER TABLE bot_usage_log
  ALTER COLUMN source_chat_id SET NOT NULL;

ALTER TABLE bot_usage_log
  DROP CONSTRAINT bot_usage_log_pkey;
ALTER TABLE bot_usage_log
  DROP CONSTRAINT bot_usage_to_source_message_fkey;
DROP INDEX bot_usage_log_chat_id_px;
DROP INDEX bot_usage_log_source_message_id_px;
ALTER TABLE bot_usage_log
  DROP COLUMN chat_id;

ALTER TABLE bot_usage_log
  ADD CONSTRAINT bot_usage_log_pkey PRIMARY KEY (target_chat_id, target_message_id);
ALTER TABLE bot_usage_log
  ADD CONSTRAINT bot_usage_to_source_message_fkey
FOREIGN KEY (source_chat_id, source_message_id) REFERENCES message_entity;
CREATE INDEX bot_usage_log_source_chat_id_px
  ON bot_usage_log (source_chat_id);
CREATE INDEX bot_usage_log_source_message_id_px
  ON bot_usage_log (source_chat_id, source_message_id);