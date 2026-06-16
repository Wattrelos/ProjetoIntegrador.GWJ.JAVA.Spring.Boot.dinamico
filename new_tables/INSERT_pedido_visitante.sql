-- 1. Cria o pedido do visitante
INSERT INTO tab_pedidos (
        cliente_id,
        nome_visitante,
        telefone_visitante,
        valor_total
    )
VALUES (NULL, 'Carlos Silva', '(11) 99999-9999', 32.00);
-- 2. Insere o item (considerando que o pedido gerado foi o ID 1)
INSERT INTO tab_itens_pedido (
        pedido_id,
        produto_id,
        quantidade,
        preco_unitario
    )
VALUES (1, 2, 1, 32.00);