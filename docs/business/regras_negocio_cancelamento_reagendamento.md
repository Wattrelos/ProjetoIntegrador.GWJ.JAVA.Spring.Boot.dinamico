# 📜 Regras de Negócio: Cancelamento e Reagendamento

Este documento especifica as regras operacionais e políticas de desistência e remarcação de horários no **Sistema GWJ**.

---

## 🎯 Lista de Regras de Negócio (RN-CAN)

### **RN-CAN-01: Prazo Limite de Cancelamento Online (Antecedência Mínima)**
* **Contexto:** Cancelamento autônomo realizado pelo cliente através do site ou link de WhatsApp.
* **Regra:** O cancelamento direto pelo cliente só é autorizado se o intervalo de tempo entre o momento da solicitação e o horário agendado for maior ou igual a **2 horas** (120 minutos):
  $$\text{Data/Hora Agendamento} - \text{Data/Hora Atual} \ge 2\text{ horas}$$
* **Exceção:** Caso o tempo restante seja inferior a 2 horas, o sistema bloqueia o cancelamento automático e instrui o cliente a entrar em contato diretamente com a recepção da barbearia por telefone/WhatsApp.

---

### **RN-CAN-02: Liberação Imediata de Slots na Grade Pública**
* **Contexto:** Efeito do cancelamento sobre a grade de horários.
* **Regra:** Ao confirmar o cancelamento no banco de dados (`tab_agendamento.status = 'Cancelado'`), todos os blocos de horário anteriormente ocupados por aquele agendamento devem voltar a figurar como **Disponíveis** (`disponivel = true`) instantaneamente para consultas públicas subsequentes.

---

### **RN-CAN-03: Reagendamento de Horário**
* **Contexto:** Solicitação de alteração de data/hora pelo cliente ou recepcionista.
* **Regra:** O reagendamento preserva o identificador do agendamento (`id`) e histórico do cliente, alterando os campos `data_agendamento`, `hora_inicio`, `hora_fim` e `grade_horarios_id`, desde que a nova data/horário satisfaça todas as regras de disponibilidade ([`RN-AGE-01`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_agendamento.md) a [`RN-AGE-05`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/business/regras_negocio_agendamento.md)).
