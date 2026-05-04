/* Verificar se as senhas digitadas são iguais */

const inputP1 = document.getElementById('User_password');
const inputP2 = document.getElementById('repeat-password');
const botao = document.getElementById('signup-botao');

function verificarCampos() {
  if (inputP1.value === inputP2.value) {
    botao.disabled = false; // Habilita o botão
  } else {
    botao.disabled = true; // Desabilita o botão
  }
}

// Adiciona listeners para o evento 'input' em ambos os campos
// O evento 'input' é acionado sempre que o valor do input muda.
inputP1.addEventListener('input', verificarCampos);
inputP2.addEventListener('input', verificarCampos);
