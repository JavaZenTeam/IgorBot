CREATE TABLE activity_level
(
    id              BIGINT PRIMARY KEY,
    name            TEXT,
    lower_threshold INT,
    upper_threshold INT NULL
);

INSERT INTO activity_level
VALUES (1, 'Low activity', 0, 10);
INSERT INTO activity_level
VALUES (2, 'Middle activity', 10, 100);
INSERT INTO activity_level
VALUES (3, 'High activity', 100, null);