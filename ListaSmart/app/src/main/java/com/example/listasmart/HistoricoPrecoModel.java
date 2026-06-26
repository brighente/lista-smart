package com.example.listasmart;

public class HistoricoPrecoModel {
    private final String data;
    private final String destaque;
    private final double destaqueValor;
    private final String resumo;

    public HistoricoPrecoModel(String data, String destaque, double destaqueValor, String resumo) {
        this.data = data;
        this.destaque = destaque;
        this.destaqueValor = destaqueValor;
        this.resumo = resumo;
    }

    public String getData() {
        return data;
    }

    public String getDestaque() {
        return destaque;
    }

    public double getDestaqueValor() {
        return destaqueValor;
    }

    public String getResumo() {
        return resumo;
    }
}
