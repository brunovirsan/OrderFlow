create table processed_events (
    event_id uuid primary key,
    topic varchar(255) not null,
    processed_at timestamp not null
);
