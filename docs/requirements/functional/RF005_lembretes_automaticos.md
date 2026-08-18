# 📋 Documento de Requisito Funcional

## Identificação
* **Código:** RF005
* **Título:** Módulo de Lembretes Automáticos e Notificações
* **Módulo:** Notificações & Automação em Segundo Plano
* **Prioridade:** Média-Alta (Redução de No-Shows)
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
O sistema deve executar uma rotina automatizada em segundo plano (via tarefa agendada `@Scheduled`) para identificar compromissos confirmados nas próximas 24 horas e 2 horas que ainda não receberam lembrete. O serviço deve montar mensagens personalizadas com o nome do cliente, serviço, barbeiro, data/hora e links diretos para confirmação de presença ou cancelamento rápido, disparando as notificações via WhatsApp / E-mail e registrando o histórico de envio.

---

## 2. Atores Envolvidos
* **Sistema (TaskScheduler / Daemon):** Dispara a varredura em intervalos regulares.
* **Gateway de Mensageria (WhatsApp / SMTP):** Realiza a entrega do pacote de mensagens.
* **Cliente (Destinatário):** Recebe o lembrete e interage confirmando ou cancelando o horário.

---

## 3. Entradas e Saídas

### 3.1 Entradas
* Trigger de cron programado (ex: a cada 30 minutos).
* Registros de `tab_agendamento` com status `'Confirmado'` e flag `lembrete_enviado = false`.

### 3.2 Saídas
* Disparo de mensagens de texto formatadas para o WhatsApp e/ou e-mail do cliente.
* Atualização no banco de dados marcando a flag `lembrete_enviado = true` e data/hora do envio (`lembrete_data_envio`).
* Geração de logs com status de sucesso ou falha na entrega.

---

## 4. Regras de Negócio (RN)

1. **RN01 - Unicidade de Notificação:** Cada agendamento deve receber no máximo um lembrete principal de 24h e um lembrete de proximidade de 2h, impedindo mensagens duplicadas (spam).
2. **RN02 - Agendamentos Cancelados:** Agendamentos com status `'Cancelado'` não devem, sob nenhuma hipótese, disparar notificações de lembrete.
3. **RN03 - Tolerância a Falhas de Gateway:** Caso a API externa de WhatsApp ou o servidor SMTP retorne erro de timeout ou indisponibilidade, o sistema deve registrar a falha no log e permitir uma nova tentativa na próxima execução do cron.

---

## 5. Critérios de Aceitação (BDD / Definition of Done)

### **Cenário 1: Varredura e Disparo com Sucesso**
* **Dado** que existem 3 agendamentos confirmados para o dia de amanhã com a flag `lembrete_enviado = 0`;
* **Quando** o cron de notificações for acionado pelo Spring Boot;
* **Então** o serviço deve enviar a notificação formatada para cada um dos 3 clientes;
* **E** o campo `lembrete_enviado` de cada registro deve ser atualizado para `1` na tabela `tab_agendamento`.

---

## 6. Mapeamento no Banco de Dados (`gwj5`)
* **Tabela `tab_agendamento`:** `id`, `data_agendamento`, `hora_inicio`, `cliente_nome`, `cliente_telefone`, `status`, `lembrete_enviado`, `lembrete_data_envio`.
* **Tabela `tab_cliente`:** `telefone`, `email`.

---

## 7. Classes e Componentes Relacionados
* **Agendador / Tarefas:** `TaskScheduler` (Spring `@Scheduled`)
* **Serviços:** [`AgendamentoService.java`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/src/main/java/com/gwj/service/AgendamentoService.java)
* **Diagramas Relacionados:** [`envio_lembretes_automaticos.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/activity/envio_lembretes_automaticos.puml), [`sequenceDiagramNotificationJob.puml`](file:///home/kiruma/Documentos/Projetos_JAVA_SpringBoot/ProjetoIntegrador.GWJ.JAVA.Spring.Boot.dinamico/docs/workflows/sequence/sequenceDiagramNotificationJob.puml)
