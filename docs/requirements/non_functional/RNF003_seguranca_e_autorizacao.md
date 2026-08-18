# ⚡ Documento de Requisito Não-Funcional

## Identificação
* **Código:** RNF003
* **Categoria:** Segurança, Criptografia e Autorização
* **Módulo:** Segurança / Acesso Web
* **Prioridade:** Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
A plataforma web deve implementar práticas consolidadas de segurança da informação (OWASP Top 10), assegurando o sigilo das credenciais de acesso, a proteção contra ataques comuns (SQL Injection, CSRF, XSS, Acesso Não Autorizado a Rotas) e o isolamento de privilégios administrativos.

---

## 2. Requisitos e Diretrizes de Segurança

1. **Criptografia de Senhas (Hashing):**
   * As senhas de usuários são submetidas a funções de dispersão criptográfica (SHA-256) via [`PasswordUtil.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/PasswordUtil.java).
   * Armazenamento padronizado no banco com prefixo `{sha256}`.
2. **Prevenção contra SQL Injection:**
   * Todas as instruções SQL executadas pelo `DataMapper` e `AgendamentoService` utilizam exclusivamente `PreparedStatement` com parâmetros tipados (`?`), impedindo a interpolação direta de strings oriundas do usuário no código SQL.
3. **Proteção e Mascaramento de Rotas Administrativas:**
   * O painel administrativo opera sob rota ofuscada (`/MRYnZpAsC9sp/*`) com interceptação mandatória via [`AdminInterceptor.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AdminInterceptor.java).
   * Verificação em todas as requisições de sessão ativa (`usuarioLogado`) e bloqueio automático de perfis não autorizados (ex: Clientes).
4. **Proteção contra Bots (Honeypot):**
   * Presença de campos armadilha invisíveis via [`HoneypotController.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/HoneypotController.java) nos formulários públicos para detecção e descarte automático de submissões automatizadas de spam.

---

## 3. Critérios de Validação e Testes
* **Testes de Injeção SQL:** Envio de payloads maliciosos (`' OR '1'='1`) em campos de login e busca, confirmando que a aplicação trata como texto literal seguro sem quebras de sintaxe.
* **Teste de Acesso Indevido (IDOR / Broken Access Control):** Tentativa de acesso direto à URL `/MRYnZpAsC9sp/listar/Usuario` sem cookie de sessão ou com sessão de cliente, validando o redirecionamento imediato para a tela de login.
