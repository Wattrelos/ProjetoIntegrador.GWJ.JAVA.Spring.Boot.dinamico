document.addEventListener('DOMContentLoaded', () => {
    // Captura os formulários de cadastro
    const forms = document.querySelectorAll('.create-form');

    forms.forEach(form => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault(); // Impede o reload da página

            const formData = new FormData(form);

            try {
                // Envia para o endpoint de criação
                const response = await fetch('/create-json', { 
                    method: 'POST',
                    body: new URLSearchParams(formData) 
                });

                const responseText = await response.text();

                if (!response.ok || !responseText) {
                    // Se não for OK ou o corpo vier vazio, lançamos o erro com o que tivermos de texto
                    throw new Error(responseText || 'O servidor não retornou um ID. Verifique se houve erro no console do Java.');
                }

                // Como o servidor retorna apenas o número do ID, tratamos como texto
                const idGerado = responseText;
                alert(`Sucesso! Novo registro criado com ID: ${idGerado}`);
                
                form.reset(); // Limpa o formulário após o sucesso
            } catch (error) {
                console.error('Erro ao cadastrar:', error);
                alert('Falha ao cadastrar objeto: ' + error.message);
            }
        });
    });
});