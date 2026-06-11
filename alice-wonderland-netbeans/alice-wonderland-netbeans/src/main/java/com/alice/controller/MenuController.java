package com.alice.controller;

import com.alice.MainApp;
import com.alice.dao.BancoDados;
import com.alice.model.Jogador;
import com.alice.model.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class MenuController {

    @FXML private TextField campoNome;

    @FXML
    public void onJogar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            alerta("Insira seu nome para começar!");
            return;
        }
        Jogador j = BancoDados.buscarOuCriarJogador(nome);
        if (j == null) {
            alerta("Erro ao conectar ao banco de dados.\nVerifique as configurações em ConnectionFactory.java");
            return;
        }
        Sessao.get().setJogador(j);
        MainApp.trocarTela("gameplay.fxml", "Alice - Gameplay");
    }

    @FXML
    public void onRanking() {
        MainApp.trocarTela("ranking.fxml", "Alice - Ranking");
    }

    @FXML
    public void onCreditos() {
        MainApp.trocarTela("creditos.fxml", "Alice - Créditos");
    }

    @FXML
    public void onEncerrar() {
        System.exit(0);
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
