package com.example.listasmart;

public class MercadoAdminModel {
    private int idMercado;
    private int idUsuario;
    private String nomeMercado;
    private String endereco;
    private String nomeResponsavel;
    private String email;
    private String imagemUri;

    public MercadoAdminModel(int idMercado, int idUsuario, String nomeMercado, String endereco,
                             String nomeResponsavel, String email, String imagemUri) {
        this.idMercado = idMercado;
        this.idUsuario = idUsuario;
        this.nomeMercado = nomeMercado;
        this.endereco = endereco;
        this.nomeResponsavel = nomeResponsavel;
        this.email = email;
        this.imagemUri = imagemUri;
    }

    public int getIdMercado() {
        return idMercado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNomeMercado() {
        return nomeMercado;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public String getEmail() {
        return email;
    }

    public String getImagemUri() {
        return imagemUri;
    }
}