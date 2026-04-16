create table kitchen_tickets (
    id uuid primary key,
    order_id uuid not null unique,
    customer_name varchar(255) not null,
    status varchar(50) not null,
    received_at timestamp not null,
    updated_at timestamp not null
);

create table ticket_items (
    id uuid primary key,
    ticket_id uuid not null references kitchen_tickets (id) on delete cascade,
    product_name varchar(255) not null,
    quantity integer not null
);

create table processed_events (
    event_id uuid primary key,
    topic varchar(255) not null,
    processed_at timestamp not null
);
