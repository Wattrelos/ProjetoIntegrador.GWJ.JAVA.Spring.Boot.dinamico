

### 1. Patterns of Enterprise Application Architecture
* **Autores:** Martin Fowler et. al.
* **Obra de Referência:** *Patterns of Enterprise Application Architecture* (2002).
* **Onde se aplica no projeto:**
  * Fundamenta diretamente o [DataMapper.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataAccessObject/DataMapper.java), o [UnitOfWork.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/transaction/UnitOfWork.java) e o [QueryBuilder.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataAccessObject/QueryBuilder.java).

---

### 2. Padrões de Projeto Clássicos (GoF)
* **Autores:** Erich Gamma, Richard Helm, Ralph Johnson e John Vlissides (*Gang of Four*).
* **Obra de Referência:** *Design Patterns: Elements of Reusable Object-Oriented Software* (1994).
* **Onde se aplica no projeto:**
  * **Singleton:** Gerenciamento do ciclo de conexão única no banco em [ConnectionDB.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataAccessObject/ConnectionDB.java).
  * **Factory Method / Simple Factory:** Criação e resolução dinâmica de instâncias de entidades via reflexão em [SimpleObjectFactory.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/factory/SimpleObjectFactory.java).
  * **Template Method / Decorator:** Herança e reaproveitamento de layout com fragmentos no Thymeleaf (`main-layout.html`) e fluxo padronizado no CRUD genérico.
  * **Strategy / Interceptor:** Interceptação centralizada de requisições e exceções em [GlobalExceptionHandler.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/GlobalExceptionHandler.java) e [AdminInterceptor.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AdminInterceptor.java).

---

### 3. Padrões de Arquitetura Corporativa Java / J2EE
* **Autores:** Deepak Alur, John Crupi e Dan Malks (Sun Microsystems).
* **Obra de Referência:** *Core J2EE Patterns: Best Practices and Design Strategies* (2001/2003).
* **Onde se aplica no projeto:**
  * **Data Access Object (DAO):** Isolamento e abstração da camada relacional em [GenericRepository.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/repository/GenericRepository.java).
  * **Data Transfer Object (DTO) / Entity Mapper:** Conversão e transporte dinâmico de parâmetros de formulários HTTP para o domínio em [EntityMapper.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/dataTransferObject/EntityMapper.java).
  * **Service Locator / Registry:** Registro e despacho dinâmico de serviços em [ServiceRegistry.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/ServiceRegistry.java).

---

### 4. Código Limpo, Princípios SOLID e Arquitetura em Camadas
* **Autor:** Robert C. Martin (*Uncle Bob*).
* **Obras de Referência:**
  * *Clean Code: A Handbook of Agile Software Craftsmanship* (2008).
  * *Clean Architecture: A Craftsman's Guide to Software Structure and Design* (2017).
  * *Agile Software Development, Principles, Patterns, and Practices* (2002).
* **Onde se aplica no projeto:**
  * **Princípios SOLID:** Separação estrita de responsabilidades (SRP) entre Controller, Service, Repository, Mapper e Builder; Inversão de Dependências (DIP) usando interfaces genéricas ([IRepository.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/repository/IRepository.java), [IService.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/IService.java)).
  * **Tratamento Limpo de Erros:** Exceções desacopladas do fluxo principal com capturas globais amigáveis.

---

### 5. Boas Práticas e Idiomas Avançados da Linguagem Java
* **Autor:** Joshua Bloch (arquiteto de linguagem na Sun Microsystems/Google).
* **Obra de Referência:** *Effective Java* (Java Efetivo - 3ª Edição, 2018).
* **Onde se aplica no projeto:**
  * **Gerenciamento de Recursos com `AutoCloseable`:** Uso de `try-with-resources` no [UnitOfWork.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/transaction/UnitOfWork.java) para fechamento determinístico de transações JDBC.
  * **Concorrência e `ThreadLocal`:** Isolamento de contexto transacional por thread de requisição.
  * **Java Reflection & Generics Seguros:** Boas práticas de introspecção dinâmica com tipos genéricos parametrizados (`<T extends IEntity>`).

---

### 6. Domain-Driven Design (Design Orientado ao Domínio)
* **Autores:** Eric Evans e Vaughn Vernon.
* **Obras de Referência:**
  * Eric Evans: *Domain-Driven Design: Tackling Complexity in the Heart of Software* (2003).
  * Vaughn Vernon: *Implementing Domain-Driven Design* (2013).
* **Onde se aplica no projeto:**
  * **Entidades e Relacionamentos Ricos:** Modelagem do domínio ([Cliente.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Cliente.java), [Profissional.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Profissional.java), [Agendamento.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/model/domain/entities/Agendamento.java)).
  * **Serviços de Domínio:** Encapsulamento de regras complexas de negócio em [AgendamentoService.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java) (cálculo de slots de horários, colisões e janelas de funcionamento).

---

### 7. Spring Framework e Inversão de Controle (IoC)
* **Autores:** Rod Johnson (criador do Spring) e Craig Walls.
* **Obras de Referência:**
  * Rod Johnson: *Expert One-on-One J2EE Design and Development* (2002).
  * Craig Walls: *Spring in Action* (6ª Edição, 2022).
* **Onde se aplica no projeto:**
  * Fundamentação da injeção de dependências, ciclo de vida de beans e orquestração do padrão MVC no Spring Boot 3.x.

---

### 8. Modelagem Relacional e Teoria de Bancos de Dados
* **Autores:** Peter Chen, Ramez Elmasri, Shamkant B. Navathe e C. J. Date.
* **Obras de Referência:**
  * Peter Chen: *The Entity-Relationship Model: Toward a Unified View of Data* (1976).
  * Ramez Elmasri & Shamkant B. Navathe: *Sistemas de Banco de Dados* (7ª Edição, Pearson).
* **Onde se aplica no projeto:**
  * Modelagem EER, cardinalidades N:N com tabelas associativas (`tab_profissional_endereco`, `tab_agenda_servico`), chaves estrangeiras (`@JoinColumn`) e garantias ACID documentadas no [adr-0001.md](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/adr/adr-0001.md).

---

### 9. Modelagem Visual e UML
* **Autores:** Grady Booch, James Rumbaugh e Ivar Jacobson (*The Three Amigos*).
* **Obra de Referência:** *The Unified Modeling Language User Guide* (2ª Edição).
* **Onde se aplica no projeto:**
  * Diagramas de classes de domínio, diagramas estruturais e diagramas de sequência em PlantUML localizados em [diagramas/](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/diagramas/).

---

### 10. Segurança em Aplicações Web
* **Referência:** OWASP Foundation (*Open Web Application Security Project*).
* **Guias:** *OWASP Top 10 Web Application Security Risks* e *OWASP Testing Guide*.
* **Onde se aplica no projeto:**
  * Prevenção de SQL Injection via queries parametrizadas (`PreparedStatement`) no `QueryBuilder`.
  * Defesa ativa e armadilhas automatizadas via [HoneypotController.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/HoneypotController.java).
  * Controle de acesso baseado em papéis/permissões (RBAC).

---

### 📋 Referências Formatadas (Padrão ABNT para Relatórios / Artigos)

```text
ALUR, Deepak; CRUPI, John; MALKS, Dan. Core J2EE Patterns: Best Practices and Design Strategies. 2. ed. Prentice Hall, 2003.
BLOCH, Joshua. Effective Java. 3. ed. Boston: Addison-Wesley, 2018.
BOOCH, Grady; RUMBAUGH, James; JACOBSON, Ivar. The Unified Modeling Language User Guide. 2. ed. Addison-Wesley, 2005.
ELMASRI, Ramez; NAVATHE, Shamkant B. Sistemas de Banco de Dados. 7. ed. São Paulo: Pearson, 2018.
EVANS, Eric. Domain-Driven Design: Tackling Complexity in the Heart of Software. Boston: Addison-Wesley, 2003.
GAMMA, Erich et al. Design Patterns: Elements of Reusable Object-Oriented Software. Boston: Addison-Wesley, 1994.
JOHNSON, Rod. Expert One-on-One J2EE Design and Development. Wrox, 2002.
MARTIN, Robert C. Clean Code: A Handbook of Agile Software Craftsmanship. Prentice Hall, 2008.
MARTIN, Robert C. Clean Architecture: A Craftsman's Guide to Software Structure and Design. Prentice Hall, 2017.
WALLS, Craig. Spring in Action. 6. ed. Manning Publications, 2022.
```