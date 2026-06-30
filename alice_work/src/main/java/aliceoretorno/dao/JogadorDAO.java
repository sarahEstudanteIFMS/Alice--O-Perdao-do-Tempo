package aliceoretorno.dao;

import aliceoretorno.config.DataBaseConfig;
import aliceoretorno.model.Jogador;
import java.sql.*;

public class JogadorDAO {

    public Jogador obterOuCriarJogador(String username) {
        String sqlSelect = "SELECT * FROM jogadores WHERE username = ?";
        String sqlInsert = "INSERT INTO jogadores (username) VALUES (?) RETURNING *";
        
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement psSel = conn.prepareStatement(sqlSelect)) {
            
            psSel.setString(1, username);
            try (ResultSet rs = psSel.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                psIns.setString(1, username);
                try (ResultSet rsIns = psIns.executeQuery()) {
                    if (rsIns.next()) return mapRow(rsIns);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void actualizarProgresso(Jogador jogador) {
        String sql = "UPDATE jogadores SET engrenagens = ?, pontos_upgrade = ?, sanidade_max_upgrade = ?, tempo_base_upgrade = ? WHERE id = ?";
        try (Connection conn = DataBaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jogador.getEngrenagens());
            ps.setInt(2, jogador.getPontosUpgrade());
            ps.setInt(3, jogador.getSanidadeMaxUpgrade());
            ps.setInt(4, jogador.getTempoBaseUpgrade());
            ps.setInt(5, jogador.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Jogador mapRow(ResultSet rs) throws SQLException {
       Jogador j = new Jogador();
        j.setId(rs.getInt("id"));
        j.setUsername(rs.getString("username"));
        j.setEngrenagens(rs.getInt("engrenagens"));
        j.setPontosUpgrade(rs.getInt("pontos_upgrade"));
        j.setSanidadeMaxUpgrade(rs.getInt("sanidade_max_upgrade"));
        j.setTempoBaseUpgrade(rs.getInt("tempo_base_upgrade"));
        return j;
    }
}