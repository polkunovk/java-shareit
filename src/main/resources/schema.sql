-- Создание схемы, если её нет
CREATE SCHEMA IF NOT EXISTS shareIt_schema;

CREATE TABLE IF NOT EXISTS shareIt_schema.users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS shareIt_schema.items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512),
    available BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_items_user_id
        FOREIGN KEY (user_id)
        REFERENCES shareIt_schema.users(user_id)
);

CREATE TABLE IF NOT EXISTS shareIt_schema.comments (
    item_id BIGINT NOT NULL,
    text VARCHAR(512) NOT NULL,
    CONSTRAINT fk_comments_item_id
        FOREIGN KEY (item_id)
        REFERENCES shareIt_schema.items(item_id)
        ON DELETE CASCADE,
    PRIMARY KEY (item_id, text)
);

CREATE TABLE IF NOT EXISTS shareIt_schema.bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES shareIt_schema.users(user_id),
    item_id BIGINT NOT NULL REFERENCES shareIt_schema.items(item_id),
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL
);