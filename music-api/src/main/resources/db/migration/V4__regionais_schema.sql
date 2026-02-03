-- Regional table for external API sync
-- Using source_id to track the external system's ID for upsert logic
CREATE TABLE regional (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    synced_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regional_source_id ON regional(source_id);
CREATE INDEX idx_regional_nome ON regional(nome);
