package com.alice.dao;

import com.alice.model.Jogador;
import com.alice.model.ItemInventario;
import com.alice.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza todas as operações de banco de dados (DAO simplificado).
 */
public class BancoDados {

    // ─── INICIALIZAÇÃO ───────────────────────────────────────────────────────────

    public static void inicializar() {
        String sqlJogadores = """
            CREATE TABLE IF NOT EXISTS jogadores (
                id SERIAL PRIMARY KEY,
                nome VARCHAR(100) UNIQUE NOT NULL,
                moedas INTEGER DEFAULT 0,
                nivel INTEGER DEFAULT 1,
                xp_total INTEGER DEFAULT 0,
                upgrade_dano INTEGER DEFAULT 0,
                upgrade_tempo INTEGER DEFAULT 0,
                upgrade_sorte INTEGER DEFAULT 0
            )
            """;

        String sqlPartidas = """
            CREATE TABLE IF NOT EXISTS partidas (
                id SERIAL PRIMARY KEY,
                jogador_id INTEGER REFERENCES jogadores(id),
                pontuacao INTEGER,
                moedas_ganhas INTEGER,
                data_hora TIMESTAMP DEFAULT NOW()
            )
            """;

        String sqlInventario = """
            CREATE TABLE IF NOT EXISTS inventario (
                id SERIAL PRIMARY KEY,
                jogador_id INTEGER REFERENCES jogadores(id),
                item_nome VARCHAR(100),
                item_tipo VARCHAR(50),
                quantidade INTEGER DEFAULT 1
            )
            """;

        try (Connection con = ConnectionFactory.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sqlJogadores);
            st.execute(sqlPartidas);
            st.execute(sqlInventario);
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }

    // ─── JOGADORES ────────────────────────────────────────────────────────────────

    public static Jogador buscarOuCriarJogador(String nome) {
        try (Connection con = ConnectionFactory.getConnection()) {
            // Tenta buscar
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM jogadores WHERE nome = ?");
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearJogador(rs);
            }
            // Cria novo
            PreparedStatement ins = con.prepareStatement(
                "INSERT INTO jogadores (nome) VALUES (?) RETURNING *");
            ins.setString(1, nome);
            ResultSet rs2 = ins.executeQuery();
            if (rs2.next()) return mapearJogador(rs2);
        } catch (SQLException e) {
            System.err.println("Erro buscarOuCriarJogador: " + e.getMessage());
        }
        return null;
    }

    public static void salvarJogador(Jogador j) {
        String sql = """
            UPDATE jogadores SET moedas=?, nivel=?, xp_total=?,
            upgrade_dano=?, upgrade_tempo=?, upgrade_sorte=?
            WHERE id=?
            """;
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, j.getMoedas());
            ps.setInt(2, j.getNivel());
            ps.setInt(3, j.getXpTotal());
            ps.setInt(4, j.getUpgradeDano());
            ps.setInt(5, j.getUpgradeTempo());
            ps.setInt(6, j.getUpgradeSorte());
            ps.setInt(7, j.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro salvarJogador: " + e.getMessage());
        }
    }

    // ─── PARTIDAS ─────────────────────────────────────────────────────────────────

    public static void salvarPartida(int jogadorId, int pontuacao, int moedas) {
        String sql = "INSERT INTO partidas (jogador_id, pontuacao, moedas_ganhas) VALUES (?,?,?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jogadorId);
            ps.setInt(2, pontuacao);
            ps.setInt(3, moedas);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro salvarPartida: " + e.getMessage());
        }
    }

    // ─── RANKING ──────────────────────────────────────────────────────────────────

    public static List<String[]> getRanking() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
            SELECT j.nome, MAX(p.pontuacao) as melhor, COUNT(p.id) as partidas
            FROM jogadores j
            LEFT JOIN partidas p ON p.jogador_id = j.id
            GROUP BY j.nome
            ORDER BY melhor DESC NULLS LAST
            LIMIT 10
            """;
        try (Connection con = ConnectionFactory.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int pos = 1;
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(pos++),
                    rs.getString("nome"),
                    String.valueOf(rs.getInt("melhor")),
                    String.valueOf(rs.getInt("partidas"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro getRanking: " + e.getMessage());
        }
        return lista;
    }

    // ─── INVENTÁRIO ───────────────────────────────────────────────────────────────

    public static void salvarItemInventario(int jogadorId, String nome, String tipo) {
        // Incrementa quantidade se já existe, senão insere
        String sqlCheck = "SELECT id, quantidade FROM inventario WHERE jogador_id=? AND item_nome=?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlCheck)) {
            ps.setInt(1, jogadorId);
            ps.setString(2, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                int qtd = rs.getInt("quantidade") + 1;
                PreparedStatement up = con.prepareStatement(
                    "UPDATE inventario SET quantidade=? WHERE id=?");
                up.setInt(1, qtd);
                up.setInt(2, id);
                up.executeUpdate();
            } else {
                PreparedStatement ins = con.prepareStatement(
                    "INSERT INTO inventario (jogador_id, item_nome, item_tipo) VALUES (?,?,?)");
                ins.setInt(1, jogadorId);
                ins.setString(2, nome);
                ins.setString(3, tipo);
                ins.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erro salvarItemInventario: " + e.getMessage());
        }
    }

    public static List<ItemInventario> getInventario(int jogadorId) {
        List<ItemInventario> lista = new ArrayList<>();
        String sql = "SELECT item_nome, item_tipo, quantidade FROM inventario WHERE jogador_id=?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jogadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new ItemInventario(
                    rs.getString("item_nome"),
                    rs.getString("item_tipo"),
                    rs.getInt("quantidade")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro getInventario: " + e.getMessage());
        }
        return lista;
    }

    // ─── HELPER ───────────────────────────────────────────────────────────────────

    private static Jogador mapearJogador(ResultSet rs) throws SQLException {
        Jogador j = new Jogador();
        j.setId(rs.getInt("id"));
        j.setNome(rs.getString("nome"));
        j.setMoedas(rs.getInt("moedas"));
        j.setNivel(rs.getInt("nivel"));
        j.setXpTotal(rs.getInt("xp_total"));
        j.setUpgradeDano(rs.getInt("upgrade_dano"));
        j.setUpgradeTempo(rs.getInt("upgrade_tempo"));
        j.setUpgradeSorte(rs.getInt("upgrade_sorte"));
        return j;
    }
}
