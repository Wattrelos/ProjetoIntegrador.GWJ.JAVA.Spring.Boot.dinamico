package com.gwj.model.domain.entities;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.gwj.model.domain.IEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tab_produto")
public class Produto implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    private BigDecimal preco;
    private Integer estoque;
    private String marca;
    private String categoria;
    private String imagem;

    // Campos simples (mapeados automaticamente como colunas)
    private LocalDateTime dataCadastro;

    // Campo para armazenar data e hora do cadastro
    @PrePersist
    // Método executado automaticamente ANTES de salvar no banco de dados
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Produto() {
    }

    public Produto(Long id, String nome, String descricao, BigDecimal preco, Integer estoque, String marca,
            String categoria) {
        this.Id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.marca = marca;
        this.categoria = categoria;
        this.imagem = imagem;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}