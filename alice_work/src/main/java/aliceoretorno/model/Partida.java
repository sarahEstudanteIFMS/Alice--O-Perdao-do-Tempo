package aliceoretorno.model;

import java.util.ArrayList;
import java.util.List;

public class Partida {

    // Estado estático global para a sessão (equivalente a variáveis de aplicação)
    public static Jogador jogadorLogado;
    public static Partida runAtual;

    public static final int TOTAL_NIVEIS = 15;
    public static final int TAMANHO_FASE = 5; // perguntas por fase/nível de dificuldade

    private int nivelAtual;          // 1..15
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

        // ============== FASE 1 (Perguntas 1-5) — Tempo: 40s — Sem dicas ==============
        enigmas.add(new Enigma(
            "Nível 1 — O Chapeleiro pergunta: \"Qual a cor do céu?\"",
            "depende", true));

        enigmas.add(new Enigma(
            "Nível 2 — O Gato de Cheshire sorri: \"Sou visto até quando não estou aqui. O que sobra de mim quando eu vou embora?\"",
            "sorriso", true));

        enigmas.add(new Enigma(
            "Nível 3 — A Lagarta sopra fumaça e pergunta: \"Quem é você, afinal?\"",
            "Alice", false));

        enigmas.add(new Enigma(
            "Nível 4 — O coelho atrasou 20s no banho, 35s no café, 1m34s para sair e 56s no caminho. "
            + "Qual o atraso total no formato XmXs?",
            "3m25s", false));

        enigmas.add(new Enigma(
            "Nível 5 — A Rainha de Copas grita: \"Quanto é seis vezes sete, sua tola?\"",
            "42", false));

        // ============== FASE 2 (Perguntas 6-10) — Tempo: 30s — 1 dica ==============
        enigmas.add(new Enigma(
            "Nível 6 — Tweedledum e Tweedledee dizem juntos: \"O que tem cidades, mas nenhuma casa; "
            + "florestas, mas nenhuma árvore; e rios, mas nenhuma água?\"",
            "mapa", true,
            "Pense em algo que você desdobra para se orientar em uma viagem."));

        enigmas.add(new Enigma(
            "Nível 7 — Você cresce e encolhe, mas seu nome não muda desde que caiu na toca. Quem é você?",
            "Alice", false,
            "É o seu próprio nome, repetido desde a primeira pergunta da Lagarta."));

        enigmas.add(new Enigma(
            "Nível 8 — O Chá das 5 nunca termina. Se já são 17h e o relógio do Coelho está sempre 1h25min atrasado, "
            + "que horas o relógio dele está marcando agora?",
            "15h35", false,
            "Subtraia 1h25min de 17h00."));

        enigmas.add(new Enigma(
            "Nível 9 — As cartas de baralho marcham em fila: \"Quanto mais você tira de mim, maior eu fico. O que sou?\"",
            "buraco", true,
            "Pense na toca por onde Alice caiu no início de tudo."));

        enigmas.add(new Enigma(
            "Nível 10 — O Espelho da Rainha Branca sussurra: \"Para me atravessar, você precisa fazer o quê com tudo que sabe?\"",
            "inverter", true,
            "No País do Espelho, a lógica normal funciona ao contrário."));

        // ============== FASE 3 (Perguntas 11-15) — Tempo: 15s — 2 dicas ==============
        enigmas.add(new Enigma(
            "Nível 11 — O Coelho Branco, apavorado, grita: \"Estou atrasado para o quê, mais do que tudo nesse mundo?\"",
            "casamento", true,
            "Ele corre sempre carregando um relógio em direção a um compromisso formal."));

        enigmas.add(new Enigma(
            "Nível 12 — As flores falantes cochicham sobre Alice: \"Ela não é uma flor de verdade porque lhe falta o quê?\"",
            "raiz", true,
            "É aquilo que prende uma planta no solo e a mantém presa a um lugar."));

        enigmas.add(new Enigma(
            "Nível 13 — O Chapeleiro pergunta pela última vez: \"Por que um corvo se parece com uma escrivaninha?\" "
            + "Diga apenas o nome do objeto que ele comparou ao corvo.",
            "escrivaninha", true,
            "A resposta está na própria pergunta do Chapeleiro."));

        enigmas.add(new Enigma(
            "Nível 14 — O que você deve fazer para fazer este mundo de pesadelos sumir e as cartas virarem folhas secas?",
            "Acordar", false,
            "É o oposto de sonhar — a única forma de sair de um pesadelo."));

        enigmas.add(new Enigma(
            "Nível 15 (CONFRONTO FINAL) — Eu devoro impérios e transformo sonhos de infância em pó. Qual o meu nome mais cruel?",
            "Esquecimento", false,
            "É o que acontece quando ninguém mais se lembra de você — pior que a própria morte."));
    }

    /**
     * Retorna o enigma do nível atual (1..15).
     */
    public Enigma getEnigmaAtual() {
        int idx = nivelAtual - 1;
        if (idx >= 0 && idx < enigmas.size()) {
            return enigmas.get(idx);
        }
        return null;
    }

    /** Fase de dificuldade atual: 1 (níveis 1-5), 2 (6-10) ou 3 (11-15). */
    public int getFaseAtual() {
        return ((nivelAtual - 1) / TAMANHO_FASE) + 1;
    }

    /** Tempo (em segundos) de resposta de acordo com a fase atual. */
    public int getTempoFaseAtual() {
        switch (getFaseAtual()) {
            case 1: return 40;
            case 2: return 30;
            default: return 15;
        }
    }

    /** Quantidade de dicas disponíveis por pergunta, de acordo com a fase atual. */
    public int getDicasDisponiveisFaseAtual() {
        switch (getFaseAtual()) {
            case 1: return 0;
            case 2: return 1;
            default: return 2;
        }
    }

    /** Nível em que a fase atual começa (1, 6 ou 11). */
    public int getInicioDaFaseAtual() {
        return ((getFaseAtual() - 1) * TAMANHO_FASE) + 1;
    }

    /**
     * Reseta para o início da FASE em que o jogador está (e não para o nível 1 da run),
     * mantendo a sanidade atual (já decrementada pelo chamador).
     * Se a sanidade zerou, a run inteira recomeça do nível 1 com sanidade máxima.
     */
    public void resetarRunDaDerrota() {
        if (this.sanidadeAtual <= 0) {
            this.nivelAtual = 1;
            this.sanidadeAtual = (jogadorLogado != null) ? jogadorLogado.getSanidadeMaxUpgrade() : 3;
        } else {
            this.nivelAtual = getInicioDaFaseAtual();
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
