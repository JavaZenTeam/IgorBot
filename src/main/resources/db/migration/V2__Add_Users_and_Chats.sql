--Add user_entity
CREATE TABLE user_entity (
  user_id   INTEGER PRIMARY KEY,
  first_name VARCHAR(255) NULL,
  last_name  VARCHAR(255) NULL,
  username  VARCHAR(255) NULL
);

INSERT INTO user_entity (user_id)
  SELECT DISTINCT user_id
  FROM message_entity;

ALTER TABLE message_entity
  ADD CONSTRAINT message_to_user_fkey FOREIGN KEY (user_id) REFERENCES user_entity;

CREATE INDEX message_entity_user_id_px
  ON message_entity (user_id);

--Add chat_entity
CREATE TABLE chat_entity (
  chat_id  BIGINT PRIMARY KEY,
  username VARCHAR(255) NULL,
  title    VARCHAR(512) NULL
);

INSERT INTO chat_entity (chat_id)
  SELECT DISTINCT chat_id
  FROM message_entity;

ALTER TABLE message_entity
  ADD CONSTRAINT message_to_chat_fkey FOREIGN KEY (chat_id) REFERENCES chat_entity;

CREATE INDEX message_entity_chat_id_px
  ON message_entity (chat_id);