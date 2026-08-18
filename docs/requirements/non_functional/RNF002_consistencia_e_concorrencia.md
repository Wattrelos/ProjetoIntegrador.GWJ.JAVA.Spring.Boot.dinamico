# ⚡ Documento de Requisito Não-Funcional

## Identificação
* **Código:** RNF002
* **Categoria:** Confiabilidade, Consistência e Concorrência
* **Módulo:** Persistência / Camada de Serviços
* **Prioridade:** Crítica
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve garantir a estrita integridade transacional (propriedades ACID: Atomicidade, Consistência, Isolamento e Durabilidade) e prevenir condições de corrida (*race conditions*) em cenários de alta concorrência de agendamentos e vendas na loja. A duplicidade de reservas (*double-booking*) para um mesmo profissional no mesmo slot de horário deve ser impossibilitada pelo motor transacional.

---

## 2. Padrões de Projeto e Arquitetura Aplicados

1. **Unit of Work com `ThreadLocal`:**
   * A classe [`UnitOfWork.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/transaction/UnitOfWork.java) gerencia a conexão JDBC ativa associada à thread da requisição HTTP corrente.
   * Garante que todas as operações de leitura, validação e inserção de uma mesma operação de negócio compartilhem a mesma conexão e o mesmo contexto transacional (`setAutoCommit(false)`).
2. **Controle de Concorrência e Bloqueio de Intervalo:**
   * No `AgendamentoService`, a verificação de disponibilidade e a inserção do registro são executadas dentro do mesmo bloco transacional:
     ```sql
     SELECT COUNT(*) FROM tab_agendamento 
     WHERE profissional_id = ? AND data_agendamento = ? 
       AND status = 'Confirmado' AND hora_inicio < ? AND ? < hora_fim
     ```
   * Se o resultado for maior que zero, a transação é revertida com `connection.rollback()`.
3. **Mecanismo de Storage Engine:**
   * Todas as tabelas críticas do schema `gwj5` utilizam o motor **InnoDB** do MySQL/MariaDB, com suporte nativo a transações e chaves estrangeiras (`FOREIGN KEY`).

---

## 3. Critérios de Validação e Testes
* **Simulação de Concorrência Paralela:** Execução de requisições simultâneas disparadas em threads concorrentes para o mesmo barbeiro, serviço e horário, comprovando que apenas uma única transação obtém sucesso (HTTP 200) e as demais são rejeitadas com Rollback e mensagem explicativa.
* **Teste de Rollback em Falhas de Checkout:** Simulação de interrupção ou erro durante o checkout de pedido (após inserção do mestre mas antes da inserção dos itens), verificando que nenhum registro órfão permanece em `tab_pedidos`.
