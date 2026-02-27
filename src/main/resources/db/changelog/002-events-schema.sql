--liquibase formatted sql

--changeset respond:002-create-events
CREATE TABLE events (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

--changeset respond:002-create-participants
CREATE TABLE participants (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL REFERENCES events(id),
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    user_id  UUID
);

--changeset respond:002-create-event-responses
CREATE TABLE event_responses (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL REFERENCES events(id),
    email    VARCHAR(255) NOT NULL,
    status   VARCHAR(50) NOT NULL
);
