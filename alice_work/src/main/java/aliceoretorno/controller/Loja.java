package aliceoretorno.controller;

import aliceoretorno.dao.JogadorDAO;
import aliceoretorno.model.Jogador;
import aliceoretorno.model.Partida;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class Loja {

    @FXML private Label lblMoedas;
    @FXML private Label lblMensagem;
    private final JogadorDAO jogadorDAO = new JogadorDAO();

    // Quando != null, a Loja foi aberta DURANTE uma run (modal sobre o Gameplay).
    // Nesse caso, o tempo comprado é aplicado direto no timer em andamento.
    private Gameplay gameplayController;

    public void setGameplayController(Gameplay ctrl) {
        this.gameplayController = ctrl;
    }

    @FXML
    public void initialize() {
        Jogador j = Partida.jogadorLogado;
        lblMoedas.setText("Engrenagens: " + (j != null ? j.getEngrenagens() : 0));
    }

    @FXML
    void handleComprarTempo(ActionEvent event) {
        if (Partida.jogadorLogado == null) {
            lblMensagem.setText("Identifique-se primeiro (clique em Começar).");
            return;
        }
        Jogador j = Partida.jogadorLogado;
        if (j.getEngrenagens() >= 15) {
            j.setEngrenagens(j.getEngrenagens() - 15);
            jogadorDAO.actualizarProgresso(j);
            lblMoedas.setText("Engrenagens: " + j.getEngrenagens());

            if (gameplayController != null) {
                // Comprado durante a run: aplica os +15s direto no cronômetro em andamento
                gameplayController.adicionarTempoItem(15);
                lblMensagem.setText("+15s adicionados ao seu tempo atual!");
            } else if (Partida.runAtual != null) {
                // Comprado fora da run (ex: pelo Menu): guarda na Bolsa para usar depois
                Partida.runAtual.setTempoExtraItens(Partida.runAtual.getTempoExtraItens() + 1);
                lblMensagem.setText("Item 'Tempo Extra' adicionado à sua Bolsa!");
            } else {
                lblMensagem.setText("Item 'Tempo Extra' comprado! Inicie uma run para usá-lo.");
            }
        } else {
            lblMensagem.setText("Engrenagens insuficientes (Preço: 15).");
        }
    }

    @FXML
    void handleVoltar(ActionEvent event) throws IOException {
        if (gameplayController != null) {
            // Aberta como modal durante o jogo: apenas fecha a janela e retoma a partida
            Stage stage = (Stage) lblMoedas.getScene().getWindow();
            stage.close();
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
