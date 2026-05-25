document.addEventListener('DOMContentLoaded', () => {
    // Funções para formatar os valores de acordo com o padrão
    const masks = {
        cpf: (value) => {
            return value
                .replace(/\D/g, '') // Remove tudo o que não for dígito
                .replace(/(\d{3})(\d)/, '$1.$2') // Adiciona ponto após os primeiros 3 dígitos
                .replace(/(\d{3})(\d)/, '$1.$2') // Adiciona ponto após os próximos 3 dígitos
                .replace(/(\d{3})(\d{1,2})/, '$1-$2') // Adiciona hífen antes dos 2 últimos dígitos
                .replace(/(-\d{2})\d+?$/, '$1'); // Impede que o usuário digite mais de 11 dígitos
        },
        cep: (value) => {
            return value
                .replace(/\D/g, '')
                .replace(/^(\d{5})(\d)/, '$1-$2') // Adiciona o hífen após os primeiros 5 dígitos
                .substring(0, 9); // Limita o tamanho a 9 caracteres (8 números + 1 hífen)
        },
        telefone: (value) => {
            let v = value.replace(/\D/g, ''); 
            v = v.replace(/^(\d{2})(\d)/g, '($1) $2'); // Coloca os parênteses em volta do DDD e o espaço
            v = v.replace(/(\d)(\d{4})$/, '$1-$2'); // Coloca o hífen entre o quarto e o quinto dígitos finais
            return v.substring(0, 15); // Suporta no máximo o formato (99) 99999-9999
        }
    };

    // Função auxiliar para injetar o evento caso o campo exista na tela
    const applyMask = (id, maskFunction) => {
        const input = document.getElementById(id);
        if (input) {
            input.addEventListener('input', (e) => {
                e.target.value = maskFunction(e.target.value);
            });
        }
    };

    // Aplica as máscaras
    applyMask('cpf', masks.cpf);
    applyMask('cep', masks.cep);
    applyMask('telefone', masks.telefone);
});