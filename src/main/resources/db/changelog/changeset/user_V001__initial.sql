CREATE TABLE users (
    user_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nick_name VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
    authority_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    role VARCHAR(24) NOT NULL
);

-- To find by nickname --
CREATE UNIQUE INDEX idx_users_email_unique ON users (email);

-- To optimize nickname + role joins --
CREATE INDEX idx_auth_user_id ON authorities (user_id);

-- To find roles --
CREATE INDEX idx_auth_role ON authorities (role);