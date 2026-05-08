document.addEventListener('DOMContentLoaded', () => {
    // Usamos querySelectorAll para capturar todos os formulários caso haja mais de um na lista
    const forms = document.querySelectorAll('.update-form');
    const containerObjeto = document.querySelector('#containerObjeto'); // Onde o objeto será atualizado

    forms.forEach(form => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const formData = new FormData(form);

            try {
                // Enviamos como POST para a URL base /update-json
                // O parâmetro 'entity' já está dentro do formData (campo oculto)
                const response = await fetch('/update-json', { 
                    method: 'POST',
                    body: new URLSearchParams(formData) 
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Erro na requisição');
                }

                const idAtualizado = await response.json();
                alert(`Sucesso! Objeto com ID ${idAtualizado} foi atualizado.`);
                
                // Recarrega a página para exibir os dados salvos no banco
                location.reload();

            } catch (error) {
                console.error('Erro ao atualizar:', error);
                alert('Falha ao atualizar o objeto: ' + error.message);
            }
        });
    });
});