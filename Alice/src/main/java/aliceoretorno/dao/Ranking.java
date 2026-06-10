package aliceoretorno.dao;

import aliceoretorno.config.DataBaseConfig;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ranking{

    public void salvarPontuacao(int jogadorId, int pontos) {
        String sql = "INSERT INTO ranking (jogador_id, pontuacao) VALUES (?, ?)";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jogadorId);
            ps.setInt(2, pontos);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> obterRankingFormatado() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT j.username, r.pontuacao FROM ranking r " +
                     "JOIN jogadores j ON r.jogador_id = j.id ORDER BY r.pontuacao DESC LIMIT 10";
        try (Connection conn = DataBaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int posicao = 1;
            while (rs.next()) {
                lista.add(posicao + "° - " + rs.getString("username") + " | Pontos: " + rs.getInt("pontuacao"));
                posicao++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void exportarCSV(String caminhoArquivo) {
        String sql = "SELECT j.username, r.pontuacao FROM ranking r JOIN jogadores j ON r.jogador_id = j.id ORDER BY r.pontuacao DESC";
        try (Connection conn = DataBaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            
            writer.println("Posicao;Jogador;Pontuacao");
            int pos = 1;
            while (rs.next()) {
                writer.println(pos + ";" + rs.getString("username") + ";" + rs.getInt("pontuacao"));
                pos++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportarTXT(String caminhoArquivo) {
        String sql = "SELECT j.username, r.pontuacao, r.data_conclusao FROM ranking r JOIN jogadores j ON r.jogador_id = j.id ORDER BY r.pontuacao DESC";
        try (Connection conn = DataBaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            
            writer.println("==================================================");
            writer.println("        RANKING CRONOLÓGICO - ALICE GAME          ");
            writer.println("==================================================");
            int pos = 1;
            while (rs.next()) {
                writer.printf("%02d. %-20s -> Pontos: %-5d [%s]\n", 
                    pos, rs.getString("username"), rs.getInt("pontuacao"), rs.getTimestamp("data_conclusao"));
                pos++;
            }
            writer.println("==================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}