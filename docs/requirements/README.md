# 📋 Especificação de Requisitos do Sistema (GWJ - Barbearia)

Este diretório contém as especificações formais de **Requisitos Funcionais (RF)** e **Requisitos Não-Funcionais (RNF)** do **Sistema GWJ para Tgo's Barbearia**.

---

## 🎯 1. Requisitos Funcionais (RF)

Localizados em [`docs/requirements/functional/`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/):

| Código | Título | Módulo | Prioridade |
| :--- | :--- | :--- | :--- |
| [`RF001_kits.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/RF001_kits.md) | **Catálogo de Kits Promocionais, Serviços e Produtos** | Loja & Serviços | Alta |
| [`RF002_grade_horarios_disponibilidade.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/RF002_grade_horarios_disponibilidade.md) | **Motor de Busca e Cálculo da Grade de Horários Livres** | Agenda & Horários | Crítica |
| [`RF003_agendamento_online_autonomo.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/RF003_agendamento_online_autonomo.md) | **Agendamento Online Autônomo e Prevenção de Concorrência** | Agendamento & Checkout | Crítica |
| [`RF004_autenticacao_e_permissoes.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/RF004_autenticacao_e_permissoes.md) | **Autenticação de Usuários e Controle Granular de Permissões** | Segurança & Acesso | Alta |
| [`RF005_lembretes_automaticos.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/RF005_lembretes_automaticos.md) | **Módulo de Lembretes Automáticos e Notificações** | Background & Mensageria | Média-Alta |
| [`RF006_ecommerce_e_controle_estoque.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/functional/RF006_ecommerce_e_controle_estoque.md) | **E-Commerce de Cosméticos Masculinos e Controle de Estoque** | Loja & Estoque | Média-Alta |

---

## ⚡ 2. Requisitos Não-Funcionais (RNF)

Localizados em [`docs/requirements/non_functional/`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/non_functional/):

| Código | Título | Categoria / Foco | Prioridade |
| :--- | :--- | :--- | :--- |
| [`RNF001_performance.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/non_functional/RNF001_performance.md) | **Desempenho, Eficiência e Baixa Latência** | SLAs de resposta (< 1.2s para busca de slots) | Alta |
| [`RNF002_consistencia_e_concorrencia.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/non_functional/RNF002_consistencia_e_concorrencia.md) | **Consistência Transacional e Anti Double-Booking** | Transações ACID com `UnitOfWork` e `ThreadLocal` | Crítica |
| [`RNF003_seguranca_e_autorizacao.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/non_functional/RNF003_seguranca_e_autorizacao.md) | **Segurança, Criptografia e Interceptação** | Hashing SHA-256, PreparedStatement, `AdminInterceptor` | Alta |
| [`RNF004_usabilidade_e_responsividade.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/non_functional/RNF004_usabilidade_e_responsividade.md) | **Usabilidade, UX e Mobile-First** | CSS Vanilla, Design System Premium, Área de toque mínima | Média-Alta |
| [`RNF005_arquitetura_e_manutenibilidade.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/requirements/non_functional/RNF005_arquitetura_e_manutenibilidade.md) | **Arquitetura Desacoplada e Design Patterns** | Java 21, Spring Boot 3.2.5, DataMapper, Service Registry | Alta |
