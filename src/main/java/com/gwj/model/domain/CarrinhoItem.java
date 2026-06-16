package com.gwj.model.domain;

import com.gwj.model.domain.entities.Produto;
import java.math.BigDecimal;
import java.io.Serializable;

public class CarrinhoItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Produto produto;
    private int quantidade;

    public CarrinhoItem() {}

    public CarrinhoItem(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        if (produto == null || produto.getPreco() == null) {
            return BigDecimal.ZERO;
        }
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }
}
