CREATE
MATERIALIZED VIEW daily_user_chat_statistic
AS (
SELECT user_id,
       chat_id,
       date::date,
       count(message_id) as count,
       sum(text_length)  as text_length,
       sum(score)        as score
FROM message_entity
GROUP BY user_id, chat_id, date::date
);

CREATE UNIQUE INDEX ON daily_user_chat_statistic (user_id,chat_id, date);