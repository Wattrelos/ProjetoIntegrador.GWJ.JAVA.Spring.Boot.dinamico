# 📜 Regras de Negócio: Atendimento Presencial e Comandas

Este documento especifica as regras de negócio relativas à recepção, execução do serviço pelo barbeiro, acréscimo de itens na comanda e encerramento financeiro do atendimento.

---

## 🎯 Lista de Regras de Negócio (RN-ATE)

### **RN-ATE-01: Ciclo de Vida do Atendimento e Transição de Status**
* **Contexto:** Acompanhamento do cliente desde sua chegada até a saída da barbearia.
* **Regra:** O agendamento deve transitar estritamente pelos seguintes estados:
  $$\text{Confirmado} \xrightarrow{\text{Check-in / Início}} \text{Em Atendimento} \xrightarrow{\text{Pagamento / Fim}} \text{Concluído}$$
* **Comportamento:**
  * Um agendamento com status `'Em Atendimento'` bloqueia o profissional para novos atendimentos imediatos.
  * O status `'Concluído'` finaliza a comanda e alimenta as métricas de faturamento.

---

### **RN-ATE-02: Comanda Dinâmica (Itens e Serviços Extras)**
* **Contexto:** Cliente solicita serviços adicionais na cadeira (ex: Barboterapia ou Sobrancelha) ou adquire produtos cosméticos (ex: Pomada ou Óleo para Barba).
* **Regra:** A comanda vinculada ao atendimento permite a inclusão de múltiplos itens extras antes do fechamento financeiro:
  $$\text{Valor Total Comanda} = \text{tab\_servico.preco} + \sum (\text{Serviços Extras}) + \sum (\text{Produtos Adquiridos})$$

---

### **RN-ATE-03: Baixa de Estoque de Balcão**
* **Contexto:** Venda de produtos realizada presencialmente durante o corte.
* **Regra:** Ao finalizar a comanda com produtos cosméticos vinculados, o sistema deve executar o decremento imediato do saldo em `tab_produto.estoque`, vinculando os itens a um pedido de caixa presencial.

---

### **RN-ATE-04: Rateio e Apuração de Comissões**
* **Contexto:** Apuração financeira do profissional.
* **Regra:** A comissão do barbeiro é calculada exclusivamente sobre o valor líquido dos **serviços prestados** por ele (e percentual diferenciado sobre produtos comercializados, quando parametrizado). Cancelamentos e No-Shows não geram comissão.
