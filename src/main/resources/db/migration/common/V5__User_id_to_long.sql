ALTER TABLE subscription
    ALTER COLUMN user_id TYPE bigint;

ALTER TABLE user_entity
    ALTER COLUMN user_id TYPE bigint;

ALTER TABLE message_entity_member
    ALTER COLUMN user_id TYPE bigint;

ALTER TABLE message_entity
    ALTER COLUMN user_id TYPE bigint;

ALTER TABLE message_entity
    ALTER COLUMN forward_user_id TYPE bigint;