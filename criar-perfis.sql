-- 1. Criação da tabela de Permissões (Ações/Funcionalidades do sistema)
CREATE TABLE IF NOT EXISTS permissoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

-- 2. Inserção das permissões e ações comuns em uma barbearia
INSERT INTO permissoes (nome, descricao) VALUES 
('AGENDAR_HORARIO', 'Permite que o cliente ou recepcionista crie um agendamento'),
('CANCELAR_AGENDAMENTO', 'Permite cancelar um horário agendado'),
('VISUALIZAR_PROPRIA_AGENDA', 'Permite ao barbeiro ver apenas os seus atendimentos'),
('GERENCIAR_TODAS_AGENDAS', 'Permite à recepção/adm mover e organizar horários de todos'),
('GERENCIAR_CLIENTES', 'Permite cadastrar, editar ou bloquear perfis de clientes'),
('GERENCIAR_SERVICOS', 'Permite alterar preços e cadastrar novos serviços (Cabelo, Barba, etc)'),
('VISUALIZAR_FATURAMENTO', 'Permite ver relatórios financeiros e comissões da equipe'),
('GERENCIAR_ESTOQUE', 'Permite dar entrada e saída em produtos (Pomadas, Shampoos)');

-- 3. Criação da tabela intermediária (Relaciona Perfil com suas Permissões)
CREATE TABLE IF NOT EXISTS perfil_permissoes (
    perfil_id INT NOT NULL,
    permissao_id INT NOT NULL,
    PRIMARY KEY (perfil_id, permissao_id),
    FOREIGN KEY (perfil_id) REFERENCES perfis(id) ON DELETE CASCADE,
    FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE
);

-- 4. Vinculando as permissões comuns para cada perfil da barbearia

-- ADMINISTRADOR (Tem acesso a absolutamente tudo)
INSERT INTO perfil_permissoes (perfil_id, permissao_id) 
SELECT 1, id FROM permissoes;

-- RECEPCIONISTA (Gerencia o dia a dia, mas não mexe em configurações estruturais ou faturamento total)
INSERT INTO perfil_permissoes (perfil_id, permissao_id) VALUES
(2, (SELECT id FROM permissoes WHERE nome = 'AGENDAR_HORARIO')),
(2, (SELECT id FROM permissoes WHERE nome = 'CANCELAR_AGENDAMENTO')),
(2, (SELECT id FROM permissoes WHERE nome = 'GERENCIAR_TODAS_AGENDAS')),
(2, (SELECT id FROM permissoes WHERE nome = 'GERENCIAR_CLIENTES')),
(2, (SELECT id FROM permissoes WHERE nome = 'GERENCIAR_ESTOQUE'));

-- BARBEIRO (Foco total no atendimento dele)
INSERT INTO perfil_permissoes (perfil_id, permissao_id) VALUES
(3, (SELECT id FROM permissoes WHERE nome = 'VISUALIZAR_PROPRIA_AGENDA')),
(3, (SELECT id FROM permissoes WHERE nome = 'AGENDAR_HORARIO')), -- Caso ele agende direto para um cliente
(3, (SELECT id FROM permissoes WHERE nome = 'CANCELAR_AGENDAMENTO'));

-- CLIENTE (Apenas o básico para marcar e desmarcar o próprio corte)
INSERT INTO perfil_permissoes (perfil_id, permissao_id) VALUES
(4, (SELECT id FROM permissoes WHERE nome = 'AGENDAR_HORARIO')),
(4, (SELECT id FROM permissoes WHERE nome = 'CANCELAR_AGENDAMENTO'));
