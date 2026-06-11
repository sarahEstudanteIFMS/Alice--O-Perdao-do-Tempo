package com.alice.controller;

import com.alice.MainApp;
import com.alice.dao.BancoDados;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RankingController implements Initializable {

    @FXML private TableView<String[]> tabela;
    @FXML private TableColumn<String[], String> colPos;
    @FXML private TableColumn<String[], String> colNome;
    @FXML private TableColumn<String[], String> colPontos;
    @FXML private TableColumn<String[], String> colPartidas;
    @FXML private Label lblMensagem;

    private List<String[]> dados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colPos.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colNome.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colPontos.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colPartidas.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        carregar();
    }

    private void carregar() {
        dados = BancoDados.getRanking();
        tabela.getItems().setAll(dados);
        if (dados.isEmpty()) {
            lblMensagem.setText("Nenhuma pontuação registrada ainda. Verifique a conexão com o banco ou jogue uma partida!");
        } else {
            lblMensagem.setText("");
        }
    }

    @FXML
    public void onExportarCSV() {
        exportar("csv");
    }

    @FXML
    public void onExportarTXT() {
        exportar("txt");
    }

    private void exportar(String formato) {
        if (dados == null || dados.isEmpty()) {
            lblMensagem.setText("Nada para exportar.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("ranking_alice." + formato);
        if (formato.equals("csv")) {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        } else {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        }
        var arquivo = fc.showSaveDialog(MainApp.primaryStage);
        if (arquivo == null) return;

        try (FileWriter fw = new FileWriter(arquivo)) {
            if (formato.equals("csv")) {
                fw.write("Posicao,Nome,Pontuacao,Partidas\n");
                for (String[] linha : dados) {
                    fw.write(String.join(",", linha) + "\n");
                }
            } else {
                fw.write("=== RANKING - ALICE NO PAÍS DAS MARAVILHAS ===\n\n");
                for (String[] linha : dados) {
                    fw.write(String.format("%2s. %-20s | Pontos: %-6s | Partidas: %s%n",
                        linha[0], linha[1], linha[2], linha[3]));
                }
            }
            lblMensagem.setText("Arquivo exportado com sucesso!");
        } catch (IOException e) {
            lblMensagem.setText("Erro ao exportar: " + e.getMessage());
        }
    }

    @FXML
    public void onAtualizar() {
        carregar();
    }

    @FXML
    public void onVoltar() {
        MainApp.trocarTela("menu.fxml", "Alice no País das Maravilhas");
    }
}
