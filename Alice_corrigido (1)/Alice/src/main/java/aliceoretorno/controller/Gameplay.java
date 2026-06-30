package aliceoretorno.controller;

import aliceoretorno.model.Partida;
import aliceoretorno.model.Enigma;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class Gameplay {

    @FXML private Label lblEnigma;
    @FXML private Label lblTimer;
    @FXML private Label lblSanidade;
    @FXML private Label lblNivel;
    @FXML private TextField txtResposta;

    private Timeline timeline;
    private int tempoRestante;
    private int tempoMaximoDaRodada;

    @FXML
    public void initialize() {
        tempoMaximoDaRodada = Partida.jogadorLogado.getTempoBaseUpgrade();
        tempoRestante = tempoMaximoDaRodada;
        atualizarInterface();
        inicializarTimer();
    }

    private void atualizarInterface() {
        Partida run = Partida.runAtual;
        lblNivel.setText("Nível: " + run.getNivelAtual() + " / 5");
        lblSanidade.setText("Sanidade: " + run.getSanidadeAtual());
        lblEnigma.setText(run.getEnigmaAtual().getPergunta());
        txtResposta.clear();
        txtResposta.setStyle("");
    }

    private void inicializarTimer() {
        if (timeline != null) timeline.stop();
        lblTimer.setText("Tempo: " + tempoRestante + "s");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            tempoRestante--;
            lblTimer.setText("Tempo: " + tempoRestante + "s");
            if (tempoRestante <= 0) {
                timeline.stop();
                processarDerrotaPorTempo();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    void handleConfirmar(ActionEvent event) throws IOException {
        Partida run = Partida.runAtual;
        Enigma enigma = run.getEnigmaAtual();

        if (enigma.validarResposta(txtResposta.getText())) {
            timeline.stop();
            int tempoGasto = tempoMaximoDaRodada - tempoRestante;

            // Premia agilidade: resposta correta em menos de 10s ganha Engrenagens
            if (tempoGasto < 10) {
                run.somarEngrenagens(10);
                Partida.jogadorLogado.setEngrenagens(Partida.jogadorLogado.getEngrenagens() + 10);
            }

            run.setNivelAtual(run.getNivelAtual() + 1);

            if (run.getNivelAtual() == 5) {
                // Nível 5 → Boss
                irParaBoss(event);
            } else {
                // Próximo nível — reset do timer
                tempoRestante = tempoMaximoDaRodada;
                atualizarInterface();
                inicializarTimer();
            }
        } else {
            txtResposta.setStyle("-fx-border-color: red; -fx-background-color: #ffcccc;");
        }
    }

    private void processarDerrotaPorTempo() {
        Partida run = Partida.runAtual;
        run.setSanidadeAtual(run.getSanidadeAtual() - 1);

        if (run.getSanidadeAtual() <= 0) {
            // Permadeath da sessão
            run.resetarRunDaDerrota();
            exibirAlerta(Alert.AlertType.ERROR,
                "Fim da Run!",
                "Sua sanidade zerou — O Tempo te consumiu!\nReiniciando no Nível 1...");
        } else {
            // Perde sanidade mas continua a run desde o nível 1
            run.resetarRunDaDerrota();
            exibirAlerta(Alert.AlertType.WARNING,
                "Tempo Esgotado!",
                "Você perdeu 1 de Sanidade.\nSanidade restante: " + run.getSanidadeAtual() + "\nReiniciando no Nível 1.");
        }

        tempoRestante = tempoMaximoDaRodada;
        atualizarInterface();
        inicializarTimer();
    }

    @FXML
    void handleAbrirInventario(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Inventory.fxml"));
        Parent root = loader.load();

        Inventory invCtrl = loader.getController();
        invCtrl.setGameplayController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Bolsa de Alice");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /** Chamado pelo Inventory para adicionar tempo ao timer em execução */
    public void adicionarTempoItem(int segundos) {
        this.tempoRestante += segundos;
        this.lblTimer.setText("Tempo: " + tempoRestante + "s");
    }

    private void irParaBoss(ActionEvent event) throws IOException {
        if (timeline != null) timeline.stop();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Boss.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("O Confronto Final contra O Tempo");
    }

    @FXML
    void handleVoltarMenu(ActionEvent event) throws IOException {
        if (timeline != null) timeline.stop();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
