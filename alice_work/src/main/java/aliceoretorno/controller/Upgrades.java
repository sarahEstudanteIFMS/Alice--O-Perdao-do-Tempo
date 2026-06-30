package aliceoretorno.controller;

import aliceoretorno.dao.JogadorDAO;
import aliceoretorno.dao.Ranking;
import aliceoretorno.model.Jogador;
import aliceoretorno.model.Partida;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;

public class Upgrades {

    @FXML private Label lblPontosUpgrade;
    @FXML private Label lblStatusSanidade;
    @FXML private Label lblStatusTempo;
    @FXML private ListView<String> listRanking;

    private final JogadorDAO jogadorDAO = new JogadorDAO();
    private final Ranking ranking = new Ranking();

    @FXML
    public void initialize() {
        // Guard: se por algum motivo chegar aqui sem jogador, mostra valores padrão
        atualizarLabels();
        carregarRanking();
    }

    private void atualizarLabels() {
        Jogador j = Partida.jogadorLogado;
        if (j == null) {
            lblPontosUpgrade.setText("Pontos Permanentes Disponíveis: —");
            lblStatusSanidade.setText("Sanidade Inicial Base: —");
            lblStatusTempo.setText("Tempo Base das Rodadas: —");
        } else {
            lblPontosUpgrade.setText("Pontos Permanentes Disponíveis: " + j.getPontosUpgrade());
            lblStatusSanidade.setText("Sanidade Inicial Base: " + j.getSanidadeMaxUpgrade() + " HP");
            lblStatusTempo.setText("Tempo Base das Rodadas: " + j.getTempoBaseUpgrade() + "s");
        }
    }

    private void carregarRanking() {
        listRanking.getItems().clear();
        listRanking.getItems().addAll(ranking.obterRankingFormatado());
    }

    @FXML
    void handleUpgradeSanidade(ActionEvent event) {
        if (Partida.jogadorLogado == null) return;
        Jogador j = Partida.jogadorLogado;
        if (j.getPontosUpgrade() >= 1) {
            j.setPontosUpgrade(j.getPontosUpgrade() - 1);
            j.setSanidadeMaxUpgrade(j.getSanidadeMaxUpgrade() + 1);
            jogadorDAO.actualizarProgresso(j);
            atualizarLabels();
        }
    }

    @FXML
    void handleUpgradeTempo(ActionEvent event) {
        if (Partida.jogadorLogado == null) return;
        Jogador j = Partida.jogadorLogado;
        if (j.getPontosUpgrade() >= 1) {
            j.setPontosUpgrade(j.getPontosUpgrade() - 1);
            j.setTempoBaseUpgrade(j.getTempoBaseUpgrade() + 5);
            jogadorDAO.actualizarProgresso(j);
            atualizarLabels();
        }
    }

    @FXML void handleExportarCSV(ActionEvent event) { ranking.exportarCSV("ranking_alice.csv"); }
    @FXML void handleExportarTXT(ActionEvent event) { ranking.exportarTXT("ranking_alice.txt"); }

    @FXML
    void handleVoltar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
