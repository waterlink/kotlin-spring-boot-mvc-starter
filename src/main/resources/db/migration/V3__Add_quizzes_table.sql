create table quizzes (
    "id" bigint not null unique primary key default nextval('serial'),
    "user_id" bigint not null references users(id),
    "title" varchar(130) not null,
    "image_url" text not null,
    "description" text not null,
    "duration_in_minutes" int not null,
    "cta" varchar(50) not null,
    "created_at" timestamp without time zone not null,
    "updated_at" timestamp without time zone not null
);