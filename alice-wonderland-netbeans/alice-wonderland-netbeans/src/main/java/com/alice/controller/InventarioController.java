package com.alice.controller;

import com.alice.dao.BancoDados;
import com.alice.model.ItemInventario;
import com.alice.model.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {

    @FXML private ListView<String> listaItens;
    @FXML private Label lblJogador;
    @FXML private Label lblTitulo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        var jogador = Sessao.get().getJogador();
        lblJogador.setText("Inventário de " + jogador.getNome());

        // Itens persistidos no banco (de runs anteriores)
        List<ItemInventario> doBanco = BancoDados.getInventario(jogador.getId());

        // Itens coletados na run atual (ainda não salvos)
        List<ItemInventario> daRun = Sessao.get().getItensRun();

        List<String> exibir = new ArrayList<>();
        if (!daRun.isEmpty()) {
            exibir.add("── Coletados nesta run ──");
            for (ItemInventario item : daRun) {
                exibir.add(item.toString());
            }
        }
        if (!doBanco.isEmpty()) {
            exibir.add("── Itens permanentes ──");
            for (ItemInventario item : doBanco) {
                exibir.add(item.toString());
            }
        }
        if (exibir.isEmpty()) {
            exibir.add("Inventário vazio. Colete xícaras de chá no Gameplay!");
        }

        listaItens.getItems().setAll(exibir);
    }

    @FXML
    public void onFechar() {
        Stage stage = (Stage) listaItens.getScene().getWindow();
        stage.close();
    }
}
