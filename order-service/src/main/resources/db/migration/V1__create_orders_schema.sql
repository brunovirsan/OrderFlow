create table orders (
    id uuid primary key,
    customer_name varchar(255) not null,
    customer_email varchar(255) not null,
    status varchar(50) not null,
    total_amount numeric(19, 2) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    cancellation_reason varchar(255)
);

create table order_items (
    id uuid primary key,
    order_id uuid not null references orders (id) on delete cascade,
    product_id uuid not null,
    product_name varchar(255) not null,
    quantity integer not null,
    unit_price numeric(19, 2) not null
);
