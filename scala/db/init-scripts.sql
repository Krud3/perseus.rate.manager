CREATE EXTENSION IF NOT EXISTS hstore;

CREATE SCHEMA IF NOT EXISTS rate;

CREATE TABLE IF NOT EXISTS rate."room_type_rate" (
    "hotel_id" VARCHAR(255) NOT NULL,
    "date" DATE NOT NULL,
    "rate" NUMERIC NOT NULL,
    PRIMARY KEY ("hotel_id", "date"),
    CHECK ("rate" >= 0 AND "rate" <= 100.0)
);
