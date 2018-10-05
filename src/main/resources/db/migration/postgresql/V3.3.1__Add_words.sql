INSERT INTO message_entity_words
SELECT chat_id, message_id, lower(word) word
FROM (SELECT me.chat_id, me.message_id, regexp_split_to_table(text, '\W*(^|\s|$)\W*') AS word
      FROM message_entity me
      WHERE me.text IS NOT NULL) words
WHERE word SIMILAR TO '[\w\-]+'
  AND word SIMILAR TO '\D+'