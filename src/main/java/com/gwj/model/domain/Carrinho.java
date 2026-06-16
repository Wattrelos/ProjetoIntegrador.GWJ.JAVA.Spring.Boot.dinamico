package com.gwj.model.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Long, CarrinhoItem> itens = new HashMap<>();

    public Map<Long, CarrinhoItem> getItens() {
        return itens;
    }

    public List<CarrinhoItem> getListaItens() {
        return new ArrayList<>(itens.values());
    }

    public void adicionarItem(com.gwj.model.domain.entities.Produto produto, int quantidade) {
        if (produto == null || produto.getId() == null) return;
        if (itens.containsKey(produto.getId())) {
            CarrinhoItem item = itens.get(produto.getId());
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            itens.put(produto.getId(), new CarrinhoItem(produto, quantidade));
        }
    }

    public void atualizarQuantidade(Long produtoId, int quantidade) {
        if (itens.containsKey(produtoId)) {
            if (quantidade <= 0) {
                itens.remove(produtoId);
            } else {
                itens.get(produtoId).setQuantidade(quantidade);
            }
        }
    }

    public void removerItem(Long produtoId) {
        itens.remove(produtoId);
    }

    public void limpar() {
        itens.clear();
    }

    public int getQuantidadeTotal() {
        return itens.values().stream().mapToInt(CarrinhoItem::getQuantidade).sum();
    }

    public BigDecimal getValorTotal() {
        return itens.values().stream()
                .map(CarrinhoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
