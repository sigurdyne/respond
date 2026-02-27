--liquibase formatted sql

--changeset respond:003-add-event-start-time
ALTER TABLE events ADD COLUMN start_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now();
