-- =============================================================
--  Alice: O Retorno — Script de criação das tabelas
--
--  COMO EXECUTAR:
--  1. Abra o pgAdmin ou o SQL Shell (psql)
--  2. Conecte no banco "postgres" (já existe por padrão)
--  3. Execute este script inteiro
--
--  OU pelo terminal:
--    psql -U postgres -d postgres -f setup_db.sql
--
--  Se quiser um banco separado chamado "alice_db":
--    1. Crie: CREATE DATABASE alice_db;
--    2. Altere a URL em DataBaseConfig.java para:
--       jdbc:postgresql://localhost:5432/alice_db
--    3. Execute: psql -U postgres -d alice_db -f setup_db.sql
-- =============================================================

CREATE TABLE IF NOT EXISTS jogadores (
    id                   SERIAL PRIMARY KEY,
    username             VARCHAR(100) NOT NULL UNIQUE,
    engrenagens          INT NOT NULL DEFAULT 0,
    pontos_upgrade       INT NOT NULL DEFAULT 0,
    sanidade_max_upgrade INT NOT NULL DEFAULT 3,
    tempo_base_upgrade   INT NOT NULL DEFAULT 30
);

CREATE TABLE IF NOT EXISTS ranking (
    id             SERIAL PRIMARY KEY,
    jogador_id     INT NOT NULL REFERENCES jogadores(id) ON DELETE CASCADE,
    pontuacao      INT NOT NULL DEFAULT 0,
    data_conclusao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ranking_pontuacao ON ranking (pontuacao DESC);

-- Verificar se criou certo:
SELECT 'Tabelas criadas com sucesso!' AS status;
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public' AND table_name IN ('jogadores','ranking');
