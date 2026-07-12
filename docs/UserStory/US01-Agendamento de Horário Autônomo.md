# 📖 User Story: Agendamento de Horário Autônomo

**Código:** US01  
**Título:** Agendamento de Serviço e Horário pelo Cliente  
**Relevância:** Alta (Core Business)  
**Estimativa:** 5 Story Points  

---

### 🗣️ Descrição da Story
> **Como** cliente da barbearia,  
> **Eu quero** acessar o sistema web, escolher um serviço, um profissional e ver os horários disponíveis,  
> **Para que** eu possa agendar meu corte de forma rápida e autônoma, sem precisar falar no WhatsApp.

---

### ⚙️ Regras de Negócio e Comportamento dos Slots

1. **Grade Base de Slots:** A barbearia opera com uma grade de horários cadastrada na tabela `tab_grade_horarios`. Os slots básicos de atendimento iniciam a cada **20 minutos** (ex: 09:00, 09:20, 09:40, 10:00, etc.).
2. **Duração Dinâmica dos Serviços:** Cada serviço da tabela `tab_servico` possui uma duração específica em minutos (ex: *Corte Simples* = 20 min, *Barba Terapia* = 40 min, *Combo Premium* = 60 min).
3. **Múltiplos Blocos Consecutivos:** Um serviço que dura mais que os 20 minutos padrão irá ocupar **mais de um bloco consecutivo** na agenda do profissional. O sistema deve validar a disponibilidade de blocos sequenciais livres na data selecionada.
4. **Respeito ao Limite de Expediente:** Nenhum agendamento pode ser iniciado se o horário previsto para o seu término ultrapassar o horário de encerramento do expediente da barbearia (definido na tabela `tab_dias_funcionamento`, normalmente 19:00).

---

### 🔍 Critérios de Aceitação (Definition of Done)

#### **Cenário 1: Agendamento de Serviço Longo (Múltiplos Blocos Consecutivos)**
* **Dado** que estou na tela de agendamento (`servicos.html`) e selecionei o serviço "Combo Premium" (Duração: 60 minutos);
* **E** selecionei o Barbeiro "Carlos";
* **E** a grade de horários do Carlos tem blocos de 20 minutos livres a partir das 10:00 (10:00, 10:20, 10:40);
* **E** existe um agendamento confirmado para o Carlos das 11:00 às 11:20;
* **Quando** o sistema carregar os horários disponíveis para a data selecionada;
* **Então** o sistema deve exibir o horário de **10:00** como **Disponível** (pois necessita de 3 blocos livres consecutivos: 10:00-10:20, 10:20-10:40, 10:40-11:00);
* **E** deve exibir o horário de **10:20** e **10:40** como **Indisponíveis** para iniciar o serviço (pois colidiriam com o compromisso das 11:00).

#### **Cenário 2: Restrição de Fim de Expediente**
* **Dado** que o horário de encerramento do expediente na data escolhida é às **19:00**;
* **E** selecionei o serviço "Barba Terapia" (Duração: 40 minutos);
* **E** os blocos das 18:20 and 18:40 estão sem nenhum agendamento marcado no banco de dados;
* **Quando** o sistema listar os horários disponíveis;
* **Então** o horário de **18:20** deve constar como **Disponível** (pois o serviço termina exatamente às 19:00);
* **E** o horário de **18:40** deve constar como **Indisponível** (pois 18:40 + 40 minutos terminaria às 19:20, excedendo o horário de funcionamento).

#### **Cenário 3: Evitar Concorrência Simultânea (Double-Booking)**
* **Dado** que o Cliente A e o Cliente B acessaram a página de agendamento simultaneamente para a mesma data, selecionando o mesmo barbeiro e o mesmo horário das 14:00 (com duração de 20 minutos);
* **Quando** ambos prosseguirem para o checkout e o Cliente A clicar em confirmar a reserva frações de segundo antes do Cliente B;
* **Então** a transação de persistência JDBC associada ao Cliente A deve ser executada com sucesso, criando o registro na tabela `tab_agendamento` com status "Confirmado";
* **E** a tentativa do Cliente B deve falhar na validação de sobreposição executada no `AgendamentoService`, exibindo uma mensagem de erro na tela: *"Desculpe, não há nenhum profissional livre para o horário e duração selecionados."*
