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
import javafx.scene.control.Button;
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
    @FXML private Label lblDicas;
    @FXML private TextField txtResposta;
    @FXML private Button btnDica;

    private Timeline timeline;
    private int tempoRestante;
    private int tempoMaximoDaRodada;
    private int dicasRestantesNaPergunta;

    @FXML
    public void initialize() {
        prepararRodada();
    }

    /** Recalcula tempo/dicas conforme a fase do nível atual e (re)inicia a rodada. */
    private void prepararRodada() {
        Partida run = Partida.runAtual;

        // Tempo extra comprado na Loja/Inventário é somado apenas uma vez, ao entrar na rodada
        int tempoExtra = run.getTempoExtraItens();
        if (tempoExtra > 0) {
            run.setTempoExtraItens(0);
        }

        tempoMaximoDaRodada = run.getTempoFaseAtual();
        tempoRestante = tempoMaximoDaRodada + (tempoExtra * 15); // cada item dá 15s extras
        dicasRestantesNaPergunta = run.getDicasDisponiveisFaseAtual();

        atualizarInterface();
        inicializarTimer();
    }

    private void atualizarInterface() {
        Partida run = Partida.runAtual;
        Enigma enigma = run.getEnigmaAtual();

        lblNivel.setText("Nível: " + run.getNivelAtual() + " / " + Partida.TOTAL_NIVEIS
                + "  (Fase " + run.getFaseAtual() + "/3)");
        lblSanidade.setText("Sanidade: " + run.getSanidadeAtual());
        lblEnigma.setText(enigma.getPergunta());
        txtResposta.clear();
        txtResposta.setStyle("");

        if (lblDicas != null) {
            lblDicas.setText("Dicas disponíveis: " + dicasRestantesNaPergunta);
        }
        if (btnDica != null) {
            btnDica.setDisable(dicasRestantesNaPergunta <= 0 || !enigma.temDica());
        }
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
    void handleUsarDica(ActionEvent event) {
        Partida run = Partida.runAtual;
        Enigma enigma = run.getEnigmaAtual();

        if (dicasRestantesNaPergunta <= 0 || !enigma.temDica()) {
            return;
        }
        dicasRestantesNaPergunta--;
        if (lblDicas != null) {
            lblDicas.setText("Dicas disponíveis: " + dicasRestantesNaPergunta);
        }
        if (btnDica != null) {
            btnDica.setDisable(dicasRestantesNaPergunta <= 0);
        }
        exibirAlerta(Alert.AlertType.INFORMATION, "Dica", enigma.getDica());
    }

    @FXML
    void handleConfirmar(ActionEvent event) throws IOException {
        Partida run = Partida.runAtual;
        Enigma enigma = run.getEnigmaAtual();

        if (enigma.validarResposta(txtResposta.getText())) {
            timeline.stop();
            int tempoGasto = tempoMaximoDaRodada - tempoRestante;

            // Premia agilidade: resposta correta rapidamente ganha mais Engrenagens (pontuação da loja)
            int recompensa = calcularRecompensa(tempoGasto, run.getFaseAtual());
            if (recompensa > 0) {
                run.somarEngrenagens(recompensa);
                Partida.jogadorLogado.setEngrenagens(Partida.jogadorLogado.getEngrenagens() + recompensa);
            }

            run.setNivelAtual(run.getNivelAtual() + 1);

            if (run.getNivelAtual() > Partida.TOTAL_NIVEIS) {
                irParaBoss(event);
            } else {
                prepararRodada();
            }
        } else {
            txtResposta.setStyle("-fx-border-color: red; -fx-background-color: #ffcccc;");
        }
    }

    /** Quanto mais rápido e mais avançada a fase, maior a recompensa em Engrenagens. */
    private int calcularRecompensa(int tempoGasto, int fase) {
        int limiteAgilidade = tempoMaximoDaRodada / 3; // responder no 1º terço do tempo = ágil
        if (tempoGasto > limiteAgilidade) {
            return fase; // recompensa mínima por fase, mesmo sem agilidade
        }
        return 10 * fase; // bônus de agilidade escala com a dificuldade
    }

    private void processarDerrotaPorTempo() {
        Partida run = Partida.runAtual;
        run.setSanidadeAtual(run.getSanidadeAtual() - 1);

        boolean zerouSanidade = run.getSanidadeAtual() <= 0;
        int inicioFase = run.getInicioDaFaseAtual();
        run.resetarRunDaDerrota();

        if (zerouSanidade) {
            exibirAlerta(Alert.AlertType.ERROR,
                "Fim da Run!",
                "Sua sanidade zerou — O Tempo te consumiu!\nReiniciando toda a run no Nível 1...");
        } else {
            exibirAlerta(Alert.AlertType.WARNING,
                "Tempo Esgotado!",
                "Você perdeu 1 de Sanidade.\nSanidade restante: " + run.getSanidadeAtual()
                + "\nVocê volta para o início da sua fase (Nível " + inicioFase + ").");
        }

        prepararRodada();
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
