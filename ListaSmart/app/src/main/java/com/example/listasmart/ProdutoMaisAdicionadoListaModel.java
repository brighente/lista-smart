package com.example.listasmart;

public class ProdutoMaisAdicionadoListaModel {
    private final String nomeProduto;
    private final String nomeCategoria;
    private final int totalAdicoes;
    private final int totalListas;

    public ProdutoMaisAdicionadoListaModel(
            String nomeProduto,
            String nomeCategoria,
            int totalAdicoes,
            int totalListas
    ) {
        this.nomeProduto = nomeProduto;
        this.nomeCategoria = nomeCategoria;
        this.totalAdicoes = totalAdicoes;
        this.totalListas = totalListas;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public int getTotalAdicoes() {
        return totalAdicoes;
    }

    public int getTotalListas() {
        return totalListas;
    }
}