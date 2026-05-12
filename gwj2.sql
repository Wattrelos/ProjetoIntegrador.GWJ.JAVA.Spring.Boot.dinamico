-- 1. Criação do Banco de Dados
CREATE DATABASE IF NOT EXISTS `gwj2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `gwj2`;

-- 2. Configuração de Usuário (conforme application.properties)
CREATE USER IF NOT EXISTS 'desenvolvedor'@'%' IDENTIFIED BY 'b2#FbXPQTu4FYw';
GRANT ALL PRIVILEGES ON `gwj2`.* TO 'desenvolvedor'@'%';
FLUSH PRIVILEGES;

-- 3. Tabela Base: Usuario (Referência: Usuario.java)
CREATE TABLE IF NOT EXISTS `tab_usuario` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `grupo_usuario_id` BIGINT,
    `nome_usuario` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL,
    `senha` VARCHAR(255) NOT NULL,
    `status` TINYINT(1) DEFAULT 1,
    `token` VARCHAR(255),
    `ip` VARCHAR(45),
    `data_cadastro` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- 4. Tabela: Cliente (Herança de Usuario)
CREATE TABLE IF NOT EXISTS `tab_cliente` (
    `id` BIGINT NOT NULL,
    `nome` VARCHAR(100) NOT NULL,
    `sobrenome` VARCHAR(100),
    `telefone` VARCHAR(20),
    `cpf` VARCHAR(14) UNIQUE,
    `observacao` TEXT,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_cliente_usuario` FOREIGN KEY (`id`) REFERENCES `tab_usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tabela: Profissional (Herança de Usuario - Os Barbeiros)
CREATE TABLE IF NOT EXISTS `tab_profissional` (
    `id` BIGINT NOT NULL,
    `nome` VARCHAR(100) NOT NULL,
    `sobrenome` VARCHAR(100),
    `telefone` VARCHAR(20),
    `cpf` VARCHAR(14) UNIQUE,
    `observacao` TEXT,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_profissional_usuario` FOREIGN KEY (`id`) REFERENCES `tab_usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Tabela: Endereco (Associação 1:N)
CREATE TABLE IF NOT EXISTS `tab_endereco` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(100), -- Ex: "Casa", "Trabalho"
    `logradouro` VARCHAR(200),
    `numero` INT,
    `complemento` VARCHAR(100),
    `bairro` VARCHAR(100),
    `cidade` VARCHAR(100),
    `estado` CHAR(2),
    `cep` VARCHAR(10),
    `data_cadastro` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `observacao` TEXT,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- 7. Tabela: Servico (Catálogo: Corte, Barba, etc)
CREATE TABLE IF NOT EXISTS `tab_servico` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL,
    `descricao` VARCHAR(255),
    `preco` DECIMAL(10,2) NOT NULL,
    `duracao` INT, -- Em minutos
    `tipo` VARCHAR(50),
    `ativo` TINYINT(1) DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- 8. Tabela: Produto (Produtos adicionais/Venda)
CREATE TABLE IF NOT EXISTS `tab_produto` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(150) NOT NULL,
    `descricao` TEXT,
    `preco` DECIMAL(10,2) NOT NULL,
    `estoque` INT DEFAULT 0,
    `marca` VARCHAR(100),
    `categoria` VARCHAR(50), -- Ex: Shampoos, Pomadas, Óleos
    `data_cadastro` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- 9. Tabela: Agenda (Onde os horários são marcados)
CREATE TABLE IF NOT EXISTS `tab_agenda` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nome` VARCHAR(100),
    `data` DATETIME NOT NULL,
    `cliente_id` BIGINT,
    `profissional_id` BIGINT,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_agenda_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `tab_cliente` (`id`),
    CONSTRAINT `fk_agenda_profissional` FOREIGN KEY (`profissional_id`) REFERENCES `tab_profissional` (`id`)
) ENGINE=InnoDB;

-- 10. Tabela de Ligação: Agenda x Servico (ManyToMany)
CREATE TABLE IF NOT EXISTS `tab_agenda_servico` (
    `agenda_id` BIGINT NOT NULL,
    `servico_id` BIGINT NOT NULL,
    PRIMARY KEY (`agenda_id`, `servico_id`),
    CONSTRAINT `fk_link_agenda` FOREIGN KEY (`agenda_id`) REFERENCES `tab_agenda` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_link_servico` FOREIGN KEY (`servico_id`) REFERENCES `tab_servico` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Dados Fictícios (Barbearia Educativa)
INSERT INTO `tab_usuario` (id, nome_usuario, email, senha) VALUES 
(1, 'admin', 'contato@barbearia.com', '123'),
(2, 'barbeiro1', 'carlos@email.com', '123'),
(3, 'cliente1', 'marcos@email.com', '123');

INSERT INTO `tab_profissional` (id, nome, sobrenome, telefone) VALUES 
(2, 'Carlos', 'Tesoura', '(11) 99999-1111');

INSERT INTO `tab_cliente` (id, nome, sobrenome, telefone) VALUES 
(3, 'Marcos', 'Oliveira', '(11) 98888-2222');

INSERT INTO `tab_servico` (nome, preco, duracao) VALUES 
('Corte de Cabelo Masculino', 45.00, 30),
('Barba Completa (Toalha Quente)', 35.00, 20),
('Combo: Cabelo + Barba', 70.00, 50);

INSERT INTO `tab_produto` (nome, descricao, preco, estoque, marca, categoria) VALUES 
('Pomada Modeladora', 'Efeito Matte de alta fixação', 45.90, 20, 'Barba de Respeito', 'Estilização'),
('Óleo Vikings', 'Hidratação profunda para fios rebeldes', 32.00, 15, 'Vikings', 'Cuidado Facial'),
('Shampoo Ice', 'Sensação refrescante e limpeza profunda', 28.50, 10, 'QOD Barber Shop', 'Limpeza');

INSERT INTO `tab_agenda` (data, cliente_id, profissional_id, nome) VALUES 
('2023-10-27 14:00:00', 3, 2, 'Corte Semanal');

-- 12. Tabela de Ligação: Cliente x Endereco (ManyToMany)
CREATE TABLE IF NOT EXISTS `tab_cliente_endereco` (
    `cliente_id` BIGINT NOT NULL,
    `endereco_id` BIGINT NOT NULL,
    PRIMARY KEY (`cliente_id`, `endereco_id`),
    CONSTRAINT `fk_link_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `tab_cliente` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_link_endereco` FOREIGN KEY (`endereco_id`) REFERENCES `tab_endereco` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 13. Tabela de Ligação: Profissional x Endereco (ManyToMany)
CREATE TABLE IF NOT EXISTS `tab_profissional_endereco` (
    `profissional_id` BIGINT NOT NULL,
    `endereco_id` BIGINT NOT NULL,
    PRIMARY KEY (`profissional_id`, `endereco_id`),
    CONSTRAINT `fk_link_prof` FOREIGN KEY (`profissional_id`) REFERENCES `tab_profissional` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_link_end_prof` FOREIGN KEY (`endereco_id`) REFERENCES `tab_endereco` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO `tab_agenda_servico` (agenda_id, servico_id) VALUES (1, 1), (1, 2);

-- 14. Inserindo Endereços Fictícios e Vínculos
INSERT INTO `tab_endereco` (id, nome, logradouro, numero, complemento, bairro, cidade, estado, cep) VALUES 
(1, 'Casa', 'Rua das Flores', 123, 'Apto 12', 'Centro', 'São Paulo', 'SP', '01001-000'),
(2, 'Trabalho', 'Avenida Paulista', 1000, 'Andar 15', 'Bela Vista', 'São Paulo', 'SP', '01310-100'),
(3, 'Residencial', 'Rua da Praia', 50, NULL, 'Copacabana', 'Rio de Janeiro', 'RJ', '22020-001'),
(4, 'Barbearia Matriz', 'Rua dos Barbeiros', 10, 'Térreo', 'Centro', 'São Paulo', 'SP', '01002-000');

-- Vinculando Marcos (Cliente ID 3) aos seus endereços
INSERT INTO `tab_cliente_endereco` (cliente_id, endereco_id) VALUES (3, 1), (3, 2);

-- Vinculando Carlos (Profissional ID 2) aos seus endereços
INSERT INTO `tab_profissional_endereco` (profissional_id, endereco_id) VALUES (2, 3), (2, 4);