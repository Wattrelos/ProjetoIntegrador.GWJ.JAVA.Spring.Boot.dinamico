# 📋 Documento de Requisito Funcional

## Identificação
* **Código:** RF004
* **Título:** Autenticação de Usuários e Controle Granular de Permissões
* **Módulo:** Segurança & Acesso
* **Prioridade:** Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve fornecer um mecanismo seguro de cadastro, autenticação (login/logout) e autorização baseada em papéis (RBAC - Role-Based Access Control) e permissões granulares. O acesso ao painel administrativo e a rotas sensíveis é protegido por um interceptador HTTP (`AdminInterceptor`), garantindo que clientes, barbeiros, recepcionistas e administradores acessem somente os módulos aos quais possuem autorização formal.

---

## 2. Atores Envolvidos
* **Cliente (Perfil 4):** Possui acesso à área pública, histórico de agendamentos e checkout de compras.
* **Barbeiro (Perfil 2):** Possui permissão para consultar sua própria agenda (`VISUALIZAR_PROPRIA_AGENDA`).
* **Recepcionista (Perfil 3):** Possui permissão para gerenciar a agenda geral e clientes (`GERENCIAR_TODAS_AGENDAS`, `GERENCIAR_CLIENTES`).
* **Administrador (Perfil 1):** Possui acesso irrestrito a todos os módulos, relatórios, configurações e permissões.

---

## 3. Entradas e Saídas

### 3.1 Entradas
* Formulário de Login: `email` e `senha`.
* Formulário de Cadastro de Cliente: `nome`, `sobrenome`, `email`, `telefone`, `senha`.

### 3.2 Saídas
* Criação de sessão `HttpSession` com o objeto `Usuario` autenticado.
* Redirecionamento condicional:
  * Clientes -> Redirecionados para `/` (Home da Barbearia).
  * Administradores/Equipe -> Redirecionados para `/MRYnZpAsC9sp` (Painel Administrativo).
* Bloqueio com HTTP Redirect e Flash Message caso o usuário tente acessar uma rota sem a permissão devida.

---

## 4. Regras de Negócio (RN)

1. **RN01 - Criptografia de Senhas:** Nenhuma senha de usuário pode ser armazenada em texto puro no banco de dados. Todas as senhas devem ser hasheadas com prefixo `{sha256}` através do utilitário `PasswordUtil`.
2. **RN02 - Unicidade de E-mail:** O sistema não deve permitir o cadastro de mais de um usuário ou cliente com o mesmo endereço de e-mail.
3. **RN03 - Isolamento de Clientes no Painel Administrativo:** Clientes comuns (Perfil ID 4) são estritamente impedidos de acessar qualquer rota sob o prefixo `/MRYnZpAsC9sp/*`.
4. **RN04 - Mapeamento Granular de Entidades:** O `AdminInterceptor` valida dinamicamente a permissão necessária para cada entidade requisitada nas rotas de CRUD:
   * `/MRYnZpAsC9sp/listar/Cliente` -> Requer permissão `GERENCIAR_CLIENTES`.
   * `/MRYnZpAsC9sp/listar/Servico` -> Requer permissão `GERENCIAR_SERVICOS`.
   * `/MRYnZpAsC9sp/listar/Agenda` -> Requer permissão `AGENDAR_HORARIO` ou `GERENCIAR_TODAS_AGENDAS`.

---

## 5. Critérios de Aceitação (BDD / Definition of Done)

### **Cenário 1: Autenticação de Administrador com Sucesso**
* **Dado** que o administrador informa seu e-mail e senha corretos na tela de login;
* **Quando** o formulário de login for submetido;
* **Então** a senha digitada deve ser convertida para hash SHA-256 e comparada com o banco de dados;
* **E** o usuário deve ser armazenado na sessão (`session.setAttribute("usuarioLogado", usuario)`);
* **E** o sistema deve redirecionar o navegador para a rota `/MRYnZpAsC9sp`.

### **Cenário 2: Acesso a Recurso Sem Permissão (Bloqueio pelo Interceptor)**
* **Dado** que um usuário logado com o perfil de "Barbeiro" (sem a permissão `GERENCIAR_SERVICOS`) tenta acessar `/MRYnZpAsC9sp/listar/Servico`;
* **Quando** a requisição for processada pelo `AdminInterceptor`;
* **Então** o método `preHandle` deve retornar `false`;
* **E** o usuário deve ser redirecionado para `/MRYnZpAsC9sp` com a mensagem flash: *"Acesso Negado: Você não possui permissão para gerenciar Servico."*

---

## 6. Mapeamento no Banco de Dados (`gwj5`)
* **Tabela `tab_usuario`:** `id`, `nome_usuario`, `email`, `senha`, `perfil_id`, `status`.
* **Tabela `tab_perfil`:** `id`, `nome` (ex: Administrador, Barbeiro, Recepcionista, Cliente).
* **Tabela `permissoes`:** `id`, `nome`, `descricao`.
* **Tabela `tab_perfil_permissao`:** `perfil_id`, `permissao_id`.

---

## 7. Classes e Componentes Relacionados
* **Controladores & Interceptadores:** [`LoginController.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/LoginController.java), [`AdminInterceptor.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AdminInterceptor.java)
* **Utilitário:** [`PasswordUtil.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/PasswordUtil.java)
* **Entidades:** [`Usuario.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Usuario.java), [`Perfil.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Perfil.java), [`Permissao.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Permissao.java)
