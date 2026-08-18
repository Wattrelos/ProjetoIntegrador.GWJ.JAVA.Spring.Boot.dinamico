# ⚡ Documento de Requisito Não-Funcional

## Identificação
* **Código:** RNF004
* **Categoria:** Usabilidade, Experiência do Usuário (UX) e Responsividade
* **Módulo:** Interface Web (Frontend)
* **Prioridade:** Média-Alta
* **Versão:** 1.0.0

---

## 1. Descrição do Requisito
A interface com o usuário deve oferecer uma experiência estética de alto nível (visual moderno, escuro e premium adequado à temática de barbearia), intuitiva e totalmente responsiva (abordagem *Mobile-First*), adaptando-se com fluidez a smartphones, tablets e desktops.

---

## 2. Diretrizes de Design e Usabilidade

1. **Abordagem Mobile-First:**
   * O fluxo de agendamento (`servicos.html`) e a navegação pela loja (`shop.html`) devem ser operáveis com apenas uma mão em dispositivos móveis com telas a partir de 360px de largura.
   * Elementos clicáveis (botões de horário, cards de serviços) devem possuir área de toque mínima de `44x44px`.
2. **Design System & Estilização:**
   * Utilização de CSS Vanilla em `style.css` com paleta de cores consistente: tons escuros sofisticados, contrastes dourados/âmbar e tipografia moderna (Google Fonts).
   * Feedback visual imediato com transições suaves, estados `:hover`, `:active`, *toasts* informativos e badges dinâmicos de contagem de carrinho.
3. **Máscaras de Entrada e Validação Instantânea:**
   * Aplicação automática de máscaras para números de telefone/WhatsApp (`(XX) 9XXXX-XXXX`) e CPF para prevenir erros de digitação antes do envio do formulário.

---

## 3. Critérios de Validação e Testes
* **Responsividade Cross-Device:** Verificação visual em resoluções mobile (375x667, 412x915) e desktop (1920x1080), sem overflow horizontal indesejado ou textos truncados.
* **Acessibilidade e Contraste:** Verificação do contraste de texto com fundo (índice WCAG AA mínimo de 4.5:1).
