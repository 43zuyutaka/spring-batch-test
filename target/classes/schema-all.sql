DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

DROP TABLE food IF EXISTS;

CREATE TABLE food  (
    food_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    food_name VARCHAR(20),
    lovely VARCHAR(20)
);
