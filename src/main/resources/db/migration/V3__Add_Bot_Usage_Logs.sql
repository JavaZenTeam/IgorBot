CREATE TABLE bot_usage_log (
  chat_id           BIGINT        NOT NULL,
  target_message_id INTEGER       NOT NULL,
  source_message_id INTEGER       NOT NULL,
  module_name       VARCHAR(255)  NOT NULL,
  text              VARCHAR(4096) NULL,
  CONSTRAINT bot_usage_log_pkey
  PRIMARY KEY (chat_id, target_message_id),
  CONSTRAINT bot_usage_to_source_message_fkey
  FOREIGN KEY (chat_id, source_message_id) REFERENCES message_entity
);

CREATE INDEX bot_usage_log_chat_id_px
  ON bot_usage_log (chat_id);
CREATE INDEX bot_usage_log_source_message_id_px
  ON bot_usage_log (chat_id, source_message_id);