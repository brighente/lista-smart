package com.example.listasmart;

public class DiaForteBuscaModel {
    private final String nomeDia;
    private final int totalBuscas;

    public DiaForteBuscaModel(String nomeDia, int totalBuscas) {
        this.nomeDia = nomeDia;
        this.totalBuscas = totalBuscas;
    }

    public String getNomeDia() {
        return nomeDia;
    }

    public int getTotalBuscas() {
        return totalBuscas;
    }
}