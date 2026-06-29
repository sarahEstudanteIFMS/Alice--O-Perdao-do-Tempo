package aliceoretorno.controller;

import aliceoretorno.dao.JogadorDAO;
import aliceoretorno.model.Jogador;
import aliceoretorno.model.Partida;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class Menu {

    // 👉 CORREÇÃO 1: Mudamos de "Jogador" para "JogadorDAO", 
    // pois é ele quem acessa o banco de dados.
    private final JogadorDAO jogadorDAO = new JogadorDAO();

    @FXML
    void handleComecar(ActionEvent event) throws IOException {
        if (Partida.jogadorLogado == null) {
            TextInputDialog dialog = new TextInputDialog("Alice");
            dialog.setTitle("Identificação");
            dialog.setHeaderText("Entre na Toca do Coelho");
            dialog.setContentText("Digite seu nome de Viajante:");
            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                // 👉 CORREÇÃO 2: Chamamos o método pelo jogadorDAO.
                // (Nota: caso lá no seu DAO o método chame só "obterOuCriarJogador", 
                // basta apagar o "DAO" do final do nome do método abaixo)
               Partida.jogadorLogado = jogadorDAO.obterOuCriarJogador(result.get().trim());
            } else {
                return;
            }
        }
        Partida.runAtual = new Partida();
        carregarTela(event, "/fxml/Gameplay.fxml", "O Jardim das Flores - Gameplay");
    }

    @FXML void handleRanking(ActionEvent event) throws IOException { carregarTela(event, "/fxml/Upgrades.fxml", "Upgrades e Ranking"); }
    @FXML void handleLoja(ActionEvent event) throws IOException { carregarTela(event, "/fxml/Loja.fxml", "Bazar do Tempo"); }
    @FXML void handleCreditos(ActionEvent event) throws IOException { carregarTela(event, "/fxml/Creditos.fxml", "Créditos"); }
    @FXML void handleEncerrar(ActionEvent event) { Platform.exit(); }

    private void carregarTela(ActionEvent event, String fxmlPath, String titulo) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
}