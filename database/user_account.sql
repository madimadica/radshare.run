CREATE TABLE user_account (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(24) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    email VARCHAR(512),
    roles_bitmask INTEGER NOT NULL
);
