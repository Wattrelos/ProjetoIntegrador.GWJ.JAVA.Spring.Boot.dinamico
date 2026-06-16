CREATE TABLE tab_pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NULL,
    -- NULL significa que é um visitante sem cadastro
    nome_visitante VARCHAR(100) NULL,
    -- Preenchido apenas se for visitante
    telefone_visitante VARCHAR(20) NULL,
    -- Preenchido apenas se for visitante
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(50) DEFAULT 'Pagamento na Retirada',
    status VARCHAR(50) DEFAULT 'Aguardando Retirada',
    -- Status inicial para o seu fluxo
    valor_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES tab_cliente(id) ON DELETE
    SET NULL
);