-- Seed artists
INSERT INTO artist (name, is_singer, is_band) VALUES
    ('The Beatles', FALSE, TRUE),
    ('Queen', FALSE, TRUE),
    ('Michael Jackson', TRUE, FALSE),
    ('Pink Floyd', FALSE, TRUE),
    ('Bob Dylan', TRUE, FALSE);

-- Seed albums
INSERT INTO album (title, release_year, genre) VALUES
    ('Abbey Road', 1969, 'Rock'),
    ('A Night at the Opera', 1975, 'Rock'),
    ('Thriller', 1982, 'Pop'),
    ('The Dark Side of the Moon', 1973, 'Progressive Rock'),
    ('Highway 61 Revisited', 1965, 'Folk Rock'),
    ('Sgt. Peppers Lonely Hearts Club Band', 1967, 'Rock'),
    ('News of the World', 1977, 'Rock');

-- Seed artist-album relationships (N:N)
INSERT INTO artist_album (artist_id, album_id) VALUES
    (1, 1),  -- The Beatles - Abbey Road
    (1, 6),  -- The Beatles - Sgt. Pepper's
    (2, 2),  -- Queen - A Night at the Opera
    (2, 7),  -- Queen - News of the World
    (3, 3),  -- Michael Jackson - Thriller
    (4, 4),  -- Pink Floyd - The Dark Side of the Moon
    (5, 5);  -- Bob Dylan - Highway 61 Revisited
