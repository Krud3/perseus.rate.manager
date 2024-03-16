CREATE DATABASE rate;
\c rate;

CREATE TABLE IF NOT EXIST rate(
    "hotel_id" uuid DEFAULT gen_random_uuid(),
    date bigint NOT NULL,
    "rate" NUMERIC NOT NULL,
    PRIMARY KEY ("hotel_id", date),
    CHECK ("rate" >= 0 AND "rate" <= 100.0)
);