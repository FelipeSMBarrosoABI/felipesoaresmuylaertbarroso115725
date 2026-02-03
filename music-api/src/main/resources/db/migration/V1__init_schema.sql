-- Artists table
CREATE TABLE artist (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_singer BOOLEAN NOT NULL DEFAULT FALSE,
    is_band BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Albums table
CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INTEGER,
    genre VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Artist-Album relationship (N:N)
CREATE TABLE artist_album (
    artist_id BIGINT NOT NULL REFERENCES artist(id) ON DELETE CASCADE,
    album_id BIGINT NOT NULL REFERENCES album(id) ON DELETE CASCADE,
    PRIMARY KEY (artist_id, album_id)
);

-- Album covers table
CREATE TABLE album_cover (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL UNIQUE REFERENCES album(id) ON DELETE CASCADE,
    object_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Users table for authentication
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Refresh tokens table
CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_artist_name ON artist(name);
CREATE INDEX idx_album_title ON album(title);
CREATE INDEX idx_album_genre ON album(genre);
CREATE INDEX idx_refresh_token_token ON refresh_token(token);
CREATE INDEX idx_refresh_token_user ON refresh_token(user_id);
