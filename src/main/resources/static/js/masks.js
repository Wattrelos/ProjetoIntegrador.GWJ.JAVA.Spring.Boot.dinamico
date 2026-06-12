document.addEventListener('DOMContentLoaded', () => {
    // Funções para formatar os valores de acordo com o padrão
    const masks = {
        cpfCnpj: (value) => {
            let val = value.replace(/\D/g, '');
            if (val.length <= 11) {
                // Máscara de CPF
                return val.replace(/(\d{3})(\d)/, '$1.$2')
                          .replace(/(\d{3})(\d)/, '$1.$2')
                          .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            } else {
                // Máscara de CNPJ
                return val.replace(/^(\d{2})(\d)/, '$1.$2')
                          .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
                          .replace(/\.(\d{3})(\d)/, '.$1/$2')
                          .replace(/(\d{4})(\d)/, '$1-$2')
                          .substring(0, 18);
            }
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
        },
        money: (value) => {
            let val = value.replace(/\D/g, '');
            if (val === '') return '';
            val = (parseInt(val, 10) / 100).toFixed(2);
            val = val.replace('.', ',').replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1.');
            return 'R$ ' + val;
        }
    };

    // Função auxiliar para injetar o evento em múltiplos campos e formatar o valor inicial
    const applyMaskBySelector = (selector, maskFunction) => {
        const inputs = document.querySelectorAll(selector);
        inputs.forEach(input => {
            // Aplica a máscara no valor inicial (carregado do banco no edit.html)
            if (input.value) {
                input.value = maskFunction(input.value);
            }
            
            // Aplica a máscara ao digitar
            input.addEventListener('input', (e) => {
                e.target.value = maskFunction(e.target.value);
            });
        });
    };

    // Aplica as máscaras buscando por qualquer campo que contenha esses nomes de forma dinâmica
    // O "i" no seletor ignora letras maiúsculas/minúsculas (ex: "cpf", "Cpf", "CPF")
    applyMaskBySelector('input[name*="cpf" i], input[name*="cnpj" i]', masks.cpfCnpj);
    applyMaskBySelector('input[name*="cep" i]', masks.cep);
    applyMaskBySelector('input[name*="telefone" i], input[name*="celular" i]', masks.telefone);

    // Função auxiliar para injetar a máscara em textos HTML estáticos (ex: detalhes e listagens)
    const formatTextBySelector = (selector, maskFunction) => {
        document.querySelectorAll(selector).forEach(el => {
            if (el.textContent) {
                const text = el.textContent.trim();
                if (text && text !== '—') {
                    el.textContent = maskFunction(text);
                }
            }
        });
    };

    // Aplica as máscaras em elementos de texto (detalhe.html usa IDs, listagem-dinamica.html usa data-coluna com span)
    formatTextBySelector('[id*="cpf" i], [id*="cnpj" i], [data-coluna*="cpf" i] span, [data-coluna*="cnpj" i] span', masks.cpfCnpj);
    formatTextBySelector('[id*="cep" i], [data-coluna*="cep" i] span', masks.cep);
    formatTextBySelector('[id*="telefone" i], [id*="celular" i], [data-coluna*="telefone" i] span, [data-coluna*="celular" i] span', masks.telefone);

    // Função específica para campos monetários (trata formatação inicial via parseFloat e impede vazios no blur)
    const applyMoneyMask = (selector) => {
        document.querySelectorAll(selector).forEach(input => {
            // Aplica máscara no valor inicial (carregado do banco na tela de edição)
            if (input.value && !input.value.includes('R$')) {
                let parsedNum = parseFloat(input.value);
                if (!isNaN(parsedNum)) {
                    input.value = masks.money(parsedNum.toFixed(2).replace('.', ''));
                }
            }

            // Aplica máscara ao digitar
            input.addEventListener('input', (e) => {
                e.target.value = masks.money(e.target.value);
            });

            // Evita submissão de valores vazios com apenas 'R$ '
            input.addEventListener('blur', (e) => {
                if (e.target.value === 'R$ 0,00' || e.target.value === 'R$ ') e.target.value = '';
            });
        });
    };

    // Aplica a máscara monetária em campos cujo nome contenha preco, valor ou salario ignorando case sensitive (i)
    applyMoneyMask('input[name*="preco" i], input[name*="valor" i], input[name*="salario" i]');
});