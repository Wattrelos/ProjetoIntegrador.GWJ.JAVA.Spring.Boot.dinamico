# 🏢 Regras de Negócio e Domínio da Barbearia (GWJ)

Este diretório centraliza a documentação das **Regras de Negócio (RN)**, **Políticas Operacionais** e o **Glossário de Domínio** da **Tgo's Barbearia (Sistema GWJ)**.

---

## 📂 Índice de Documentos de Negócio

| Documento | Escopo / Módulo | Principais Regras Especificadas |
| :--- | :--- | :--- |
| [`glossary.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/glossary.md) | **Glossário Ubíquo do Domínio** | Definições formais de Agendamento, Slot de 20 min, Blocos Consecutivos, Double-Booking, No-Show, Comandas e RBAC. |
| [`regras_negocio_agendamento.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_agendamento.md) | **Regras de Agendamento (RN-AGE)** | Múltiplos blocos consecutivos por duração de serviço, restrição de horário de fechamento, bloqueio de horários no passado e prevenção de double-booking. |
| [`regras_negocio_cancelamento_reagendamento.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_cancelamento_reagendamento.md) | **Regras de Cancelamento & Reagendamento (RN-CAN)** | Antecedência mínima de 2 horas para cancelamento online, liberação instantânea de slots e regras de remarcação. |
| [`regras_negocio_atendimento_comandas.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_atendimento_comandas.md) | **Regras de Atendimento & Comandas (RN-ATE)** | Ciclo de vida do atendimento, check-in, inclusão de serviços/produtos extras na comanda e apuração de comissões. |
| [`regras_negocio_loja_estoque.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_loja_estoque.md) | **Regras de Loja & Estoque (RN-EST)** | Trava atômica de estoque, composição de kits promocionais, checkout para clientes cadastrados vs visitantes e limpeza de sessão. |
| [`regras_negocio_seguranca_perfis.md`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_seguranca_perfis.md) | **Regras de Segurança & Perfis (RN-SEG)** | Hashing SHA-256 de senhas, bloqueio de clientes no painel administrativo e autorização granular por entidade via `AdminInterceptor`. |
