package com.example.listasmart;

public class CompetitividadeMercadoModel {
    private final int posicao;
    private final int totalMercadosCompetitivos;
    private final int produtosComparaveis;
    private final int produtosVencidos;
    private final double percentualVitorias;
    private final String faixaResumo;
    private final boolean possuiBaseComparavel;

    public CompetitividadeMercadoModel(
            int posicao,
            int totalMercadosCompetitivos,
            int produtosComparaveis,
            int produtosVencidos,
            double percentualVitorias,
            String faixaResumo,
            boolean possuiBaseComparavel
    ) {
        this.posicao = posicao;
        this.totalMercadosCompetitivos = totalMercadosCompetitivos;
        this.produtosComparaveis = produtosComparaveis;
        this.produtosVencidos = produtosVencidos;
        this.percentualVitorias = percentualVitorias;
        this.faixaResumo = faixaResumo;
        this.possuiBaseComparavel = possuiBaseComparavel;
    }

    public int getPosicao() {
        return posicao;
    }

    public int getTotalMercadosCompetitivos() {
        return totalMercadosCompetitivos;
    }

    public int getProdutosComparaveis() {
        return produtosComparaveis;
    }

    public int getProdutosVencidos() {
        return produtosVencidos;
    }

    public double getPercentualVitorias() {
        return percentualVitorias;
    }

    public String getFaixaResumo() {
        return faixaResumo;
    }

    public boolean possuiBaseComparavel() {
        return possuiBaseComparavel;
    }
}
