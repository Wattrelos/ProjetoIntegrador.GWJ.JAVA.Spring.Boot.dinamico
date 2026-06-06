# Guia de Estrutura do Projeto (Para Avaliação)

Olá, professor! 

Para facilitar a avaliação deste projeto e poupar seu tempo, preparamos este guia rápido. Este é um projeto **Java Spring Boot (MVC)** gerenciado pelo **Maven**. 

Abaixo estão listados os diretórios onde o código-fonte desenvolvido por nós se encontra, ignorando pastas geradas automaticamente pelo sistema, IDE ou compilador (como `target/`, `.settings/`, `.mvn/`, `.classpath`, etc).

---

## 📂 Árvore de Diretórios Principal

Todo o código relevante está concentrado na pasta **`src/main/`**. Ela se divide em duas frentes principais: `java` (Backend) e `resources` (Frontend/Configurações).

### 1. Backend: Código Java (`src/main/java/com/gwj/`)
Aqui estão as regras de negócio, os padrões de projeto e o controle de fluxo (MVC).

- **`controller/`**
  - Contém os controladores do Spring MVC.
  - **Destaque:** `GenericViewController.java` (Orquestra as requisições HTTP de forma dinâmica utilizando Reflexão e Factory).
  - **Destaque:** `GlobalExceptionHandler.java` (Anotado com `@ControllerAdvice` atua como interceptor para tratamento centralizado de erros 404, 500 e recursos estáticos ausentes).
  - **Destaque:** `HoneypotController.java` (Armadilha de segurança que bloqueia tentativas de acesso à antiga rota `/admin` devolvendo 403 Forbidden).

- **`model/`** (Onde a maior parte da lógica e padrões GoF se concentram):
  - **`domain/entities/`**: Classes de domínio do projeto com herança (ex: `Usuario.java`, `Cliente.java`, `Endereco.java`).
  - **`dataAccessObject/` (DAO)**: Classes responsáveis pela persistência de dados.
    - **Destaque:** `DataAccessObject.java` (Um CRUD totalmente dinâmico que lê atributos via Reflexão e monta instruções SQL em tempo de execução).
    - **Destaque:** Padrão *Singleton* utilizado para gerenciar a conexão com o banco de dados.
  - **`dataTransferObject/` (DTO)**:
    - **Destaque:** `EntityMapper.java` (Preenche os objetos dinamicamente com os dados recebidos via requisição HTTP).
  - **`domain/factory/`**: 
    - **Destaque:** `SimpleObjectFactory.java` (Implementação do padrão *Factory* para instanciar objetos corretos com base em strings).

- **`AppConfig.java`**: Arquivo de constantes e leitura do `application.properties`.

### 2. Frontend e Configurações (`src/main/resources/`)
Aqui se encontram as views, estilos, scripts e configurações do Spring.

- **`templates/`** (Views HTML):
  - Telas dinâmicas renderizadas no servidor utilizando o motor **Thymeleaf**.
  - Contém os formulários dinâmicos genéricos (`create.html`, `edit.html`, `detalhe.html`, `listagem-dinamica.html`).
  - **`layouts/` e `fragments/`**: Padrão *Decorator/Template*, isolando cabeçalhos e menus para reaproveitamento de código (`main-layout.html`).

- **`static/`** (Arquivos Estáticos):
  - **`css/`**: Estilos da aplicação (`style.css`).
  - **`js/`**: Scripts no lado do cliente (Vanilla JS), contendo interceptadores de formulário e utilitários como máscaras (`masks.js`) e preenchimento de CEP (`automatic-address.js`).

- **`application.properties`**:
  - Arquivo nativo do Spring Boot contendo a porta do servidor web (`8089`) e as credenciais de conexão do banco de dados relacional.

### 3. Gerenciamento de Dependências
- **`pom.xml`**:
  - Localizado na raiz do projeto. Contém as configurações do Maven, listando dependências essenciais como `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `jakarta.persistence-api` e o driver `mariadb-java-client`.

---

## 💡 Resumo dos Padrões de Projeto (GoF) Aplicados:
*   **Singleton**: Gerenciamento de conexão com o banco de dados.
*   **Factory**: Construção dinâmica de instâncias de classes de domínio.
*   **DAO**: Abstração e encapsulamento do acesso ao banco de dados utilizando *Java Reflection* para persistência genérica e recursiva (suportando herança).
*   **Decorator / Template View**: Arquitetura de layouts e fragmentos com Thymeleaf.
*   **Interceptor**: Captura e tratamento centralizado de exceções e requisições HTTP mal sucedidas.