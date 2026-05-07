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
public class Produto  implements IEntity{
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

    // Campos simples (mapeados automaticamente como colunas)
    private LocalDateTime dataCadastro;
    // Campo para armazenar data e hora do cadastro
    @PrePersist
    // Método executado automaticamente ANTES de salvar no banco de dados
    public void prePersist(){
        this.dataCadastro = LocalDateTime.now();
    }
}