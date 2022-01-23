UPDATE message_entity m
SET file_id = null, file_unique_id = null
WHERE not exists (
	SELECT * FROM chat_config c
	WHERE c.chat_id = m.chat_id
	and c.key = 'messageCollector.saveText'
	and c.value = 'Y')
AND (event_type is null or event_type != 'NEW_PHOTO')
AND (file_type is null or file_type != 'STICKER')
AND file_id is not null;
