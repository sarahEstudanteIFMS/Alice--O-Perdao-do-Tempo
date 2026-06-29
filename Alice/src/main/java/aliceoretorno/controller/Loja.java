package aliceoretorno.controller;

import aliceoretorno.dao.JogadorDAO;
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
    private final JogadorDAO jogador = new JogadorDAO();

    @FXML
    public void initialize() {
        lblMoedas.setText("Engrenagens: " + Partida.jogadorLogado.getEngrenagens());
    }

    @FXML
    void handleComprarTempo(ActionEvent event) {
        if (Partida.jogadorLogado.getEngrenagens() >= 15) {
            Partida.jogadorLogado.setEngrenagens(Partida.jogadorLogado.getEngrenagens() - 15);
            Partida.runAtual.setTempoExtraItens(Partida.runAtual.getTempoExtraItens() + 1);
            
            jogador.actualizarProgresso(Partida.jogadorLogado);
            lblMoedas.setText("Engrenagens: " + Partida.jogadorLogado.getEngrenagens());
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