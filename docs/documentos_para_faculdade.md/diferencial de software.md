# Diferenciais do aplicativo GWJ (Tgo's Barbearia)
Aplicativos e softwares tem diferenciais competitivo. O aplicativo GWJ se destaca dos concorrentes por resolver problemas que outros não resolvem. Não adianta um aplicativo ter milhares de funções se não atende o básico.

O aplicativo **GWJ (Tgo''s Barbearia)** se destaca tanto do ponto de vista **funcional/negócio** quanto do ponto de vista de **engenharia de software e arquitetura**. 

Em comparação com as soluções convencionais do mercado que variam entre a gestão manual informal (WhatsApp/papel) e plataformas SaaS comerciais fechadas (AppBarber, Trinks, Booksy, Avec) os principais diferenciais estão divididos em 4 pilares:

---
## Aqui está alguns dos diferenciais que pelo menos um dos concorrentes não oferece:
---

### 1. ⏱️ Motor Inteligente de Agendamento e Prevenção de Conflitos
A maioria dos sistemas simples agenda blocos fixos de 30 ou 60 minutos sem considerar a flexibilidade dos serviços. O motor do GWJ implementa:
* **Slots Granulares e Duração Variável:** Divide a agenda em blocos de 20 minutos e calcula dinamicamente se há janelas contínuas livres para serviços de 20, 40 ou 60 minutos através do [AgendamentoService.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java).
* **Prevenção Atômica de Double-Booking:** Validação em tempo real e controle de concorrência no banco de dados via transações ACID ([UnitOfWork.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/transaction/UnitOfWork.java)), impedindo que dois clientes reservem o mesmo slot simultaneamente.
* **Respeito ao Limite de Fechamento e Passado:** Impede automaticamente que serviços ultrapassem o horário de encerramento da barbearia (ex.: 19h) e bloqueia horários no passado.
* **Interface Reativa de Alta Usabilidade:** Os slots indisponíveis/ocupados aparecem riscados e travados na interface com feedback visual imediato no [servicos.html](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/resources/templates/servicos.html).

---

### 2. 🏪 Ecossistema Integrado (Agendamento + Comandas + Loja/Estoque + RBAC)
Enquanto muitas ferramentas do mercado focam exclusivamente na agenda ou exigem módulos pagos adicionais para vendas e controle de estoque:
* **Catálogo & E-commerce Embutido:** Permite aos clientes e recepcionistas adicionar produtos masculinos (pomadas, óleos de barba) ao carrinho, com baixa atômica de estoque na tabela `tab_produto`.
* **Controle de Acesso Granular (RBAC):** Sistema com separação estrita de papéis (*Administrador, Barbeiro, Recepcionista, Cliente*) com matriz de permissões (`tab_perfil_permissao`) protegida pelo [AdminInterceptor.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AdminInterceptor.java).
* **Gestão de Comandas e Comissões:** Suporte à apuração de consumo durante o atendimento presencial e divisão das comissões dos profissionais.

---

### 3. ⚡ Arquitetura Dinâmica e Persistência de Alta Performance (Zero-ORM Overhead)
Ao contrário das aplicações Java corporativas que utilizam *frameworks* pesados de ORM (como Hibernate/JPA) que trazem sobrecarga de memória, lentidão no arranque e problemas de consultas silenciosas (*N+1 queries*):
* **Persistência Proprietária via Data Mapper & Reflection:** Desenvolvida sobre JDBC puro (`java.sql`), [DataMapper.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataAccessObject/DataMapper.java) e [QueryBuilder.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataAccessObject/QueryBuilder.java). As consultas SQL são executadas de forma previsível e direta.
* **CRUD Dinâmico e Escalabilidade Rápida:** Graças ao [GenericViewController.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/GenericViewController.java), [EntityMapper.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataTransferObject/EntityMapper.java) e [ServiceRegistry.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/ServiceRegistry.java), qualquer nova entidade criada ganha automaticamente listagens administrativas, telas de edição e rotas REST sem necessidade de reescrever dezenas de classes e telas.
* **Segurança Ativa:** Inclui armadilhas para bots maliciosos via [HoneypotController.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/HoneypotController.java) e parametrização contra SQL Injection.

---

### 4. 💰 Modelo Comercial: Autonomia e Soberania de Dados (Sem Taxas SaaS)

| Aspecto | SaaS Comerciais (Trinks, AppBarber, etc.) | WhatsApp / Agenda Manual | Sistema GWJ Dinâmico |
| :--- | :--- | :--- | :--- |
| **Custo Recorrente** | Mensalidade fixa + taxa por agendamento/barbeiro | Custo de tempo e perdas operacionais | **Zero taxas por agendamento** (hospedagem própria e econômica) |
| **Autonomia do Cliente** | Requer baixar app de terceiros ou login genérico | Depende de resposta manual do barbeiro | **Fluxo web mobile-first em < 1 minuto** |
| **Double-Booking** | Raro, mas possível em conexões ruins | Frequente | **Zero (impossibilitado por travas ACID)** |
| **Propriedade dos Dados** | Dados dos clientes retidos na nuvem do fornecedor | Espalhados em conversas | **Totalmente do dono da barbearia** (banco local ou nuvem própria) |
| **Overhead da Aplicação** | Aplicações comerciais pesadas e genéricas | N/A | **Leve e veloz** (Spring Boot 3 + Java 21 + Vanilla CSS) |

