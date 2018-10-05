ALTER TABLE message_entity
  ADD COLUMN file_id VARCHAR(4096);
ALTER TABLE message_entity
  ADD COLUMN file_type VARCHAR(10);
ALTER TABLE message_entity
  ADD COLUMN forward_user_id INTEGER;

ALTER TABLE message_entity
  ADD CONSTRAINT message_forward_user_fkey FOREIGN KEY (forward_user_id) REFERENCES user_entity;

CREATE INDEX message_entity_forward_user_id_px
  ON message_entity (forward_user_id);