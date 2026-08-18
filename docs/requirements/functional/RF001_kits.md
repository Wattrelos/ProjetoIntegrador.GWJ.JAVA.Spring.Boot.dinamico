# 📋 Documento de Requisito Funcional

## Identificação
* **Código:** RF001
* **Título:** Catálogo de Kits Promocionais, Serviços e Produtos Cosméticos
* **Módulo:** Loja & Catálogo de Serviços
* **Prioridade:** Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve disponibilizar um catálogo dinâmico e visualmente atraente de serviços de barbearia (ex: Cortes, Barba, Combos) e produtos cosméticos masculinos (pomadas, loções, óleos e **Kits Promocionais**), permitindo ao cliente navegar, visualizar descrições, preços, durações e adicionar itens ao carrinho de compras ou selecionar para agendamento.

---

## 2. Atores Envolvidos
* **Cliente / Visitante:** Navega pelo catálogo na vitrine virtual, visualiza detalhes e adiciona ao carrinho de compras.
* **Barbeiro / Recepcionista:** Consulta o catálogo durante o atendimento presencial para oferecer kits e cosméticos adicionais.
* **Administrador:** Cadastra, edita, inativa e gerencia os preços, estoques e fotos dos serviços e kits na plataforma.

---

## 3. Entradas e Saídas

### 3.1 Entradas
* Filtros de categoria / busca por nome.
* Seleção de item no catálogo (`idProduto` ou `idServico`).
* Dados cadastrais do produto/kit (Nome, Descrição, Preço, Quantidade em Estoque, Caminho da Imagem, Categoria).

### 3.2 Saídas
* Lista de produtos/serviços ativos renderizados com foto, valor formatado em BRL e badge de disponibilidade.
* Página individual do produto/kit (`single-product.html`) com especificações completas e botão de compra.
* Mensagens de alerta caso o item esteja fora de estoque.

---

## 4. Regras de Negócio (RN)

1. **RN01 - Exibição Condicionada a Itens Ativos:** Apenas produtos (`tab_produto`) e serviços (`tab_servico`) com flag de status ativo devem ser exibidos na vitrine pública.
2. **RN02 - Validação de Estoque de Kits:** Produtos do tipo "Kit" ou cosméticos individuais com saldo de estoque igual a 0 (`tab_produto.estoque = 0`) não devem permitir a adição ao carrinho, exibindo badge visual "Esgotado".
3. **RN03 - Composição de Kits:** Kits promocionais podem agrupar múltiplos itens físicos sob um preço diferenciado (ex: *Kit Barba de Respeito* contendo Shampoo para Barba + Óleo Hidratante + Balm Modelador).
4. **RN04 - Preço Dinâmico e Formatação:** Os preços devem ser persistidos como valores decimais no banco de dados e renderizados no frontend formatados no padrão monetário brasileiro (`R$ 0,00`).

---

## 5. Critérios de Aceitação (BDD / Definition of Done)

### **Cenário 1: Visualização e Seleção de um Kit em Estoque**
* **Dado** que o cliente está navegando na página da loja (`/shop`);
* **Quando** visualiza o item "Kit Barba Premium" com estoque disponível de 5 unidades;
* **Então** o card do produto deve exibir o valor, descrição resumida, imagem e o botão "Adicionar ao Carrinho";
* **E** ao clicar em adicionar, a contagem do carrinho no cabeçalho deve ser incrementada.

### **Cenário 2: Tentativa de Compra de Kit Esgotado**
* **Dado** que o produto "Kit Cabelo & Pomada Matte" possui saldo de estoque igual a 0 no banco de dados;
* **Quando** a vitrine da loja for carregada;
* **Então** o botão de compra deve aparecer desabilitado e com a indicação "Esgotado";
* **E** qualquer requisição enviada para `/carrinho/adicionar` com esse ID deve retornar `{ "sucesso": false, "mensagem": "Produto esgotado" }`.

---

## 6. Mapeamento no Banco de Dados (`gwj5`)
* **Tabela `tab_produto`:**
  * `id` (INT, PK)
  * `nome` (VARCHAR)
  * `descricao` (TEXT)
  * `preco` (DECIMAL)
  * `estoque` (INT)
  * `imagem` (VARCHAR)
  * `categoria_id` (INT, FK)
* **Tabela `tab_servico`:**
  * `id` (INT, PK)
  * `nome` (VARCHAR)
  * `duracao` (INT - em minutos)
  * `preco` (DECIMAL)

---

## 7. Classes e Componentes Relacionados
* **Controladores:** [`Router.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/Router.java), [`CarrinhoController.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/CarrinhoController.java)
* **Entidades:** [`Produto.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Produto.java), [`Servico.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Servico.java)
* **Serviços:** `GenericService<Produto>`, `ServiceRegistry`
* **Views / Templates:** `shop.html`, `single-product.html`, `servicos.html`
