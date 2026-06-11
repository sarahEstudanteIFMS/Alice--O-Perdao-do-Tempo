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

public class LojaController implements Initializable {

    @FXML private Label lblJogador;
    @FXML private Label lblMoedas;
    @FXML private Label lblMensagem;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        atualizar();
    }

    /** Chapéu do Chapeleiro — +5 pontos por xícara (custo 8 moedas) */
    @FXML
    public void onComprarChapeu() {
        Jogador j = Sessao.get().getJogador();
        int custo = 8;
        if (j.getMoedas() < custo) {
            lblMensagem.setText("Moedas insuficientes! Precisa de " + custo + " ☕");
            return;
        }
        j.setMoedas(j.getMoedas() - custo);
        j.setUpgradeDano(j.getUpgradeDano() + 2); // item dá +2 dano bônus
        BancoDados.salvarJogador(j);
        BancoDados.salvarItemInventario(j.getId(), "Chapéu do Chapeleiro", "Equipamento");
        lblMensagem.setText("Chapéu adquirido! +2 ao dano permanente.");
        atualizar();
    }

    /** Poção de Alice — restaura 1 vida extra na próxima run (custo 6 moedas) */
    @FXML
    public void onComprarPocao() {
        Jogador j = Sessao.get().getJogador();
        int custo = 6;
        if (j.getMoedas() < custo) {
            lblMensagem.setText("Moedas insuficientes! Precisa de " + custo + " ☕");
            return;
        }
        j.setMoedas(j.getMoedas() - custo);
        BancoDados.salvarJogador(j);
        BancoDados.salvarItemInventario(j.getId(), "Poção de Alice", "Consumível");
        lblMensagem.setText("Poção adquirida! Ficará no inventário.");
        atualizar();
    }

    /** Chave do País das Maravilhas — desbloqueia chefe (custo 15 moedas) */
    @FXML
    public void onComprarChave() {
        Jogador j = Sessao.get().getJogador();
        int custo = 15;
        if (j.getMoedas() < custo) {
            lblMensagem.setText("Moedas insuficientes! Precisa de " + custo + " ☕");
            return;
        }
        j.setMoedas(j.getMoedas() - custo);
        BancoDados.salvarJogador(j);
        BancoDados.salvarItemInventario(j.getId(), "Chave das Maravilhas", "Chave");
        lblMensagem.setText("Chave adquirida! Você pode enfrentar o Tempo!");
        atualizar();
    }

    @FXML
    public void onVoltar() {
        MainApp.trocarTela("upgrades.fxml", "Alice - Upgrades");
    }

    @FXML
    public void onBoss() {
        Jogador j = Sessao.get().getJogador();
        if (j.getNivel() < 3) {
            lblMensagem.setText("Você precisa ser nível 3 para enfrentar o Tempo! (Nível atual: " + j.getNivel() + ")");
            return;
        }
        MainApp.trocarTela("boss.fxml", "Alice - Confronto com o Tempo");
    }

    private void atualizar() {
        Jogador j = Sessao.get().getJogador();
        lblJogador.setText(j.getNome() + " | Nível " + j.getNivel());
        lblMoedas.setText("☕ Moedas: " + j.getMoedas());
    }
}
