package com.example.listasmart;

public class ResultadoComparacaoMercadoModel {
    private final String nomeMercado;
    private final double totalLista;
    private final double percentualAcimaMaisBarato;

    public ResultadoComparacaoMercadoModel(String nomeMercado, double totalLista, double percentualAcimaMaisBarato) {
        this.nomeMercado = nomeMercado;
        this.totalLista = totalLista;
        this.percentualAcimaMaisBarato = percentualAcimaMaisBarato;
    }

    public String getNomeMercado() {
        return nomeMercado;
    }

    public double getTotalLista() {
        return totalLista;
    }

    public double getPercentualAcimaMaisBarato() {
        return percentualAcimaMaisBarato;
    }
}