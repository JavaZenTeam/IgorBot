create table message_task
(
  id bigserial not null
    constraint message_task_pkey
    primary key,
  chat_id bigint,
  is_completed boolean,
  message_id bigint,
  reply_message_id bigint,
  scheduled_text varchar(4096),
  time_of_completion bigint,
  user_id bigint,
  bot_name varchar(255)
)
;

create table subscription
(
  chatid bigint not null,
  messageid integer not null,
  response varchar(4096) not null,
  trigger varchar(4096) not null,
  userid integer,
  user_id integer,
  constraint subscription_pkey
  primary key (chatid, messageid)
)
;

create table chat_config
(
  chat_id bigint not null,
  key varchar(255) not null,
  value varchar(255),
  constraint chat_config_pkey
  primary key (chat_id, key)
)
;

create table message_entity
(
  chatid bigint not null,
  messageid integer not null,
  date timestamp,
  text varchar(4096),
  user_id integer,
  constraint message_entity_pkey
  primary key (chatid, messageid)
)
;

