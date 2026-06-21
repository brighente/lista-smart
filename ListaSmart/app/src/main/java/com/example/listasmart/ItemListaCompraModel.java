package com.example.listasmart;

public class ItemListaCompraModel {
    private final int idItem;
    private final int idProduto;
    private final String nomeProduto;
    private final int quantidade;
    private final double precoReferencia;

    public ItemListaCompraModel(int idItem, int idProduto, String nomeProduto, int quantidade, double precoReferencia) {
        this.idItem = idItem;
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoReferencia = precoReferencia;
    }

    public int getIdItem() {
        return idItem;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoReferencia() {
        return precoReferencia;
    }
}