package com.alice.model;

public class Jogador {
    private int id;
    private String nome;
    private int moedas;
    private int nivel;
    private int xpTotal;
    private int upgradeDano;
    private int upgradeTempo;
    private int upgradeSorte;

    public Jogador() {}

    // ── Getters e Setters ────────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getMoedas() { return moedas; }
    public void setMoedas(int moedas) { this.moedas = moedas; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    public int getXpTotal() { return xpTotal; }
    public void setXpTotal(int xpTotal) { this.xpTotal = xpTotal; }

    public int getUpgradeDano() { return upgradeDano; }
    public void setUpgradeDano(int upgradeDano) { this.upgradeDano = upgradeDano; }

    public int getUpgradeTempo() { return upgradeTempo; }
    public void setUpgradeTempo(int upgradeTempo) { this.upgradeTempo = upgradeTempo; }

    public int getUpgradeSorte() { return upgradeSorte; }
    public void setUpgradeSorte(int upgradeSorte) { this.upgradeSorte = upgradeSorte; }

    // ── Lógica de progressão ─────────────────────────────────────────────────────

    public void adicionarXp(int xp) {
        this.xpTotal += xp;
        this.nivel = 1 + (xpTotal / 100); // sobe de nível a cada 100 XP
    }
}
