# 📋 Documento de Requisito Funcional

## Identificação
* **Código:** RF006
* **Título:** E-Commerce de Cosméticos Masculinos e Controle de Estoque
* **Módulo:** Loja & Estoque
* **Prioridade:** Média-Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve fornecer um módulo de compras online (E-Commerce) integrado para venda de cosméticos capilares e faciais, kits de barbearia e produtos de cuidados masculinos. O fluxo inclui gestão de carrinho em sessão (`HttpSession`), cálculo automático de frete/retirada presencial, checkout com identificação de clientes ou visitantes e baixa atômica no estoque físico (`tab_produto.estoque`).

---

## 2. Atores Envolvidos
* **Cliente / Visitante:** Navega, manipula itens no carrinho e conclui o pedido de compra.
* **Administrador / Almoxarifado:** Gerencia entradas de novos lotes e monitora alertas de estoque mínimo.
* **Sistema (CarrinhoController / GenericService<Pedido>):** Orquestra o cálculo dos totais e a persistência atômica da venda.

---

## 3. Entradas e Saídas

### 3.1 Entradas
* Requisições AJAX de carrinho: `produtoId`, `quantidade` (adicionar, atualizar quantidade, remover item, limpar).
* Dados de Checkout: `nomeVisitante`, `telefoneVisitante`, `formaPagamento` (PIX, Dinheiro na Retirada, Cartão).

### 3.2 Saídas
* Respostas JSON assíncronas com contagem total de itens e valor total do carrinho.
* Criação do registro mestre na tabela `tab_pedidos` e dos itens na tabela `tab_itens_pedido`.
* Redirecionamento para a página `/compra-confirmada?id={pedidoId}`.

---

## 4. Regras de Negócio (RN)

1. **RN01 - Baixa Atômica de Estoque:** No momento da confirmação do pedido, a transação deve decrementar o saldo em `tab_produto.estoque`. Se algum item não tiver saldo suficiente, a transação inteira deve sofrer Rollback.
2. **RN02 - Persistência de Visitantes e Clientes Logados:** Se o usuário estiver autenticado na sessão (`usuarioLogado`), o pedido deve ser vinculado ao seu ID na tabela `tab_cliente`. Caso contrário, o pedido é salvo como visitante utilizando os campos `nome_visitante` e `telefone_visitante`.
3. **RN03 - Limpeza da Sessão Pós-Compra:** Após o commit com sucesso da transação no banco de dados, o objeto `Carrinho` armazenado na sessão HTTP deve ser completamente limpo.

---

## 5. Critérios de Aceitação (BDD / Definition of Done)

### **Cenário 1: Compra Concluída com Sucesso e Baixa de Estoque**
* **Dado** que o produto "Pomada Modeladora Efeito Matte" possui 10 unidades em estoque;
* **E** o cliente adicionou 2 unidades ao seu carrinho de compras;
* **Quando** o cliente finalizar o checkout e submeter o pedido;
* **Então** deve ser criado um novo pedido em `tab_pedidos` com valor correspondente;
* **E** devem ser inseridos 2 itens em `tab_itens_pedido`;
* **E** o estoque da "Pomada Modeladora" em `tab_produto` deve ser atualizado para 8 unidades.

---

## 6. Mapeamento no Banco de Dados (`gwj5`)
* **Tabela `tab_produto`:** `id`, `nome`, `preco`, `estoque`, `imagem`.
* **Tabela `tab_pedidos`:** `id`, `cliente_id`, `data_pedido`, `valor_total`, `forma_pagamento`, `status`, `nome_visitante`, `telefone_visitante`.
* **Tabela `tab_itens_pedido`:** `id`, `pedido_id`, `produto_id`, `quantidade`, `preco_unitario`.

---

## 7. Classes e Componentes Relacionados
* **Controlador:** [`CarrinhoController.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/CarrinhoController.java)
* **Modelos:** [`Carrinho.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/Carrinho.java), [`CarrinhoItem.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/CarrinhoItem.java), [`Pedido.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Pedido.java), [`ItemPedido.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/ItemPedido.java)
* **Templates:** `shop.html`, `carrinho.html`, `carrinho-checkout.html`, `compra-confirmada.html`
