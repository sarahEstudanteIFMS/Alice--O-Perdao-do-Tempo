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
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class Menu {

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
                Jogador jogador = jogadorDAO.obterOuCriarJogador(result.get().trim());
                if (jogador == null) {
                    new Alert(Alert.AlertType.ERROR,
                        "Não foi possível conectar ao banco de dados.\n" +
                        "Verifique se o PostgreSQL está rodando e se as credenciais\n" +
                        "em DataBaseConfig.java estão corretas.\n\n" +
                        "URL actual: jdbc:postgresql://localhost:5432/postgres").showAndWait();
                    return;
                }
                Partida.jogadorLogado = jogador;
            } else {
                return;
            }
        }
        Partida.runAtual = new Partida();
        carregarTela(event, "/fxml/Gameplay.fxml", "O Jardim das Flores - Gameplay");
    }

    @FXML
    void handleRanking(ActionEvent event) throws IOException {
        if (Partida.jogadorLogado == null) {
            pedirIdentificacaoParaAcesso(event, "/fxml/Upgrades.fxml", "Upgrades e Ranking");
        } else {
            carregarTela(event, "/fxml/Upgrades.fxml", "Upgrades e Ranking");
        }
    }

    @FXML
    void handleLoja(ActionEvent event) throws IOException {
        if (Partida.jogadorLogado == null) {
            pedirIdentificacaoParaAcesso(event, "/fxml/Loja.fxml", "Bazar do Tempo");
        } else {
            carregarTela(event, "/fxml/Loja.fxml", "Bazar do Tempo");
        }
    }

    @FXML void handleCreditos(ActionEvent event) throws IOException { carregarTela(event, "/fxml/Creditos.fxml", "Créditos"); }
    @FXML void handleEncerrar(ActionEvent event) { Platform.exit(); }

    /** Se o jogador ainda não fez login, pede identificação antes de abrir a tela */
    private void pedirIdentificacaoParaAcesso(ActionEvent event, String fxml, String titulo) throws IOException {
        TextInputDialog dialog = new TextInputDialog("Alice");
        dialog.setTitle("Identificação");
        dialog.setHeaderText("Identifique-se para continuar");
        dialog.setContentText("Digite seu nome de Viajante:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            Jogador jogador = jogadorDAO.obterOuCriarJogador(result.get().trim());
            if (jogador == null) {
                new Alert(Alert.AlertType.ERROR,
                    "Não foi possível conectar ao banco de dados.\n" +
                    "Verifique se o PostgreSQL está rodando.").showAndWait();
                return;
            }
            Partida.jogadorLogado = jogador;
            carregarTela(event, fxml, titulo);
        }
    }

    private void carregarTela(ActionEvent event, String fxmlPath, String titulo) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
}
