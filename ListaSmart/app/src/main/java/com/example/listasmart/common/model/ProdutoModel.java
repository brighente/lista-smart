package com.example.listasmart;

public class ProdutoModel {
    private int idProduto;
    private String nome;
    private String imagemUri;
    private String mercado;
    private String tipoRegistro;
    private double preco;

    public ProdutoModel(int idProduto, String nome, String imagemUri, String mercado, String tipoRegistro, double preco) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.imagemUri = imagemUri;
        this.mercado = mercado;
        this.tipoRegistro = tipoRegistro;
        this.preco = preco;
    }

    public int getIdProduto() { return idProduto; }
    public String getNome() { return nome; }
    public String getImagemUri() { return imagemUri; }
    public String getMercado() { return mercado; }
    public String getTipoRegistro() { return tipoRegistro; }
    public double getPreco() { return preco; }
}