# 📄 Documento de Design de Software (SDD) - GWJ Tgo's Barbearia

| Informações Gerais | Detalhes |
| :--- | :--- |
| **ID** | ssd-001 |
| **Título** | Documento de Design de Software (Software Design Document - SDD) |
| **Status** | Em Desenvolvimento |
| **Versão** | 1.0.0 |
| **Autor** | Grupo GWJ / IA Antigravity |
| **Data** | 12/07/2026 |

---

## 1. Introdução

### 1.1 Objetivo do Documento
Este documento descreve as decisões de arquitetura e design de software aplicadas no **Sistema GWJ para Tgo's Barbearia**. Ele serve como guia técnico detalhado para desenvolvedores e arquitetos, especificando os padrões de projeto, a estrutura da camada de dados (EER), o fluxo de comunicação entre componentes (MVC) e as regras lógicas cruciais do sistema, como o algoritmo dinâmico de reserva de horários.

### 1.2 Escopo do Sistema
O sistema visa otimizar o agendamento autônomo de horários para clientes e gerenciar o fluxo administrativo de uma barbearia (serviços, profissionais, permissões, controle financeiro e estoque de produtos).

---

## 2. Arquitetura do Sistema

A aplicação adota o padrão de arquitetura em camadas executado sob o **Spring Boot (MVC)**, com foco no desacoplamento e na injeção de dependências.

Abaixo, a divisão clara de responsabilidades das camadas:

```
[ Camada de Apresentação (View) ]
        |  (HTML5 + Thymeleaf + Vanilla CSS / JS)
        v
[ Camada de Controle (Controller & Interceptors) ]
        |  (Spring MVC - Ex: GenericViewController, JsonController, LoginController)
        v
[ Camada de Mapeamento (DTO / Reflection) ]
        |  (EntityMapper, SimpleObjectFactory)
        v
[ Camada de Serviços (Service & Transactions) ]
        |  (AgendamentoService, AgendaService - Regras de Negócio e Unit of Work)
        v
[ Camada de Repositório (Repository Interface) ]
        |  (GenericRepository, SpecRepos)
        v
[ Mapeador ORM Customizado (Data Mapper / JDBC) ]
        |  (DataAccessObject / DataMapper, QueryBuilder, ConnectionDB)
        v
[ Banco de Dados (MySQL / MariaDB) ]
```

### 2.1 Detalhe das Camadas
1. **Apresentação (View):** Arquivos HTML dinâmicos processados no lado do servidor pelo motor **Thymeleaf**. O layout base é reaproveitado através do `thymeleaf-layout-dialect` (decoradores de templates).
2. **Controle (Controllers):** Controladores do Spring interceptam as rotas. Destaca-se o `GenericViewController` que orquestra requisições de renderização mapeando dinamicamente as classes de domínio.
3. **Mapeamento HTTP (DTO):** O `EntityMapper` lê dados de formulários HTTP (`HttpServletRequest`) e popula os campos de objetos de domínio via reflexão, abstraindo o binding manual.
4. **Serviços (Service):** Concentram as regras de negócio do sistema. O `UnitOfWork` gerencia as transações, garantindo propriedades ACID.
5. **Persistência Customizada (Data Mapper / JDBC):** Abstrai e executa as queries SQL. Utiliza **Java Reflection** genérico para ler os atributos de entidades e montar instruções SQL dinamicamente no `DataAccessObject`, evitando o uso de frameworks pesados (como o Hibernate) por razões pedagógicas e de desempenho.

---

## 3. Modelagem de Dados (EER)

O banco de dados relacional (MySQL/MariaDB, schema `gwj5`) foi projetado adotando as regras clássicas de normalização e integridade referencial. 

### 3.1 Especialização e Herança (Joined Strategy)
No nível de banco de dados relacional, representamos a herança de classes utilizando tabelas separadas. A tabela `tab_usuario` contém os atributos comuns (credenciais, e-mail, perfil, etc.). As tabelas `tab_cliente` e `tab_profissional` atuam como especializações (classes filhas). Elas compartilham a chave primária `id`, que também funciona como chave estrangeira (`FK`) apontando para `tab_usuario(id)` com restrição `ON DELETE CASCADE`.

### 3.2 Diagrama EER e Relações Críticas
* O diagrama completo de tabelas e tipos de dados está documentado em [EER-diagram.puml](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/architecture/EER-diagram.puml).
* **Associações N:M:** Relacionamentos do tipo Muitos-para-Muitos são resolvidos com tabelas associativas explícitas (`tab_cliente_endereco`, `tab_profissional_endereco`, `tab_perfil_permissao` e `tab_agenda_servico`).

---

## 4. Padrões de Projeto (Design Patterns GoF) Implementados

| Padrão | Implementação no Projeto | Objetivo |
| :--- | :--- | :--- |
| **Singleton** | `ConnectionDB` | Garante uma única instância ativa de gerenciamento e inicialização da conexão com o banco de dados. |
| **Factory** | `SimpleObjectFactory` | Instancia dinamicamente entidades a partir do seu nome textual (String), reduzindo o acoplamento do controle sobre o modelo. |
| **Data Access Object (DAO) / Repository** | `DataAccessObject` / `IRepository` | Abstrai e encapsula todo o acesso à fonte de dados. Utiliza Reflection recursivo para buscar propriedades declaradas em toda a hierarquia de classes. |
| **Unit of Work** | `UnitOfWork` (com `ThreadLocal`) | Gerencia o escopo de uma transação. Compartilha a mesma conexão JDBC entre múltiplos repositórios sob a mesma thread de execução e realiza commit/rollback automaticamente. |
| **Decorator (Template View)** | Thymeleaf Fragments / Layout Dialect | Define esqueletos de visualização compartilhados, inserindo conteúdos específicos sem duplicar a estrutura HTML básica. |
| **Interceptor / Handler** | `AdminInterceptor` e `GlobalExceptionHandler` | O primeiro valida permissões de sessão antes que a requisição chegue aos controllers administrativos. O segundo captura exceções HTTP globalmente na aplicação. |

---

## 5. Algoritmo Crítico: Grade e Validação de Slots de Agendamento

O algoritmo implementado no método `getHorariosDisponiveis` em [AgendamentoService.java](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java) funciona da seguinte forma:

1. **Leitura da Duração e Parâmetros:** Obtém a duração do serviço escolhido (`tab_servico.duracao`) e a lista de profissionais aplicáveis.
2. **Definição de Limites de Expediente:** Consulta `tab_dias_funcionamento` para obter as horas de abertura e fechamento da barbearia (ex: 09:00 e 19:00).
3. **Carga dos Slots Base:** Carrega os horários iniciais cadastrados na grade para aquele dia (definidos de 20 em 20 minutos na `tab_grade_horarios`).
4. **Carga dos Agendamentos Existentes:** Busca no banco todas as reservas ativas da data cuja hora esteja confirmada.
5. **Cálculo em Memória de Disponibilidade (Consecutividade e Colisão):**
   Para cada horário de início (`slotStart`):
   - Calcula o horário de término esperado: `slotEnd = slotStart + duracao`.
   - Verifica se o término ultrapassa o fechamento (`slotEnd <= diaFim`). Caso ultrapasse, o slot de início é invalidado.
   - Verifica se o horário de início já passou (se a consulta for para a data atual).
   - Realiza a verificação de colisão contra os agendamentos existentes do barbeiro selecionado usando a fórmula lógica de overlap:
     $$\text{Overlap} = (\text{slotStart} < \text{reserva.horaFim}) \land (\text{reserva.horaInicio} < \text{slotEnd})$$
   - Se houver colisão ou violar os limites, o horário de início (`slotStart`) é marcado como indisponível.

---

## 6. Fluxo de Dados e Interações

Os fluxos de execução referentes ao agendamento de horários (passos de consulta de horários livres e confirmação de reserva) estão representados e documentados de forma detalhada através de diagramas de sequência no arquivo [sequenceDiagramBooking.puml](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/sequence/sequenceDiagramBooking.puml).
