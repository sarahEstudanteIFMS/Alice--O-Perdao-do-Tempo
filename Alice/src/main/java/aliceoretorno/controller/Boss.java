package aliceoretorno.controller;

import aliceoretorno.dao.Ranking;
import aliceoretorno.model.Partida;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXML;
import java.io.IOException;

public class Boss {

    @FXML private Label lblEnigmaBoss;
    @FXML private Label lblTimerBoss;
    @FXML private TextField txtRespostaBoss;

    private Timeline timeline;
    private int tempoRestante;
    private final Ranking ranking = new Ranking();

    @FXML
    public void initialize() {
        lblEnigmaBoss.setText(Partida.runAtual.getEnigmaAtual().getPergunta());
        tempoRestante = Partida.jogadorLogado.getTempoBaseUpgrade();
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            tempoRestante--;
            lblTimerBoss.setText("Tempo Restante: " + tempoRestante + "s");
            if (tempoRestante <= 0) {
                timeline.stop();
                derrotaNoBoss();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    void handleFinalizarBoss(ActionEvent event) throws IOException {
        Partida run = Partida.runAtual;
        if (run.getEnigmaAtual().validarResposta(txtRespostaBoss.getText())) {
            timeline.stop();
            
            Partida.jogadorLogado.setPontosUpgrade(Partida.jogadorLogado.getPontosUpgrade() + 5);
            
            int scoreFinal = (run.getSanidadeAtual() * 200) + (run.getEngrenagensGanhasNaRun() * 10);
            ranking.salvarPontuacao(Partida.jogadorLogado.getId(), scoreFinal);
            
            new aliceoretorno.dao.Jogador().actualizarProgresso(Partida.jogadorLogado);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Vitória!");
            alert.setHeaderText("Você derrotou O Tempo!");
            alert.setContentText("Pontuação da Run: " + scoreFinal + "\nVocê ganhou 5 Pontos de Upgrade Permanente!");
            alert.showAndWait();

            voltarAoMenu(event);
        } else {
            txtRespostaBoss.setStyle("-fx-border-color: darkred; -fx-background-color: #ffb3b3;");
        }
    }

    private void derrotaNoBoss() {
        Partida.runAtual.resetarRunDaDerrota();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("O Tempo te consumiu no último segundo... Reiniciando Run do Nível 1!");
        alert.showAndWait();
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Gameplay.fxml"));
            Stage stage = (Stage) lblEnigmaBoss.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void voltarAoMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}