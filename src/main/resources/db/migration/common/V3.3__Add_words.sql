CREATE TABLE message_entity_words (
  chat_id    BIGINT        NOT NULL,
  message_id INTEGER       NOT NULL,
  word       VARCHAR(4096) NOT NULL,
  CONSTRAINT message_entity_word_fkey
  FOREIGN KEY (chat_id, message_id) REFERENCES message_entity
);