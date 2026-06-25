package com.example.listasmart;

public class OportunidadePrecoProdutoModel {
    private final String nomeProduto;
    private final String nomeCategoria;
    private final String mercadoReferencia;
    private final int totalBuscas;
    private final int mercadosComparaveis;
    private final int mercadosNoMenorPreco;
    private final Double precoMercado;
    private final Double menorPreco;
    private final boolean mercadoTemPreco;
    private final boolean possuiBaseComparavel;
    private final boolean mercadoVence;
    private final boolean empateNoMenorPreco;

    public OportunidadePrecoProdutoModel(
            String nomeProduto,
            String nomeCategoria,
            String mercadoReferencia,
            int totalBuscas,
            int mercadosComparaveis,
            int mercadosNoMenorPreco,
            Double precoMercado,
            Double menorPreco,
            boolean mercadoTemPreco,
            boolean possuiBaseComparavel,
            boolean mercadoVence,
            boolean empateNoMenorPreco
    ) {
        this.nomeProduto = nomeProduto;
        this.nomeCategoria = nomeCategoria;
        this.mercadoReferencia = mercadoReferencia;
        this.totalBuscas = totalBuscas;
        this.mercadosComparaveis = mercadosComparaveis;
        this.mercadosNoMenorPreco = mercadosNoMenorPreco;
        this.precoMercado = precoMercado;
        this.menorPreco = menorPreco;
        this.mercadoTemPreco = mercadoTemPreco;
        this.possuiBaseComparavel = possuiBaseComparavel;
        this.mercadoVence = mercadoVence;
        this.empateNoMenorPreco = empateNoMenorPreco;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public String getMercadoReferencia() {
        return mercadoReferencia;
    }

    public int getTotalBuscas() {
        return totalBuscas;
    }

    public int getMercadosComparaveis() {
        return mercadosComparaveis;
    }

    public int getMercadosNoMenorPreco() {
        return mercadosNoMenorPreco;
    }

    public Double getPrecoMercado() {
        return precoMercado;
    }

    public Double getMenorPreco() {
        return menorPreco;
    }

    public boolean isMercadoTemPreco() {
        return mercadoTemPreco;
    }

    public boolean isPossuiBaseComparavel() {
        return possuiBaseComparavel;
    }

    public boolean isMercadoVence() {
        return mercadoVence;
    }

    public boolean isEmpateNoMenorPreco() {
        return empateNoMenorPreco;
    }

    public double getDiferencaParaLider() {
        if (precoMercado == null || menorPreco == null) {
            return 0.0;
        }

        return precoMercado - menorPreco;
    }

    public String getStatusLabel() {
        if (!mercadoTemPreco) {
            return "Sem preço";
        }

        if (!possuiBaseComparavel) {
            return "Sem base";
        }

        if (mercadoVence && empateNoMenorPreco) {
            return "Empatado";
        }

        if (mercadoVence) {
            return "Menor preço";
        }

        return "Oportunidade";
    }
}
