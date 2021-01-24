ALTER TABLE message_entity
    ADD COLUMN event_type VARCHAR(255);

CREATE TABLE message_entity_member
(
    chat_id    BIGINT  NOT NULL,
    message_id INTEGER NOT NULL,
    user_id    INTEGER  NOT NULL,
    CONSTRAINT message_entity_member_message_fkey
        FOREIGN KEY (chat_id, message_id) REFERENCES message_entity,
    CONSTRAINT message_entity_member_user_fkey
        FOREIGN KEY (user_id) REFERENCES user_entity
);

CREATE INDEX message_entity_member_message_ix
    ON message_entity_member (chat_id, message_id);