package com.example.listasmart;

public class TendenciaBuscaProdutoModel {
    private final int idProduto;
    private final String nomeProduto;
    private final String nomeCategoria;
    private final int buscasPeriodoAtual;
    private final int buscasPeriodoAnterior;
    private final int diferencaBuscas;
    private final boolean emAlta;
    private final boolean novoNoRadar;

    public TendenciaBuscaProdutoModel(
            int idProduto,
            String nomeProduto,
            String nomeCategoria,
            int buscasPeriodoAtual,
            int buscasPeriodoAnterior,
            int diferencaBuscas,
            boolean emAlta,
            boolean novoNoRadar
    ) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.nomeCategoria = nomeCategoria;
        this.buscasPeriodoAtual = buscasPeriodoAtual;
        this.buscasPeriodoAnterior = buscasPeriodoAnterior;
        this.diferencaBuscas = diferencaBuscas;
        this.emAlta = emAlta;
        this.novoNoRadar = novoNoRadar;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public int getBuscasPeriodoAtual() {
        return buscasPeriodoAtual;
    }

    public int getBuscasPeriodoAnterior() {
        return buscasPeriodoAnterior;
    }

    public int getDiferencaBuscas() {
        return diferencaBuscas;
    }

    public boolean isEmAlta() {
        return emAlta;
    }

    public boolean isNovoNoRadar() {
        return novoNoRadar;
    }

    public String getResumoVariacao() {
        if (novoNoRadar && buscasPeriodoAtual > 0) {
            return "Novo no radar";
        }

        if (diferencaBuscas > 0) {
            return "+" + diferencaBuscas + (diferencaBuscas == 1 ? " busca" : " buscas");
        }

        if (diferencaBuscas < 0) {
            int valorAbsoluto = Math.abs(diferencaBuscas);
            return "-" + valorAbsoluto + (valorAbsoluto == 1 ? " busca" : " buscas");
        }

        return "Estável";
    }
}