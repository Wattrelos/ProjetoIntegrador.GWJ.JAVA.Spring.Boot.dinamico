package com.gwj.model.domain.entities;

import java.math.BigDecimal;
import com.gwj.model.domain.IEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "tab_itens_pedido")
public class ItemPedido implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeProduto;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Integer quantidade;
    private BigDecimal precoUnitario;

    public ItemPedido() {
    }

    public ItemPedido(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.precoUnitario = (produto != null) ? produto.getPreco() : BigDecimal.ZERO; // Protege contra alterações
                                                                                       // futuras de preço no estoque
        this.nomeProduto = (produto != null) ? produto.getNome() : "";
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoTotalItem() {
        if (this.precoUnitario == null || this.quantidade == null) {
            return BigDecimal.ZERO;
        }
        return this.precoUnitario.multiply(new BigDecimal(this.quantidade));
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null && this.precoUnitario == null) {
            this.precoUnitario = produto.getPreco();
        }
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
