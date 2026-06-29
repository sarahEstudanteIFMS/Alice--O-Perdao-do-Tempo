package com.alice.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Dados da sessão atual (jogador logado e run em andamento).
 * Singleton simples sem framework.
 */
public class Sessao {

    private static Sessao instancia;

    private Jogador jogador;

    // Dados da run atual
    private int pontosRun;
    private int moedasRun;
    private List<ItemInventario> itensRun = new ArrayList<>();

    private Sessao() {}

    public static Sessao get() {
        if (instancia == null) instancia = new Sessao();
        return instancia;
    }

    public void iniciarRun() {
        pontosRun = 0;
        moedasRun = 0;
        itensRun.clear();
    }

    // ── Jogador ──────────────────────────────────────────────────────────────────

    public Jogador getJogador() { return jogador; }
    public void setJogador(Jogador jogador) { this.jogador = jogador; }

    // ── Run ──────────────────────────────────────────────────────────────────────

    public int getPontosRun() { return pontosRun; }
    public void setPontosRun(int pontosRun) { this.pontosRun = pontosRun; }

    public int getMoedasRun() { return moedasRun; }
    public void setMoedasRun(int moedasRun) { this.moedasRun = moedasRun; }

    public List<ItemInventario> getItensRun() { return itensRun; }

    public void adicionarItem(String nome, String tipo) {
        // Verifica se já existe na lista da run
        for (ItemInventario item : itensRun) {
            if (item.getNome().equals(nome)) {
                itensRun.remove(item);
                itensRun.add(new ItemInventario(nome, tipo, item.getQuantidade() + 1));
                return;
            }
        }
        itensRun.add(new ItemInventario(nome, tipo, 1));
    }
}
