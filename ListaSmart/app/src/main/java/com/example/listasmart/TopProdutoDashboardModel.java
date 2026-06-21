package com.example.listasmart;

public class TopProdutoDashboardModel {
    private final String nomeProduto;
    private final int quantidadeRegistros;

    public TopProdutoDashboardModel(String nomeProduto, int quantidadeRegistros) {
        this.nomeProduto = nomeProduto;
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getQuantidadeRegistros() {
        return quantidadeRegistros;
    }
}