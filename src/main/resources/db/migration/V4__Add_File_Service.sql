CREATE TABLE file (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(255)NOT NULL,
  size BIGINT NOT NULL,
  text VARCHAR(4096) NULL
);
