# 🔄 Diagramas de Atividades (Workflows de Negócio da Barbearia)

Este diretório contém os **Diagramas de Atividades UML** (no formato PlantUML `.puml`) que modelam os fluxos operacionais, regras de negócio, concorrência e transações da **Tgo's Barbearia (Sistema GWJ)**.

---

## 📂 Índice de Diagramas

| Arquivo | Título / Escopo | Requisitos / User Stories | Principais Atores / Swimlanes |
| :--- | :--- | :--- | :--- |
| [`agendamento_online.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/agendamento_online.puml) | **Agendamento Online Autônomo** | [US01](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/UserStory/US01-Agendamento%20de%20Hor%C3%A1rio%20Aut%C3%B4nomo.md), RF02, RF03 | Cliente, Frontend, Backend (`AgendamentoService`), MySQL (`tab_agendamento`) |
| [`cancelamento_reagendamento.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/cancelamento_reagendamento.puml) | **Cancelamento e Reagendamento** | RF03, Permissão `CANCELAR_AGENDAMENTO` | Cliente/Recepção, Frontend, Backend, MySQL |
| [`atendimento_execucao_servico.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/atendimento_execucao_servico.puml) | **Atendimento Presencial e Caixa** | RF04, RF06, Gestão da Agenda | Barbeiro/Recepção, Painel Admin, Backend, MySQL |
| [`compra_produtos_loja.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/compra_produtos_loja.puml) | **Compra de Produtos & Cosméticos (E-Commerce)** | RF06, `tab_produto`, `tab_pedidos` | Cliente, Frontend Loja, Backend (`CarrinhoController`), MySQL |
| [`envio_lembretes_automaticos.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/envio_lembretes_automaticos.puml) | **Módulo de Lembretes Automáticos** | RF05, `@Scheduled` Daemon | Scheduler, Backend, Gateway WhatsApp/E-mail, MySQL, Cliente |

---

## 🔍 Detalhamento dos Fluxos

### 1. 📅 [Agendamento Online Autônomo (`agendamento_online.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/agendamento_online.puml)
* **Objetivo:** Permitir ao cliente reservar um horário de forma 100% autônoma.
* **Pontos Chave:**
  * Cálculo dinâmico de blocos contínuos baseado em `tab_servico.duracao`.
  * Validação de limite de expediente (`tab_dias_funcionamento.horario_fim`).
  * Bloqueio transacional atômico no `AgendamentoService` com `UnitOfWork` para prevenir **double-booking**.

### 2. 🚫 [Cancelamento e Reagendamento (`cancelamento_reagendamento.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/cancelamento_reagendamento.puml)
* **Objetivo:** Gestão de desistências e alterações de agenda sem perda de consistência.
* **Pontos Chave:**
  * Regra de antecedência mínima tolerada.
  * Liberação imediata dos slots ocupados para reuso por outros clientes.

### 3. ✂️ [Atendimento Presencial e Execução (`atendimento_execucao_servico.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/atendimento_execucao_servico.puml)
* **Objetivo:** Rotina operacional do profissional na barbearia, desde a recepção/check-in do cliente até o fechamento da comanda e pagamento.
* **Pontos Chave:**
  * Inclusão de serviços e produtos adicionais durante o atendimento.
  * Baixa em estoque físico e cálculo da comissão do profissional.

### 4. 🛍️ [Compra de Produtos na Loja (`compra_produtos_loja.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/compra_produtos_loja.puml)
* **Objetivo:** Compra de cosméticos masculinos e kits pelo cliente.
* **Pontos Chave:**
  * Controle de sessão do carrinho (`Carrinho`, `CarrinhoItem`).
  * Validação de estoque com lock atômico durante o checkout.

### 5. ⏰ [Disparo de Lembretes Automáticos (`envio_lembretes_automaticos.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/envio_lembretes_automaticos.puml)
* **Objetivo:** Redução de *No-Shows* (faltas) através de robô em background.
* **Pontos Chave:**
  * Varredura cron programada (`@Scheduled`).
  * Envio de links rápidos de confirmação ou cancelamento via WhatsApp e e-mail.
