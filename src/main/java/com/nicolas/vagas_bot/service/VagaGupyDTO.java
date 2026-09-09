package com.nicolas.vagas_bot.service;

public class VagaGupyDTO {
    
    private String titulo;
    private String localizacao;
    private String tipo;
    private String modalidade;
    private String link;

    //Getters e setters necessários para o Spring conseguir transformar isso em JSON 
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
