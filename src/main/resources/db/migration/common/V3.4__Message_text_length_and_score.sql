ALTER TABLE message_entity
ADD COLUMN text_length integer;

UPDATE message_entity
SET text_length = char_length(text)
WHERE text is not null;

UPDATE message_entity
SET text_length = 0
WHERE text is null;

ALTER TABLE message_entity
ALTER COLUMN text_length SET not null;




ALTER TABLE message_entity
ADD COLUMN score double precision;

UPDATE message_entity
SET score = sqrt(char_length(text))
WHERE text is not null;

UPDATE message_entity
SET score = sqrt(30)
WHERE text is null;

ALTER TABLE message_entity
ALTER COLUMN score SET not null;