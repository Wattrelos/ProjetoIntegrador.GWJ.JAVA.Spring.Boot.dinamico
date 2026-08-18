# 📜 Regras de Negócio: Agendamento de Horários

Este documento especifica formalmente as **Regras de Negócio (RN)** aplicadas ao motor de cálculo de disponibilidade, validação de slots e confirmação de agendamentos no **Sistema GWJ**.

---

## 🎯 Lista de Regras de Negócio (RN-AGE)

### **RN-AGE-01: Ocupação de Múltiplos Blocos Consecutivos**
* **Contexto:** A barbearia possui uma grade base padronizada em blocos de **20 minutos** (`tab_grade_horarios`).
* **Regra:** A quantidade de blocos ocupados por um agendamento é calculada pela fórmula:
  $$\text{Blocos Necessários} = \lceil \frac{\text{tab\_servico.duracao}}{20} \rceil$$
* **Comportamento:**
  * Se um serviço tem duração de 40 minutos (2 blocos), o sistema só pode disponibilizar o horário $H$ se tanto o bloco $H$ quanto o bloco $H + 20\text{min}$ estiverem livres na agenda do profissional.
  * Se existir qualquer compromisso intermediário com status `'Confirmado'`, o horário inicial $H$ deve ser classificado como **Indisponível**.
* **Impacto no Código:** [`AgendamentoService.java:L142-L187`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java#L142-L187).

---

### **RN-AGE-02: Respeito ao Horário de Encerramento do Expediente**
* **Contexto:** Cada dia da semana possui um horário máximo de encerramento cadastrado em `tab_dias_funcionamento.horario_fim` (ex: 19:00).
* **Regra:** Nenhum agendamento pode ser iniciado se o horário previsto para a sua conclusão ultrapassar o fechamento do estabelecimento:
  $$\text{Horário Início} + \text{Duração do Serviço} \le \text{tab\_dias\_funcionamento.horario\_fim}$$
* **Exemplo:**
  * Fechamento às 19:00 com serviço de 40 minutos:
    * Início às 18:20 -> Término às 19:00 (**Permitido**).
    * Início às 18:40 -> Término às 19:20 (**Bloqueado**).
* **Impacto no Código:** [`AgendamentoService.java:L149-L159`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java#L149-L159).

---

### **RN-AGE-03: Bloqueio de Horários no Passado**
* **Contexto:** Tentativas de consulta ou reserva para a data de hoje ou datas retroativas.
* **Regra:** 
  * Se $\text{Data} < \text{Hoje}$, toda a grade do dia é inválida.
  * Se $\text{Data} == \text{Hoje}$, qualquer slot com $\text{Horário de Início} \le \text{Horário Atual}$ deve ser marcado como `disponivel = false`.
* **Impacto no Código:** [`AgendamentoService.java:L157-L159`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java#L157-L159).

---

### **RN-AGE-04: Atribuição Automática ("Qualquer Profissional")**
* **Contexto:** O cliente opta por não selecionar um barbeiro específico durante a reserva.
* **Regra:** O sistema deve buscar a lista de todos os profissionais com `status = true` (ativos). Um horário é considerado disponível se **ao menos um** profissional ativo possuir toda a janela de blocos livres. No momento da confirmação, o sistema atribui automaticamente o primeiro profissional elegível disponível.
* **Impacto no Código:** [`AgendamentoService.java:L318-L350`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java#L318-L350).

---

### **RN-AGE-05: Prevenção Atômica de Double-Booking**
* **Contexto:** Concorrência simultânea de múltiplos clientes tentando reservar a mesma vaga de horário.
* **Regra:** O agendamento deve ser persistido dentro de uma transação JDBC gerenciada por `UnitOfWork`. Antes da inserção, deve ser executada a query de bloqueio:
  ```sql
  SELECT COUNT(*) FROM tab_agendamento 
  WHERE profissional_id = ? 
    AND data_agendamento = ? 
    AND status = 'Confirmado' 
    AND hora_inicio < ? 
    AND ? < hora_fim;
  ```
  Caso retorne valor maior que 0, a transação sofre Rollback e retorna erro amigável ao cliente.
* **Impacto no Código:** [`AgendamentoService.java:L352-L368`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java#L352-L368).
