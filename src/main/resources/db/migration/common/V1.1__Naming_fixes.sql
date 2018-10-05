--fix naming subscription table
ALTER TABLE subscription
  DROP COLUMN userid;
ALTER TABLE subscription
  ADD COLUMN chat_id BIGINT;
ALTER TABLE subscription
  ADD COLUMN message_id INTEGER;

UPDATE subscription
SET chat_id = chatid, message_id = messageid;
ALTER TABLE subscription
  ALTER COLUMN chat_id SET NOT NULL;
ALTER TABLE subscription
  ALTER COLUMN message_id SET NOT NULL;

ALTER TABLE subscription
  DROP CONSTRAINT subscription_pkey;
ALTER TABLE subscription
  DROP COLUMN chatid;
ALTER TABLE subscription
  DROP COLUMN messageid;

ALTER TABLE subscription
  ADD CONSTRAINT subscription_pkey PRIMARY KEY (chat_id, message_id);

--fix naming in message_entity table
ALTER TABLE message_entity
  ADD COLUMN chat_id BIGINT;
ALTER TABLE message_entity
  ADD COLUMN message_id INTEGER;

UPDATE message_entity
SET chat_id = chatid, message_id = messageid;
ALTER TABLE message_entity
  ALTER COLUMN chat_id SET NOT NULL;
ALTER TABLE message_entity
  ALTER COLUMN message_id SET NOT NULL;

ALTER TABLE message_entity
  DROP CONSTRAINT message_entity_pkey;
ALTER TABLE message_entity
  DROP COLUMN chatid;
ALTER TABLE message_entity
  DROP COLUMN messageid;

ALTER TABLE message_entity
  ADD CONSTRAINT message_entity_pkey PRIMARY KEY (chat_id, message_id);

--fix incorrect dates caused by old bug
UPDATE message_entity
SET date = to_timestamp(1000 * cast(extract(EPOCH FROM date) AS INTEGER))
WHERE date < to_timestamp('2010', 'YYYY')