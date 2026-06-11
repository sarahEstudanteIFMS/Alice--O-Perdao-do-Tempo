package com.alice.model;

public class ItemInventario {
    private String nome;
    private String tipo;
    private int quantidade;

    public ItemInventario(String nome, String tipo, int quantidade) {
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
    }

    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }

    @Override
    public String toString() {
        return nome + " (" + tipo + ") x" + quantidade;
    }
}
