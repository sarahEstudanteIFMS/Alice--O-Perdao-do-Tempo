-- =============================================================
--  Alice: O Retorno — Script de criação do banco de dados
--  Execute no PostgreSQL como superusuário:
--    psql -U postgres -f setup_db.sql
-- =============================================================

-- 1. Criar a base de dados (execute separado se já tiver o psql aberto)
-- CREATE DATABASE alice_db;
-- \c alice_db

-- 2. Tabela de jogadores (persistência permanente)
CREATE TABLE IF NOT EXISTS jogadores (
    id                  SERIAL PRIMARY KEY,
    username            VARCHAR(100) NOT NULL UNIQUE,
    engrenagens         INT NOT NULL DEFAULT 0,
    pontos_upgrade      INT NOT NULL DEFAULT 0,
    sanidade_max_upgrade INT NOT NULL DEFAULT 3,
    tempo_base_upgrade  INT NOT NULL DEFAULT 30
);

-- 3. Tabela de ranking (histórico de runs concluídas)
CREATE TABLE IF NOT EXISTS ranking (
    id              SERIAL PRIMARY KEY,
    jogador_id      INT NOT NULL REFERENCES jogadores(id) ON DELETE CASCADE,
    pontuacao       INT NOT NULL DEFAULT 0,
    data_conclusao  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índice para acelerar a query do Top 10
CREATE INDEX IF NOT EXISTS idx_ranking_pontuacao ON ranking (pontuacao DESC);

-- =============================================================
--  Configuração rápida (substitua os valores abaixo conforme
--  o seu ambiente antes de executar o jogo):
--
--  URL:      jdbc:postgresql://localhost:5432/alice_db
--  USER:     postgres
--  PASSWORD: postgres
--
--  Edite DataBaseConfig.java se os valores forem diferentes.
-- =============================================================
