package com.example.listasmart;

public class HistoricoPrecoModel {
    private final String data;
    private final String precoMedio;
    private final double precoMedioValor;
    private final int quantidadeProdutos;

    public HistoricoPrecoModel(String data, String precoMedio, double precoMedioValor, int quantidadeProdutos) {
        this.data = data;
        this.precoMedio = precoMedio;
        this.precoMedioValor = precoMedioValor;
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public String getData() {
        return data;
    }

    public String getPrecoMedio() {
        return precoMedio;
    }

    public double getPrecoMedioValor() {
        return precoMedioValor;
    }

    public int getQuantidadeProdutos() {
        return quantidadeProdutos;
    }
}