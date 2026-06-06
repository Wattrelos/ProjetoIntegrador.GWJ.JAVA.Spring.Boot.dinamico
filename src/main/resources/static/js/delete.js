document.addEventListener('DOMContentLoaded', function() {
    // Elementos do Modal
    const deleteModalElement = document.getElementById('deleteConfirmModal');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    
    // Se o modal não existir na página, encerra a execução do script
    if (!deleteModalElement) return;
    
    // Instancia o modal usando a API do Bootstrap 5
    const deleteModal = new bootstrap.Modal(deleteModalElement);
    
    // Variáveis para armazenar o ID e a Entidade temporariamente
    let currentId = null;
    let currentEntity = null;
    
    // Seleciona todos os botões de exclusão na página
    const deleteButtons = document.querySelectorAll('.btn-delete');
    
    // Adiciona o evento de clique para abrir o Modal
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Captura os dados do botão clicado
            currentId = this.getAttribute('data-id');
            currentEntity = this.getAttribute('data-entity');
            
            // Abre o modal
            deleteModal.show();
        });
    });
    
    // Ação do botão "Sim, Excluir" dentro do Modal
    confirmDeleteBtn.addEventListener('click', function() {
        if (!currentId || !currentEntity) return;
        
        // Desabilita o botão para evitar cliques duplos durante a requisição
        confirmDeleteBtn.disabled = true;
        confirmDeleteBtn.innerText = "Excluindo...";
        
        // Envia a requisição DELETE para a API do Spring
        fetch(`/delete-json?entity=${currentEntity}&id=${currentId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                // Recarrega a página automaticamente para atualizar a listagem
                window.location.reload();
            } else {
                alert('Erro ao tentar excluir o registro. Ele pode estar sendo utilizado em outra tabela.');
                deleteModal.hide();
                confirmDeleteBtn.disabled = false;
                confirmDeleteBtn.innerText = "Sim, Excluir";
            }
        });
    });
});