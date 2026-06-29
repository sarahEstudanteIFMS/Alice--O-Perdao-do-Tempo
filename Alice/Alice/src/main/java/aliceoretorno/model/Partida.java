package aliceoretorno.model;

import java.util.ArrayList;
import java.util.List;

public class Partida {

    // Estado estático global para a sessão (equivalente a variáveis de aplicação)
    public static Jogador jogadorLogado;
    public static Partida runAtual;

    private int nivelAtual;
    private int sanidadeAtual;
    private int engrenagensGanhasNaRun;
    private int tempoExtraItens;
    private final List<Enigma> enigmas = new ArrayList<>();

    public Partida() {
        this.nivelAtual = 1;
        this.sanidadeAtual = (jogadorLogado != null) ? jogadorLogado.getSanidadeMaxUpgrade() : 3;
        this.engrenagensGanhasNaRun = 0;
        this.tempoExtraItens = 0;
        inicializarEnigmas();
    }

    private void inicializarEnigmas() {
        // Nível 1 — validação flexível (contém a palavra-chave "depende")
        enigmas.add(new Enigma(
            "Nível 1 — O Chapeleiro pergunta: \"Qual a cor do céu?\"",
            "depende", true));

        // Nível 2 — cálculo de tempo (resposta exata)
        enigmas.add(new Enigma(
            "Nível 2 — O coelho atrasou 20s no banho, 35s no café, 1m34s para sair e 56s no caminho. "
            + "Qual o atraso total no formato XmXs?",
            "3m25s", false));

        // Nível 3 — identidade da Alice
        enigmas.add(new Enigma(
            "Nível 3 — Você cresce e encolhe, mas seu nome não muda desde que caiu na toca. Quem é você?",
            "Alice", false));

        // Nível 4 — acordar do pesadelo
        enigmas.add(new Enigma(
            "Nível 4 — O que você deve fazer para fazer este mundo de pesadelos sumir e as cartas virarem folhas secas?",
            "Acordar", false));

        // Nível 5 (Boss) — O Esquecimento
        enigmas.add(new Enigma(
            "CONFRONTO FINAL — Eu devoro impérios e transformo sonhos de infância em pó. Qual o meu nome mais cruel?",
            "Esquecimento", false));
    }

    /**
     * Retorna o enigma do nível atual.
     * O nível 5 está na lista mas é mostrado pelo Boss.java.
     */
    public Enigma getEnigmaAtual() {
        int idx = nivelAtual - 1;
        if (idx >= 0 && idx < enigmas.size()) {
            return enigmas.get(idx);
        }
        return null;
    }

    /**
     * Reseta para o início de uma nova run após derrota,
     * mantendo a sanidade atual (já decrementada pelo chamador).
     */
    public void resetarRunDaDerrota() {
        this.nivelAtual = 1;
        // A sanidade já foi decrementada antes desta chamada;
        // se zerou, reiniciamos do máximo para começar nova run.
        if (this.sanidadeAtual <= 0) {
            this.sanidadeAtual = (jogadorLogado != null) ? jogadorLogado.getSanidadeMaxUpgrade() : 3;
        }
    }

    // ── Getters e Setters ──────────────────────────────────────
    public int getNivelAtual()                  { return nivelAtual; }
    public void setNivelAtual(int nivelAtual)   { this.nivelAtual = nivelAtual; }

    public int getSanidadeAtual()               { return sanidadeAtual; }
    public void setSanidadeAtual(int s)         { this.sanidadeAtual = s; }

    public int getEngrenagensGanhasNaRun()      { return engrenagensGanhasNaRun; }
    public void somarEngrenagens(int qtd)       { this.engrenagensGanhasNaRun += qtd; }

    public int getTempoExtraItens()             { return tempoExtraItens; }
    public void setTempoExtraItens(int t)       { this.tempoExtraItens = t; }
}
