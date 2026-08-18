# ⚡ Documento de Requisito Não-Funcional

## Identificação
* **Código:** RNF001
* **Categoria:** Desempenho e Eficiência (Performance)
* **Módulo:** Core / Todas as Camadas
* **Prioridade:** Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve apresentar alto desempenho operacional, baixa latência de resposta em requisições web e carregamento ágil das páginas dinâmicas e endpoints REST. As consultas de horários disponíveis e de catálogo de produtos devem responder em tempo hábil para garantir uma experiência de navegação fluida ao cliente.

---

## 2. Métricas e Metas de Desempenho (SLAs / NFR Metrics)

| Operação / Endpoint | Tempo Máximo de Resposta (SLA) | Carga Alvo |
| :--- | :--- | :--- |
| **Consulta de Grade de Horários Livres** (`/api/agendamentos/disponibilidade`) | **< 1.2 segundos** | Até 50 requisições concorrentes |
| **Carregamento da Vitrine da Loja** (`/shop`) | **< 800 milissegundos** | Renderização com 20+ produtos ativos |
| **Confirmação de Agendamento / Checkout** | **< 1.5 segundos** | Incluindo transação JDBC e validação de concorrência |
| **Login e Autenticação de Usuário** | **< 500 milissegundos** | Incluindo computação de hash SHA-256 e consulta SQL |
| **Operações CRUD no Painel Administrativo** | **< 600 milissegundos** | Listagem, Edição e Exclusão via `JsonController` |

---

## 3. Estratégias Técnicas de Implementação

1. **Acesso Direto a Dados via JDBC:** A persistência é realizada através de JDBC nativo (com driver MariaDB/MySQL) e `DataMapper` customizado, eliminando o overhead de inicialização, proxies e sessões pesadas de frameworks ORM tradicionais (como Hibernate/JPA).
2. **Índices Estratégicos no Banco de Dados:**
   * Índices compostos na tabela `tab_agendamento` sobre as colunas `(data_agendamento, profissional_id, status)` e `(hora_inicio, hora_fim)`.
   * Chave estrangeira indexada `grade_horarios_id`.
   * Índice único sobre a coluna `email` em `tab_usuario`.
3. **Gerenciamento Eficiente de Recursos:** Utilização de `try-with-resources` em todas as instruções `PreparedStatement`, `ResultSet` e no fechamento de conexões gerenciadas pelo `UnitOfWork` com `ThreadLocal`, prevenindo vazamentos de conexão (connection leaks).
4. **Otimização de Assets no Frontend:** CSS modularizado em `style.css` com estilos inline nos componentes críticos, fontes Google pré-carregadas e scripts assíncronos (`fetch` / Vanilla JS) sem dependências externas pesadas (como jQuery).

---

## 4. Critérios de Validação e Testes
* **Testes de Carga:** Execução de scripts de carga simulando 50 usuários simultâneos consultando a grade de agendamentos em dias de pico (ex: sextas-feiras e sábados), verificando que 95% das requisições respondem abaixo de 1.2s.
* **Monitoramento de Conexões:** Verificar através de logs que não há conexões abertas residuais no pool JDBC após o ciclo de vida de cada requisição.
