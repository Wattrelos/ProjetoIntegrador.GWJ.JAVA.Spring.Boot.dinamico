# 📜 Regras de Negócio: Segurança, Perfis e Acesso

Este documento estabelece as diretrizes e regras de negócio para gestão de identidades, perfis de usuários, permissões granulares e segurança de rotas no **Sistema GWJ**.

---

## 🎯 Lista de Regras de Negócio (RN-SEG)

### **RN-SEG-01: Hashing Criptográfico Mandatório de Senhas**
* **Contexto:** Cadastro de novos clientes, alteração de senhas e autenticação de usuários.
* **Regra:**
  1. Nenhuma senha pode ser persistida em formato legível (plain text).
  2. Todas as senhas devem ser processadas com a função de hash SHA-256 e salvas com o prefixo `{sha256}`.
  3. No login, a senha digitada é submetida ao mesmo algoritmo antes da comparação com o valor persistido.
* **Impacto no Código:** [`PasswordUtil.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/PasswordUtil.java), [`LoginController.java:L120-L132`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/LoginController.java#L120-L132).

---

### **RN-SEG-02: Bloqueio de Clientes nas Rotas Administrativas**
* **Contexto:** Tentativas de acesso ao painel ofuscado (`/MRYnZpAsC9sp/*`).
* **Regra:** Usuários cujo perfil seja classificado como "Cliente" (`perfil_id = 4`) não possuem permissão de acesso a nenhuma página, relatório ou endpoint do painel administrativo. Qualquer tentativa deve resultar em redirecionamento compulsório para a Home (`/`).
* **Impacto no Código:** [`AdminInterceptor.java:L33-L37`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AdminInterceptor.java#L33-L37).

---

### **RN-SEG-03: Autorização Granular por Entidade no CRUD Dinâmico**
* **Contexto:** Funcionários da barbearia (Barbeiros, Recepcionistas) acessando rotas de gestão de dados.
* **Regra:**
  * O Administrador Geral (`perfil_id = 1`) possui acesso irrestrito a todas as ações.
  * Para os demais colaboradores, o `AdminInterceptor` mapeia a entidade da URL e valida se o usuário possui a permissão correspondente na tabela `permissoes`:
    * Entidade `Cliente` -> Requer permissão `GERENCIAR_CLIENTES`.
    * Entidade `Servico` -> Requer permissão `GERENCIAR_SERVICOS`.
    * Entidade `Agenda` -> Requer permissão `AGENDAR_HORARIO` ou `GERENCIAR_TODAS_AGENDAS`.
* **Impacto no Código:** [`AdminInterceptor.java:L45-L73`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AdminInterceptor.java#L45-L73).

---

### **RN-SEG-04: Encerramento de Sessão (Logout)**
* **Contexto:** Saída de usuário do sistema.
* **Regra:** A requisição `/logout` deve invalidar completamente a sessão atual (`session.invalidate()`), limpando quaisquer objetos cacheados em memória (inclusive o carrinho de compras) e redirecionando o usuário para a página de login.
