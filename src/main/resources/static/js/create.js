document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('.create-form');

    if (form) {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const formData = new FormData(form);

            try {
                // Enviamos para o endpoint de criação mencionado no seu README
                const response = await fetch('/create-json', { 
                    method: 'POST',
                    body: new URLSearchParams(formData) 
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Erro ao processar cadastro.');
                }

                const novoId = await response.json();
                
                // Feedback visual
                alert(`Sucesso! Novo registro criado com ID: ${novoId}`);
                
                // Limpa o formulário para permitir o próximo cadastro sem sair da página
                form.reset();
                
                // Foca no primeiro campo de input para agilizar a digitação do próximo
                const firstInput = form.querySelector('input:not([type="hidden"])');
                if (firstInput) firstInput.focus();

            } catch (error) {
                console.error('Erro no cadastro:', error);
                alert('Falha ao cadastrar: ' + error.message);
            }
        });
    }
});