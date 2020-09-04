CREATE TABLE customer(
    id integer primary key not null,
    name varchar(150) not null,
    phone_number varchar(20) not null,
    phone_credit float not null default 0.0,
    created_at timestamp not null,
    updated_at timestamp not null
);

CREATE TABLE purchase(
    id integer primary key not null,
    item_description varchar(4000),
    amount float not null,
    currency varchar(3) not null,
    customer_id integer not null references customer(id),
    created_at timestamp not null,
    updated_at timestamp not null
);

CREATE TABLE refund(
    id integer primary key not null,
    description varchar(4000),
    amount float not null,
    currency varchar(3) not null,
    customer_id integer not null references customer(id),
    created_at timestamp not null,
    updated_at timestamp not null
);

CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE purchase_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE refund_seq START WITH 1 INCREMENT BY 1;
