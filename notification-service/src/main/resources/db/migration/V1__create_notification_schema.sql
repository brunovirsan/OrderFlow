create table notifications (
    id uuid primary key,
    order_id uuid not null,
    recipient_email varchar(255) not null,
    type varchar(50) not null,
    message varchar(1000) not null,
    sent_at timestamp not null,
    status varchar(50) not null
);

create table order_recipients (
    order_id uuid primary key,
    recipient_email varchar(255) not null
);

create table processed_events (
    event_id uuid primary key,
    topic varchar(255) not null,
    processed_at timestamp not null
);
