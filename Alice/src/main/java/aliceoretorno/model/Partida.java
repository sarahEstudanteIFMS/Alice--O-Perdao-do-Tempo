package aliceoretorno.model;

import java.util.ArrayList;
import java.util.List;

public class Partida {
    // Estado estático global para a sessão
    public static Jogador jogadorLogado;
    public static Partida runAtual;
    

    private int nivelAtual;
    private int sanidadeAtual;
    private int engrenagensGanhasNaRun;
    private int tempoExtraItens; // Unidades de item acumuladas
    private final List<Enigma> enigmas = new ArrayList<>();

    public Partida() {
        this.nivelAtual = 1;
        this.sanidadeAtual = (jogadorLogado != null) ? jogadorLogado.getSanidadeMaxUpgrade() : 3;
        this.engrenagensGanhasNaRun = 0;
        this.tempoExtraItens = 0;
        inicializarEnigmas();
    }

    private void inicializarEnigmas() {
        enigmas.add(new Enigma("Qual a cor do céu?", "depende", true));
        enigmas.add(new Enigma("O coelho atrasou 20s no banho, 35s no café, 1m34s para sair e 56s no caminho. Qual o atraso total no formato XmXs?", "3m25s", false));
        enigmas.add(new Enigma("Você cresce e encolhe, mas seu nome não muda desde que caiu na toca. Quem é você?", "Alice", false));
        enigmas.add(new Enigma("O que você deve fazer para fazer este mundo de pesadelos sumir e as cartas virarem folhas secas?", "Acordar", false));
        enigmas.add(new Enigma("Eu devoro impérios e transformo sonhos de infância em pó. Qual o meu nome mais cruel?", "Esquecimento", false));
    }

    public Enigma getEnigmaAtual() {
        if (nivelAtual <= enigmas.size()) {
            return enigmas.get(nivelAtual - 1);
        }
        return null;
    }

    public void resetarRunDaDerrota() {
        this.nivelAtual = 1;
        this.sanidadeAtual = (jogadorLogado != null) ? jogadorLogado.getSanidadeMaxUpgrade() : 3;
    }

    // Getters e Setters
    public int getNivelAtual() { return nivelAtual; }
    public void setNivelAtual(int nivelAtual) { this.nivelAtual = nivelAtual; }
    public int getSanidadeAtual() { return sanidadeAtual; }
    public void setSanidadeAtual(int sanidadeAtual) { this.sanidadeAtual = sanidadeAtual; }
    public int getEngrenagensGanhasNaRun() { return engrenagensGanhasNaRun; }
    public void somarEngrenagens(int qtd) { this.engrenagensGanhasNaRun += qtd; }
    public int getTempoExtraItens() { return tempoExtraItens; }
    public void setTempoExtraItens(int tempoExtraItens) { this.tempoExtraItens = tempoExtraItens; }
}