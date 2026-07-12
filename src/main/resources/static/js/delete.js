document.addEventListener('DOMContentLoaded', function() {
    // Elementos do Modal
    const deleteModalElement = document.getElementById('deleteConfirmModal');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    
    // Se o modal não existir na página, encerra a execução do script
    if (!deleteModalElement) return;
    
    // Funções de controle do Modal Customizado
    function showModal() {
        deleteModalElement.classList.add('gwj-modal-show');
    }
    
    function hideModal() {
        deleteModalElement.classList.remove('gwj-modal-show');
    }
    
    // Fecha o modal ao clicar em elementos com data-gwj-dismiss="modal"
    const closeButtons = deleteModalElement.querySelectorAll('[data-gwj-dismiss="modal"]');
    closeButtons.forEach(button => {
        button.addEventListener('click', hideModal);
    });
    
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
            showModal();
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
                hideModal();
                confirmDeleteBtn.disabled = false;
                confirmDeleteBtn.innerText = "Sim, Excluir";
            }
        });
    });
});