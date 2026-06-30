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
            if (Partida.runAtual != null) {
                Partida.runAtual.setTempoExtraItens(Partida.runAtual.getTempoExtraItens() + 1);
            }
            jogadorDAO.actualizarProgresso(j);
            lblMoedas.setText("Engrenagens: " + j.getEngrenagens());
            lblMensagem.setText("Item 'Tempo Extra' adicionado à sua Bolsa!");
        } else {
            lblMensagem.setText("Engrenagens insuficientes (Preço: 15).");
        }
    }

    @FXML
    void handleVoltar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
