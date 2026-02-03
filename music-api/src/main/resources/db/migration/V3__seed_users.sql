-- Seed test users
-- Password for all users is 'password123' (BCrypt hash)
INSERT INTO app_user (username, password_hash, enabled) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqPqU6Y2J1fQ1EwQsYj7yPMjYzKlO', TRUE),
    ('user', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqPqU6Y2J1fQ1EwQsYj7yPMjYzKlO', TRUE);
