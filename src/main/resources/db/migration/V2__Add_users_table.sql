create table users (
    "id" bigint not null unique primary key default nextval('serial'),
    "email" varchar(50) not null unique,
    "password" varchar(100) not null,
    "name" varchar(80) not null,
    "confirmed" boolean not null default false,
    "confirmation_code" varchar(60) not null unique,
    "created_at" timestamp without time zone not null,
    "updated_at" timestamp without time zone not null
);