package com.example.listasmart;

public class DiferencaPrecoProdutoModel {
    private final String nomeProduto;
    private final String mercadoMaisBarato;
    private final double menorPreco;
    private final String mercadoMaisCaro;
    private final double maiorPreco;
    private final double diferencaPreco;

    public DiferencaPrecoProdutoModel(
            String nomeProduto,
            String mercadoMaisBarato,
            double menorPreco,
            String mercadoMaisCaro,
            double maiorPreco,
            double diferencaPreco
    ) {
        this.nomeProduto = nomeProduto;
        this.mercadoMaisBarato = mercadoMaisBarato;
        this.menorPreco = menorPreco;
        this.mercadoMaisCaro = mercadoMaisCaro;
        this.maiorPreco = maiorPreco;
        this.diferencaPreco = diferencaPreco;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getMercadoMaisBarato() {
        return mercadoMaisBarato;
    }

    public double getMenorPreco() {
        return menorPreco;
    }

    public String getMercadoMaisCaro() {
        return mercadoMaisCaro;
    }

    public double getMaiorPreco() {
        return maiorPreco;
    }

    public double getDiferencaPreco() {
        return diferencaPreco;
    }
}