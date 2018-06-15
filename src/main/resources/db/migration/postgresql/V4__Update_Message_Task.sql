ALTER TABLE message_task
  ALTER COLUMN time_of_completion
  TYPE TIMESTAMP WITH TIME ZONE
  USING TO_TIMESTAMP(time_of_completion / 1000);