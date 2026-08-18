# ⚡ Documento de Requisito Não-Funcional

## Identificação
* **Código:** RNF005
* **Categoria:** Arquitetura, Manutenibilidade e Padrões de Projeto
* **Módulo:** Backend / Engenharia de Software
* **Prioridade:** Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
A arquitetura do backend deve ser estritamente desacoplada, modular e baseada em padrões de projeto consolidados (Design Patterns), facilitando a manutenibilidade, extensibilidade e clareza para a documentação acadêmica e profissional do projeto.

---

## 2. Padrões de Projeto e Stack Tecnológica

1. **Stack Tecnológica:**
   * Linguagem: **Java 21 (LTS)**.
   * Framework: **Spring Boot 3.2.5** (Spring MVC).
   * Persistência: **JDBC Nativo** com driver MariaDB/MySQL.
   * Motor de Templates: **Thymeleaf**.
2. **Padrões de Projeto Implementados:**
   * **Service Layer & Service Registry:** Centralização das regras de negócio desacoplada dos controladores HTTP via [`ServiceRegistry.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/ServiceRegistry.java).
   * **Unit of Work:** Gestão atômica de transações e controle de ciclo de vida de conexões via `ThreadLocal`.
   * **Data Mapper & Generic Repository:** Separação estrita entre os objetos de domínio em memória e o schema relacional no banco de dados (`gwj5`).
   * **Simple Object Factory:** Instanciação dinâmica de entidades de domínio para operações CRUD genéricas.

---

## 3. Critérios de Validação e Qualidade
* **Baixo Acoplamento:** Nenhuma classe de Controller deve executar instruções SQL diretamente ou abrir conexões JDBC fora da camada de serviços/repositórios.
* **Cobertura e Rastreabilidade:** Todos os requisitos funcionais devem mapear diretamente para serviços, diagramas de atividades e diagramas de sequência correspondentes.
