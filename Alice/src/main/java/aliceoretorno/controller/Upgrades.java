package aliceoretorno.controller;

import aliceoretorno.dao.Jogador;
import aliceoretorno.dao.Ranking;
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

    private final Jogador jogador = new Jogador();
    private final Ranking ranking = new Ranking();

    @FXML
    public void initialize() {
        atualizarLabels();
        carregarRanking();
    }

    private void atualizarLabels() {
        lblPontosUpgrade.setText("Pontos Permanentes Disponíveis: " + Partida.jogadorLogado.getPontosUpgrade());
        lblStatusSanidade.setText("Sanidade Inicial Base: " + Partida.jogadorLogado.getSanidadeMaxUpgrade() + " HP");
        lblStatusTempo.setText("Tempo Base das Rodadas: " + Partida.jogadorLogado.getTempoBaseUpgrade() + "s");
    }

    private void carregarRanking() {
        listRanking.getItems().clear();
        listRanking.getItems().addAll(ranking.obterRankingFormatado());
    }

    @FXML
    void handleUpgradeSanidade(ActionEvent event) {
        if (Partida.jogadorLogado.getPontosUpgrade() >= 1) {
            Partida.jogadorLogado.setPontosUpgrade(Partida.jogadorLogado.getPontosUpgrade() - 1);
            Partida.jogadorLogado.setSanidadeMaxUpgrade(Partida.jogadorLogado.getSanidadeMaxUpgrade() + 1);
            jogador.actualizarProgresso(Partida.jogadorLogado);
            atualizarLabels();
        }
    }

    @FXML
    void handleUpgradeTempo(ActionEvent event) {
        if (Partida.jogadorLogado.getPontosUpgrade() >= 1) {
            Partida.jogadorLogado.setPontosUpgrade(Partida.jogadorLogado.getPontosUpgrade() - 1);
            Partida.jogadorLogado.setTempoBaseUpgrade(Partida.jogadorLogado.getTempoBaseUpgrade() + 5);
            jogador.actualizarProgresso(Partida.jogadorLogado);
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