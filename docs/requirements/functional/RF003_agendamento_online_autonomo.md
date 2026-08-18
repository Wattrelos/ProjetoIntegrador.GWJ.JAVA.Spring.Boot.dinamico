# 📋 Documento de Requisito Funcional

## Identificação
* **Código:** RF003
* **Título:** Agendamento Online Autônomo e Prevenção de Concorrência
* **Módulo:** Agendamento & Checkout
* **Prioridade:** Crítica (Core Business)
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve permitir que o cliente realize o agendamento de serviços de beleza masculina de forma 100% autônoma, escolhendo serviço, profissional, data, horário e preenchendo seus dados de contato. O processo deve garantir atomicidade transacional e controle rigoroso de concorrência, impedindo reservas duplicadas (*double-booking*) para o mesmo barbeiro no mesmo intervalo de tempo.

---

## 2. Atores Envolvidos
* **Cliente:** Realiza a seleção e submete a reserva de atendimento.
* **Barbeiro:** Tem sua agenda reservada com os dados do cliente e serviço.
* **Sistema (AgendamentoService / UnitOfWork):** Valida sobreposições e orquestra a persistência segura no MySQL.

---

## 3. Entradas e Saídas

### 3.1 Entradas
* `servicoId` (Long)
* `profissionalId` (Long - opcional)
* `data` (String `YYYY-MM-DD`)
* `horaInicio` (String `HH:mm`)
* `nome` e `sobrenome` (String)
* `email` (String)
* `telefone` (String - WhatsApp)
* `formaPagamento` (String - Presencial Dinheiro/Pix ou Cartão)

### 3.2 Saídas
* Registro persistido em `tab_agendamento` com status `'Confirmado'`.
* Redirecionamento para a página de confirmação (`/order-confirmation`) com o protocolo gerado, resumo do serviço e orientações de tolerância.
* Resposta de erro apropriada em caso de concorrência ou conflito de horário.

---

## 4. Regras de Negócio (RN)

1. **RN01 - Bloqueio de Concorrência Simultânea (Anti Double-Booking):** Antes de salvar o agendamento, o serviço deve executar uma consulta com trava transacional verificando se existe qualquer registro em `tab_agendamento` com status `'Confirmado'` que conflite com o intervalo `[horaInicio, horaFim]`. Caso haja conflito, a transação deve sofrer Rollback imediato.
2. **RN02 - Validação de Data Futura:** O sistema não deve permitir o agendamento para horários que já tenham passado no dia corrente ou para datas retroativas.
3. **RN03 - Associação com a Grade Oficial:** O agendamento deve estar amarrado a um slot inicial existente na tabela `tab_grade_horarios`.
4. **RN04 - Tolerância e Política de No-Show:** A tela de confirmação deve orientar o cliente sobre a tolerância máxima de atraso (geralmente 10 minutos) antes do cancelamento automático.

---

## 5. Critérios de Aceitação (BDD / Definition of Done)

### **Cenário 1: Agendamento Concluído com Sucesso**
* **Dado** que o cliente escolheu o serviço "Corte Degradê" no dia 20/08 às 15:00 com o barbeiro Carlos;
* **E** o barbeiro Carlos não possui nenhum agendamento marcado entre 15:00 e 15:30;
* **Quando** o cliente preencher seus dados e clicar em "Confirmar Reserva";
* **Então** a transação deve ser gravada com sucesso em `tab_agendamento` com status "Confirmado";
* **E** o cliente deve ser redirecionado para a tela `/order-confirmation?id={idGerado}`.

### **Cenário 2: Tentativa de Reserva Simultânea no Mesmo Horário (Double-Booking)**
* **Dado** que o Cliente A e o Cliente B tentam reservar o mesmo barbeiro às 16:00 para o mesmo serviço;
* **Quando** o Cliente A confirmar a reserva 50 milissegundos antes do Cliente B;
* **Então** o agendamento do Cliente A deve ser confirmado;
* **E** a tentativa do Cliente B deve falhar com a mensagem: *"Desculpe, este horário acabou de ser reservado por outro cliente."*

---

## 6. Mapeamento no Banco de Dados (`gwj5`)
* **Tabela `tab_agendamento`:**
  * `id` (INT, PK AUTO_INCREMENT)
  * `cliente_nome` (VARCHAR)
  * `cliente_telefone` (VARCHAR)
  * `profissional_id` (INT, FK `tab_profissional`)
  * `servico_id` (INT, FK `tab_servico`)
  * `data_agendamento` (DATE)
  * `hora_inicio` (TIME)
  * `hora_fim` (TIME)
  * `status` (VARCHAR - 'Confirmado', 'Cancelado', 'Concluído')
  * `grade_horarios_id` (INT, FK `tab_grade_horarios`)

---

## 7. Classes e Componentes Relacionados
* **Controlador:** [`AgendaController.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/controller/AgendaController.java)
* **Serviço:** [`AgendamentoService.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java) (`confirmarReserva`, `determinarProfissionalLivre`)
* **Transação:** [`UnitOfWork.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/transaction/UnitOfWork.java)
* **Templates:** `servicos.html`, `checkout.html`, `order-confirmation.html`
