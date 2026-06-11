package com.alice.controller;

import com.alice.MainApp;
import com.alice.dao.BancoDados;
import com.alice.model.Jogador;
import com.alice.model.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class UpgradesController implements Initializable {

    @FXML private Label lblJogador;
    @FXML private Label lblMoedas;
    @FXML private Label lblResultado;
    @FXML private Label lblDano;
    @FXML private Label lblTempo;
    @FXML private Label lblSorte;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        atualizar();
    }

    @FXML
    public void onComprarDano() {
        comprar("dano", 5);
    }

    @FXML
    public void onComprarTempo() {
        comprar("tempo", 5);
    }

    @FXML
    public void onComprarSorte() {
        comprar("sorte", 5);
    }

    private void comprar(String tipo, int custo) {
        Jogador j = Sessao.get().getJogador();
        if (j.getMoedas() < custo) {
            lblResultado.setText("Moedas insuficientes! Precisa de " + custo + " ☕");
            return;
        }
        j.setMoedas(j.getMoedas() - custo);
        switch (tipo) {
            case "dano"  -> j.setUpgradeDano(j.getUpgradeDano() + 1);
            case "tempo" -> j.setUpgradeTempo(j.getUpgradeTempo() + 1);
            case "sorte" -> j.setUpgradeSorte(j.getUpgradeSorte() + 1);
        }
        BancoDados.salvarJogador(j);
        lblResultado.setText("Upgrade comprado com sucesso!");
        atualizar();
    }

    @FXML
    public void onJogarNovamente() {
        MainApp.trocarTela("gameplay.fxml", "Alice - Gameplay");
    }

    @FXML
    public void onLoja() {
        MainApp.trocarTela("loja.fxml", "Alice - Loja");
    }

    @FXML
    public void onMenu() {
        MainApp.trocarTela("menu.fxml", "Alice no País das Maravilhas");
    }

    private void atualizar() {
        Jogador j = Sessao.get().getJogador();
        lblJogador.setText(j.getNome() + " | Nível " + j.getNivel());
        lblMoedas.setText("☕ Moedas: " + j.getMoedas());
        lblDano.setText("Nível " + j.getUpgradeDano());
        lblTempo.setText("Nível " + j.getUpgradeTempo());
        lblSorte.setText("Nível " + j.getUpgradeSorte());

        lblResultado.setText("Run concluída! Pontos: " + Sessao.get().getPontosRun()
            + " | Moedas: +" + Sessao.get().getMoedasRun());
    }
}
