package com.example.listasmart;

public class ResumoInteressePeriodoModel {
    private final String produtoMaisBuscado;
    private final String categoriaMaisBuscada;
    private final String diaMaisForte;
    private final int totalBuscas;

    public ResumoInteressePeriodoModel(
            String produtoMaisBuscado,
            String categoriaMaisBuscada,
            String diaMaisForte,
            int totalBuscas
    ) {
        this.produtoMaisBuscado = produtoMaisBuscado;
        this.categoriaMaisBuscada = categoriaMaisBuscada;
        this.diaMaisForte = diaMaisForte;
        this.totalBuscas = totalBuscas;
    }

    public String getProdutoMaisBuscado() {
        return produtoMaisBuscado;
    }

    public String getCategoriaMaisBuscada() {
        return categoriaMaisBuscada;
    }

    public String getDiaMaisForte() {
        return diaMaisForte;
    }

    public int getTotalBuscas() {
        return totalBuscas;
    }
}