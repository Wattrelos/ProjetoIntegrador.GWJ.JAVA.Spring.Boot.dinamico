# 📋 Documento de Requisito Funcional

## Identificação
* **Código:** RF002
* **Título:** Motor de Busca e Cálculo da Grade de Horários Livres
* **Módulo:** Agendamento & Agenda
* **Prioridade:** Crítica
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve calcular de forma dinâmica e automatizada as janelas de horários disponíveis para atendimento na barbearia. O cálculo cruza a duração do serviço escolhido (`tab_servico.duracao`), os horários de abertura e fechamento da barbearia (`tab_dias_funcionamento`), os slots cadastrados na grade (`tab_grade_horarios`) e os agendamentos já confirmados no banco de dados (`tab_agendamento`), impedindo que horários indisponíveis ou passados sejam apresentados para reserva.

---

## 2. Atores Envolvidos
* **Cliente:** Seleciona a data, o serviço e o barbeiro para visualizar os horários disponíveis.
* **Barbeiro / Profissional:** Possui sua grade preenchida e respeitada automaticamente pelo algoritmo.
* **Sistema (AgendamentoService):** Executa o algoritmo de cálculo de blocos sequenciais livres.

---

## 3. Entradas e Saídas

### 3.1 Entradas
* `servicoId` (Long): Identificador do serviço selecionado para consulta da duração em minutos.
* `profissionalId` (Long, Opcional): ID do barbeiro escolhido (ou nulo para buscar qualquer profissional disponível).
* `data` (String formato `YYYY-MM-DD`): Data pretendida para o atendimento.

### 3.2 Saídas
* Lista em formato JSON contendo todos os horários cadastrados na grade do dia com suas respectivas disponibilidades:
  ```json
  [
    {"horario": "09:00", "disponivel": true},
    {"horario": "09:20", "disponivel": true},
    {"horario": "09:40", "disponivel": false}
  ]
  ```

---

## 4. Regras de Negócio (RN)

1. **RN01 - Respeito ao Limite de Expediente:** Nenhum horário pode ser disponibilizado se a soma `Horário Inicial + Duração do Serviço` ultrapassar o `horario_fim` definido em `tab_dias_funcionamento` para o dia da semana correspondente.
2. **RN02 - Ocupação de Múltiplos Blocos Contínuos:** Serviços com duração superior a 20 minutos (ex: Barba Terapia = 40 min, Combo = 60 min) exigem a disponibilidade de 2 ou mais blocos contínuos livres na agenda do mesmo profissional. Se houver um compromisso intercalado, o horário de início deve constar como indisponível.
3. **RN03 - Bloqueio de Horários no Passado:** Se a data selecionada for a data de hoje (`LocalDate.now()`), slots com horário anterior ao horário corrente (`LocalTime.now()`) devem ser marcados automaticamente como indisponíveis (`disponivel = false`).
4. **RN04 - Fechamento Semanal:** Caso a barbearia esteja configurada como fechada no dia da semana (`tab_dias_funcionamento.aberto = false`), o sistema deve retornar lista vazia ou indicar fechamento.

---

## 5. Critérios de Aceitação (BDD / Definition of Done)

### **Cenário 1: Serviço de Longa Duração e Validação de Blocos Contínuos**
* **Dado** que o cliente selecionou o serviço "Combo Premium" (duração de 60 minutos);
* **E** selecionou o Barbeiro "Lucas";
* **E** o Barbeiro possui agendamento confirmado às 10:40;
* **Quando** o cliente consultar os horários para a data selecionada;
* **Então** o horário de **10:00** deve constar como **Indisponível** (pois ocuparia 10:00-11:00 e colide com 10:40).

### **Cenário 2: Consulta em Horário Próximo ao Fechamento**
* **Dado** que a barbearia encerra o expediente às 19:00;
* **E** o cliente selecionou o serviço "Corte + Barba" (duração de 40 minutos);
* **Quando** o sistema listar os horários disponíveis;
* **Então** o horário de **18:20** deve constar como **Disponível** (término às 19:00);
* **E** o horário de **18:40** deve constar como **Indisponível** (terminaria às 19:20, fora do horário de funcionamento).

---

## 6. Mapeamento no Banco de Dados (`gwj5`)
* **Tabela `tab_dias_funcionamento`:** `dia_semana`, `aberto`, `horario_inicio`, `horario_fim`.
* **Tabela `tab_grade_horarios`:** `dia_funcionamento_id`, `horario_inicio`, `horario_fim`.
* **Tabela `tab_agendamento`:** `data_agendamento`, `hora_inicio`, `hora_fim`, `profissional_id`, `status`.

---

## 7. Classes e Componentes Relacionados
* **Controlador:** [`AgendaController.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AgendaController.java)
* **Serviço de Negócio:** [`AgendamentoService.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java) (`getHorariosDisponiveis`)
* **Padrão Transacional:** [`UnitOfWork.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/transaction/UnitOfWork.java)
