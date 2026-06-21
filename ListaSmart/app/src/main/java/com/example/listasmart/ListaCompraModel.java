package com.example.listasmart;

public class ListaCompraModel {
    private final int idLista;
    private final String nomeLista;
    private final String dataCriacao;

    public ListaCompraModel(int idLista, String nomeLista, String dataCriacao) {
        this.idLista = idLista;
        this.nomeLista = nomeLista;
        this.dataCriacao = dataCriacao;
    }

    public int getIdLista() {
        return idLista;
    }

    public String getNomeLista() {
        return nomeLista;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }
}