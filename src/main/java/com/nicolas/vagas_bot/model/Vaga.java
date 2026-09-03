package com.nicolas.vagas_bot.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

@Entity //Diz ao Spring/JPA: "essa classe vira uma tabela no banco"
@Table(name = "vagas")

public class Vaga {
    @Id //Marca o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) //O Postgres gera o valor automaticamente
    private Long id;

    @Column(nullable = false, unique = true) //Unique = base da deduplicação por URL
    private String url;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String empresa;

    @Enumerated(EnumType.STRING) //Salva o NOME do enum ("FRONTEND"), não um número
    private AreaAtuacao areaAtuacao;

    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    private String localizacao;

    private LocalDate dataPublicacao; //Base do filtro "não pode ter mais de 1 mês"

    private LocalDateTime dataEncontrada; //Base do resumo diário

    public Vaga() {} //Construtor vazio exigido pelo JPA

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void stUrl(String url) {
        this.url = url;
    }

    public void setTítulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public AreaAtuacao getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(AreaAtuacao areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(Modalidade modalidade){
        this.modalidade = modalidade;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public LocalDateTime getDataEncontrada() {
        return dataEncontrada;
    }

    public void setDataEncontrada(LocalDateTime dataEncontrada) {
        this.dataEncontrada = dataEncontrada;
    }
} 
