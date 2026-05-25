
document.addEventListener('DOMContentLoaded', () => {
    const cepInput = document.getElementById('cep');

    if (cepInput) {
        cepInput.addEventListener('blur', function() {
            const cep = this.value.replace(/\D/g, ''); // Remove caracteres não numéricos

            if (cep.length === 8) {
                // Faz a requisição para a API ViaCEP
                fetch(`https://viacep.com.br/ws/${cep}/json/`)
                    .then(response => response.json())
                    .then(data => {
                        if (!data.erro) {
                            // Preenche os campos: os IDs agora são idênticos aos atributos Java (sem prefixo)
                            document.getElementById('logradouro').value = data.logradouro;
                            document.getElementById('bairro').value = data.bairro;
                            document.getElementById('cidade').value = data.localidade;
                            document.getElementById('estado').value = data.uf; // Input de texto nativo em vez de Select/jQuery
                        } else {
                            alert('CEP não encontrado.');
                        }
                    })
                    .catch(error => console.error('Erro:', error));
            } else {
                alert('CEP inválido.');
            }
        });
    }
});

/* Pontos importantes:

    Evento blur: O preenchimento ocorre quando o usuário sai do campo CEP.
    Limpeza de CEP: replace(/\D/g, '') remove pontos ou traços que o usuário possa digitar.
    API ViaCEP: Usada para retornar os dados em formato JSON.
    Validação: O código verifica se o CEP tem 8 dígitos e se a API retornou um erro (CEP não encontrado). 
	
*/
