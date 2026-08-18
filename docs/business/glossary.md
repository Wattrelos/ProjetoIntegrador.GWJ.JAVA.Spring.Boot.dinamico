# 📖 Glossário do Domínio de Negócio (Ubiquitous Language)

Este glossário estabelece a **Linguagem Ubíqua** (conceito central do *Domain-Driven Design - DDD*) para a **Tgo's Barbearia (Sistema GWJ)**, alinhando a terminologia utilizada por barbeiros, clientes, gestores e desenvolvedores de software.

---

## 🏷️ Termos e Conceitos de Domínio

### 1. Agendamento & Grade de Horários
* **Agendamento (`tab_agendamento`):** Registro formal de reserva de um serviço por um cliente com um profissional específico (ou qualquer disponível), em uma determinada data e intervalo horário.
* **Slot / Bloco Base (`tab_grade_horarios`):** Intervalo elementar de atendimento configurado na barbearia. No sistema GWJ, a grade base opera em frações de **20 minutos** (ex: 09:00, 09:20, 09:40...).
* **Blocos Consecutivos:** Sequência ininterrupta de slots de 20 minutos necessários para comportar serviços que demandam tempo superior (ex: *Corte Simples* = 1 bloco / 20 min; *Barba Terapia* = 2 blocos / 40 min; *Combo Premium* = 3 blocos / 60 min).
* **Expediente / Dias de Funcionamento (`tab_dias_funcionamento`):** Faixas horárias (`horario_inicio` e `horario_fim`) em que o estabelecimento se encontra aberto para cada dia da semana.
* **Double-Booking (Sobrecarga de Horário):** Situação de conflito concorrente em que dois agendamentos colidem no mesmo profissional no mesmo período. O sistema impede atipicamente essa condição via isolamento transacional ACID.
* **No-Show (Falta / Não Comparecimento):** Ausência do cliente no horário reservado sem aviso prévio. Mitigado pelo envio de lembretes automáticos e regras de tolerância.
* **Tolerância de Atraso:** Tempo máximo de tolerância após o horário de início (normalmente 10 minutos) antes que o barbeiro possa atender outro cliente por encaixe.

---

### 2. Atendimento & Gestão Operacional
* **Check-in:** Confirmação da chegada presencial do cliente ao salão da barbearia.
* **Comanda de Atendimento:** Registro dinâmico de serviços prestados e produtos consumidos/adquiridos durante o atendimento na cadeira, totalizando o valor final a ser pago.
* **Comissão:** Percentual ou valor fixo rateado ao profissional responsável pelo serviço prestado após o fechamento da comanda.
* **Status do Agendamento:** Ciclo de vida da reserva (`Confirmado` -> `Em Atendimento` -> `Concluído` / `Cancelado`).

---

### 3. Loja, Cosméticos & Estoque
* **Kit Promocional (`tab_produto`):** Conjunto promocional agrupando dois ou mais cosméticos/acessórios (ex: *Kit Pomada Matte + Óleo para Barba*) comercializado sob um valor especial.
* **Baixa de Estoque:** Decremento automático do saldo físico (`tab_produto.estoque`) no momento da confirmação de compra ou fechamento de comanda.
* **Pedido (`tab_pedidos`):** Ordem de compra gerada na loja virtual para produtos físicos com retirada na barbearia ou envio.
* **Item do Pedido (`tab_itens_pedido`):** Linha individual do pedido especificando o produto, a quantidade adquirida e o preço unitário congelado na data da venda.

---

### 4. Usuários, Segurança & Acesso
* **Perfil (`tab_perfil`):** Papel desempenhado pelo usuário no sistema (Administrador, Barbeiro, Recepcionista, Cliente).
* **Permissão Granular (`permissoes` / `tab_perfil_permissao`):** Direito atômico de executar uma ação ou acessar uma entidade (ex: `AGENDAR_HORARIO`, `GERENCIAR_SERVICOS`, `VISUALIZAR_FATURAMENTO`).
* **AdminInterceptor:** Componente de segurança que inspeciona requisições para rotas administrativas ofuscadas (`/MRYnZpAsC9sp/*`) e valida credenciais da sessão.
* **Unit of Work:** Padrão arquitetural que mantém uma lista de objetos afetados por uma transação de negócio e coordena a gravação de alterações e resolução de concorrência via `ThreadLocal`.
