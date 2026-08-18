# 🔄 Diagramas de Sequência (Workflows Técnicos e Mensageria)

Este diretório contém os **Diagramas de Sequência UML** (em formato PlantUML `.puml`), detalhando as chamadas de métodos, transações `UnitOfWork`, controladores Spring MVC, repositórios genéricos e interações com o banco de dados MySQL (`gwj5`).

---

## 📂 Índice de Diagramas de Sequência

| Arquivo | Título / Escopo | Componentes Envolvidos |
| :--- | :--- | :--- |
| [`sequenceDiagramBooking.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramBooking.puml) | **Agendamento de Horários pelo Cliente** | `AgendaController`, `AgendamentoService`, `UnitOfWork`, `GenericRepository`, `DataMapper`, `MySQL` |
| [`sequenceDiagramEcommerce.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramEcommerce.puml) | **Compra de Cosméticos e Checkout (Loja)** | `CarrinhoController`, `HttpSession`, `GenericService<Pedido>`, `GenericRepository`, `MySQL` |
| [`sequenceDiagramAuthSecurity.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramAuthSecurity.puml) | **Autenticação, Sessão e Interceptor** | `LoginController`, `PasswordUtil`, `AdminInterceptor`, `UsuarioService`, `MySQL` |
| [`sequenceDiagramCancelReschedule.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramCancelReschedule.puml) | **Cancelamento e Reagendamento** | `AgendaController`, `AgendamentoService`, `UnitOfWork`, `GenericRepository`, `MySQL` |
| [`sequenceDiagramNotificationJob.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramNotificationJob.puml) | **Lembretes Automáticos (@Scheduled)** | `TaskScheduler`, `NotificationService`, `AgendamentoService`, `WhatsApp/Email Gateway`, `MySQL` |
| [`sequenceDiagramAdminSchedule.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramAdminSchedule.puml) | **Painel Administrativo Dinâmico (CRUD)** | `GenericViewController`, `JsonController`, `SimpleObjectFactory`, `EntityMapper`, `GenericRepository` |

---

## 🔍 Detalhamento dos Fluxos

### 1. 📅 [Agendamento Online (`sequenceDiagramBooking.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramBooking.puml)
* **Passo 1:** Consulta de horários disponíveis cruzando duração do serviço, limites de funcionamento e slots livres.
* **Passo 2:** Confirmação da reserva com checagem de sobreposição de horários e persistência atômica via `UnitOfWork`.

### 2. 🛍️ [Compra de Produtos e Checkout (`sequenceDiagramEcommerce.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramEcommerce.puml)
* **Adição ao Carrinho:** Manipulação da sessão `HttpSession` com `Carrinho` e `CarrinhoItem`.
* **Checkout:** Criação do `Pedido`, gravação dos `ItemPedido`, cálculo do valor total e baixa de estoque em `tab_produto`.

### 3. 🔐 [Autenticação e Interceptor (`sequenceDiagramAuthSecurity.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramAuthSecurity.puml)
* **Login:** Criptografia de senha via `PasswordUtil.hash` (SHA-256) e controle de sessão.
* **AdminInterceptor:** Validação de acesso a rotas administrativas (`/MRYnZpAsC9sp/*`) e autorizações granulares (`GERENCIAR_CLIENTES`, `GERENCIAR_SERVICOS`, etc.).

### 4. 🔄 [Cancelamento e Reagendamento (`sequenceDiagramCancelReschedule.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramCancelReschedule.puml)
* **Cancelamento:** Verificação de prazo mínimo de antecedência e atualização para status `Cancelado`.
* **Reagendamento:** Verificação de conflito no novo horário e atualização dos campos de data/hora no agendamento.

### 5. ⏰ [Lembretes Automáticos (`sequenceDiagramNotificationJob.puml`)](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramNotificationJob.puml)
* **Rotina Daemon:** Disparo periódico via Spring TaskScheduler.
* **Envio:** Consulta agendamentos confirmados das próximas 24h e envia mensagem via gateway com atualização da flag `lembrete_enviado`.
