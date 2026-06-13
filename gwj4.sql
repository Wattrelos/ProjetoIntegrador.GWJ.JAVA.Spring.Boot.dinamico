-- phpMyAdmin SQL Dump
-- version 5.2.2deb1+deb13u1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Tempo de geração: 13/06/2026 às 23:37
-- Versão do servidor: 11.8.6-MariaDB-0+deb13u1 from Debian
-- Versão do PHP: 8.4.22

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `gwj2`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `permissoes`
--

CREATE TABLE `permissoes` (
  `id` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `permissoes`
--

TRUNCATE TABLE `permissoes`;
--
-- Despejando dados para a tabela `permissoes`
--

INSERT INTO `permissoes` (`id`, `nome`, `descricao`) VALUES
(1, 'AGENDAR_HORARIO', 'Permite que o cliente ou recepcionista crie um agendamento'),
(2, 'CANCELAR_AGENDAMENTO', 'Permite cancelar um horário agendado'),
(3, 'VISUALIZAR_PROPRIA_AGENDA', 'Permite ao barbeiro ver apenas os seus atendimentos'),
(4, 'GERENCIAR_TODAS_AGENDAS', 'Permite à recepção/adm mover e organizar horários de todos'),
(5, 'GERENCIAR_CLIENTES', 'Permite cadastrar, editar ou bloquear perfis de clientes'),
(6, 'GERENCIAR_SERVICOS', 'Permite alterar preços e cadastrar novos serviços (Cabelo, Barba, etc)'),
(7, 'VISUALIZAR_FATURAMENTO', 'Permite ver relatórios financeiros e comissões da equipe'),
(8, 'GERENCIAR_ESTOQUE', 'Permite dar entrada e saída em produtos (Pomadas, Shampoos)');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_agenda`
--

CREATE TABLE `tab_agenda` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `data` datetime NOT NULL DEFAULT current_timestamp(),
  `cliente_id` bigint(20) DEFAULT NULL,
  `profissional_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_agenda`
--

TRUNCATE TABLE `tab_agenda`;
--
-- Despejando dados para a tabela `tab_agenda`
--

INSERT INTO `tab_agenda` (`id`, `nome`, `data`, `cliente_id`, `profissional_id`) VALUES
(1, 'Corte Semanal', '2023-10-27 14:00:00', 3, 2);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_agendamento`
--

CREATE TABLE `tab_agendamento` (
  `id` int(11) NOT NULL,
  `cliente_nome` varchar(100) NOT NULL,
  `cliente_telefone` varchar(20) DEFAULT NULL,
  `profissional_id` int(11) NOT NULL,
  `servico_id` int(11) NOT NULL,
  `data_agendamento` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fim` time NOT NULL,
  `status` varchar(20) DEFAULT 'Confirmado',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `grade_horarios_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_agendamento`
--

TRUNCATE TABLE `tab_agendamento`;
--
-- Despejando dados para a tabela `tab_agendamento`
--

INSERT INTO `tab_agendamento` (`id`, `cliente_nome`, `cliente_telefone`, `profissional_id`, `servico_id`, `data_agendamento`, `hora_inicio`, `hora_fim`, `status`, `created_at`, `grade_horarios_id`) VALUES
(1, 'Marcos Silva', '(11) 99999-1111', 2, 1, '2026-06-15', '09:00:00', '09:30:00', 'Confirmado', '2026-06-13 00:08:29', NULL),
(2, 'Arthur Rezende', '(11) 99999-2222', 2, 7, '2026-06-15', '10:00:00', '11:50:00', 'Confirmado', '2026-06-13 00:08:29', NULL),
(3, 'Bruno Oliveira', '(11) 99999-3333', 2, 2, '2026-06-15', '14:00:00', '14:20:00', 'Confirmado', '2026-06-13 00:08:29', NULL),
(4, 'Rodrigo Costa', '(11) 99999-4444', 2, 3, '2026-06-15', '15:30:00', '17:00:00', 'Confirmado', '2026-06-13 00:08:29', NULL),
(5, 'Marcos Silva', '(11) 99999-1111', 2, 1, '2026-06-15', '09:00:00', '09:30:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(6, 'Arthur Rezende', '(11) 99999-2222', 2, 7, '2026-06-15', '10:00:00', '11:50:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(7, 'Bruno Oliveira', '(11) 99999-3333', 2, 2, '2026-06-15', '14:00:00', '14:20:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(8, 'Rodrigo Costa', '(11) 99999-4444', 2, 3, '2026-06-15', '15:30:00', '17:00:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(9, 'Felipe Amorim', '(11) 99999-5555', 9, 4, '2026-06-15', '09:00:00', '12:20:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(10, 'Lucas (Pai: Roberto)', '(11) 99999-6666', 9, 8, '2026-06-15', '14:00:00', '14:40:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(11, 'Gustavo Henrique', '(11) 99999-7777', 9, 1, '2026-06-15', '15:00:00', '15:30:00', 'Confirmado', '2026-06-13 00:09:34', NULL),
(23, 'Sebastião Silva', '11123456789', 2, 2, '2026-06-13', '14:30:00', '14:50:00', 'Confirmado', '2026-06-13 01:02:02', NULL),
(24, 'João da Silva', '11123456789', 2, 1, '2026-06-13', '10:20:00', '10:50:00', 'Confirmado', '2026-06-13 22:30:45', 155),
(25, 'Pedro Ferreira da Silva', '11987654321', 2, 4, '2026-06-14', '13:20:00', '16:40:00', 'Confirmado', '2026-06-13 22:33:39', 14);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_agenda_servico`
--

CREATE TABLE `tab_agenda_servico` (
  `agenda_id` bigint(20) NOT NULL,
  `servico_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_agenda_servico`
--

TRUNCATE TABLE `tab_agenda_servico`;
--
-- Despejando dados para a tabela `tab_agenda_servico`
--

INSERT INTO `tab_agenda_servico` (`agenda_id`, `servico_id`) VALUES
(1, 1),
(1, 2);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_cliente`
--

CREATE TABLE `tab_cliente` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `sobrenome` varchar(100) DEFAULT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `cpf` varchar(14) DEFAULT NULL,
  `observacao` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_cliente`
--

TRUNCATE TABLE `tab_cliente`;
--
-- Despejando dados para a tabela `tab_cliente`
--

INSERT INTO `tab_cliente` (`id`, `nome`, `sobrenome`, `telefone`, `cpf`, `observacao`) VALUES
(3, 'Marcos', 'Oliveira de Souza', '(11) 98888-2222', '12345678901', 'Prefere às sextas-feiras'),
(6, 'Teste', 'Teste', '(11) 123456789', '12345612378955', 'teste');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_cliente_endereco`
--

CREATE TABLE `tab_cliente_endereco` (
  `cliente_id` bigint(20) NOT NULL,
  `endereco_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_cliente_endereco`
--

TRUNCATE TABLE `tab_cliente_endereco`;
--
-- Despejando dados para a tabela `tab_cliente_endereco`
--

INSERT INTO `tab_cliente_endereco` (`cliente_id`, `endereco_id`) VALUES
(3, 1),
(3, 2),
(6, 5);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_dias_funcionamento`
--

CREATE TABLE `tab_dias_funcionamento` (
  `id` int(11) NOT NULL,
  `dia_semana` int(11) NOT NULL COMMENT '1 = Domingo a 7 = Sábado',
  `nome` varchar(14) NOT NULL COMMENT 'Nome do dia da semana',
  `aberto` tinyint(1) DEFAULT 1,
  `horario_inicio` time DEFAULT NULL,
  `horario_fim` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_dias_funcionamento`
--

TRUNCATE TABLE `tab_dias_funcionamento`;
--
-- Despejando dados para a tabela `tab_dias_funcionamento`
--

INSERT INTO `tab_dias_funcionamento` (`id`, `dia_semana`, `nome`, `aberto`, `horario_inicio`, `horario_fim`) VALUES
(1, 1, 'Domingo', 1, '09:00:00', '19:00:00'),
(2, 2, 'Segunda-feira', 0, NULL, NULL),
(3, 3, 'Terça-feira', 1, '09:00:00', '19:00:00'),
(4, 4, 'Quarta-feira', 1, '09:00:00', '19:00:00'),
(5, 5, 'Quinta-feira', 1, '09:00:00', '19:00:00'),
(6, 6, 'Sexta-feira', 1, '09:00:00', '19:00:00'),
(7, 7, 'Sábado', 1, '09:00:00', '19:00:00');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_endereco`
--

CREATE TABLE `tab_endereco` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `logradouro` varchar(200) DEFAULT NULL,
  `numero` int(11) DEFAULT NULL,
  `complemento` varchar(100) DEFAULT NULL,
  `bairro` varchar(100) DEFAULT NULL,
  `cidade` varchar(100) DEFAULT NULL,
  `estado` char(2) DEFAULT NULL,
  `cep` varchar(10) DEFAULT NULL,
  `data_cadastro` datetime DEFAULT current_timestamp(),
  `observacao` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_endereco`
--

TRUNCATE TABLE `tab_endereco`;
--
-- Despejando dados para a tabela `tab_endereco`
--

INSERT INTO `tab_endereco` (`id`, `nome`, `logradouro`, `numero`, `complemento`, `bairro`, `cidade`, `estado`, `cep`, `data_cadastro`, `observacao`) VALUES
(1, 'Casa', 'Rua das rosas', 123, 'Apto 12', 'Centro', 'São Paulo', 'SP', '01001-000', '2026-05-07 19:39:17', NULL),
(2, 'Trabalho', 'Avenida Paulista', 1000, 'Andar 15', 'Bela Vista', 'São Paulo', 'SP', '01310-100', '2026-05-07 19:39:17', NULL),
(3, 'Residencial', 'Rua da Praia', 50, NULL, 'Copacabana', 'Rio de Janeiro', 'RJ', '22020-001', '2026-05-07 19:39:17', NULL),
(4, 'Barbearia Matriz', 'Rua dos Barbeiros', 10, 'Térreo', 'Centro', 'São Paulo', 'SP', '01002-000', '2026-05-07 19:39:17', NULL),
(5, 'Paulo', 'Rua Antônio Massa', 999, NULL, 'Jardim do Papai', 'Ferraz de Vasconcelos', 'SP', '08505340', '2026-05-25 19:03:12', NULL);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_grade_horarios`
--

CREATE TABLE `tab_grade_horarios` (
  `id` int(11) NOT NULL,
  `dia_funcionamento_id` int(11) DEFAULT NULL,
  `horario_inicio` time NOT NULL,
  `horario_fim` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_grade_horarios`
--

TRUNCATE TABLE `tab_grade_horarios`;
--
-- Despejando dados para a tabela `tab_grade_horarios`
--

INSERT INTO `tab_grade_horarios` (`id`, `dia_funcionamento_id`, `horario_inicio`, `horario_fim`) VALUES
(1, 1, '09:00:00', '09:20:00'),
(2, 1, '09:20:00', '09:40:00'),
(3, 1, '09:40:00', '10:00:00'),
(4, 1, '10:00:00', '10:20:00'),
(5, 1, '10:20:00', '10:40:00'),
(6, 1, '10:40:00', '11:00:00'),
(7, 1, '11:00:00', '11:20:00'),
(8, 1, '11:20:00', '11:40:00'),
(9, 1, '11:40:00', '12:00:00'),
(10, 1, '12:00:00', '12:20:00'),
(11, 1, '12:20:00', '12:40:00'),
(12, 1, '12:40:00', '13:00:00'),
(13, 1, '13:00:00', '13:20:00'),
(14, 1, '13:20:00', '13:40:00'),
(15, 1, '13:40:00', '14:00:00'),
(16, 1, '14:00:00', '14:20:00'),
(17, 1, '14:20:00', '14:40:00'),
(18, 1, '14:40:00', '15:00:00'),
(19, 1, '15:00:00', '15:20:00'),
(20, 1, '15:20:00', '15:40:00'),
(21, 1, '15:40:00', '16:00:00'),
(22, 1, '16:00:00', '16:20:00'),
(23, 1, '16:20:00', '16:40:00'),
(24, 1, '16:40:00', '17:00:00'),
(25, 1, '17:00:00', '17:20:00'),
(26, 1, '17:20:00', '17:40:00'),
(27, 1, '17:40:00', '18:00:00'),
(28, 1, '18:00:00', '18:20:00'),
(29, 1, '18:20:00', '18:40:00'),
(30, 1, '18:40:00', '19:00:00'),
(31, 3, '09:00:00', '09:20:00'),
(32, 3, '09:20:00', '09:40:00'),
(33, 3, '09:40:00', '10:00:00'),
(34, 3, '10:00:00', '10:20:00'),
(35, 3, '10:20:00', '10:40:00'),
(36, 3, '10:40:00', '11:00:00'),
(37, 3, '11:00:00', '11:20:00'),
(38, 3, '11:20:00', '11:40:00'),
(39, 3, '11:40:00', '12:00:00'),
(40, 3, '12:00:00', '12:20:00'),
(41, 3, '12:20:00', '12:40:00'),
(42, 3, '12:40:00', '13:00:00'),
(43, 3, '13:00:00', '13:20:00'),
(44, 3, '13:20:00', '13:40:00'),
(45, 3, '13:40:00', '14:00:00'),
(46, 3, '14:00:00', '14:20:00'),
(47, 3, '14:20:00', '14:40:00'),
(48, 3, '14:40:00', '15:00:00'),
(49, 3, '15:00:00', '15:20:00'),
(50, 3, '15:20:00', '15:40:00'),
(51, 3, '15:40:00', '16:00:00'),
(52, 3, '16:00:00', '16:20:00'),
(53, 3, '16:20:00', '16:40:00'),
(54, 3, '16:40:00', '17:00:00'),
(55, 3, '17:00:00', '17:20:00'),
(56, 3, '17:20:00', '17:40:00'),
(57, 3, '17:40:00', '18:00:00'),
(58, 3, '18:00:00', '18:20:00'),
(59, 3, '18:20:00', '18:40:00'),
(60, 3, '18:40:00', '19:00:00'),
(61, 4, '09:00:00', '09:20:00'),
(62, 4, '09:20:00', '09:40:00'),
(63, 4, '09:40:00', '10:00:00'),
(64, 4, '10:00:00', '10:20:00'),
(65, 4, '10:20:00', '10:40:00'),
(66, 4, '10:40:00', '11:00:00'),
(67, 4, '11:00:00', '11:20:00'),
(68, 4, '11:20:00', '11:40:00'),
(69, 4, '11:40:00', '12:00:00'),
(70, 4, '12:00:00', '12:20:00'),
(71, 4, '12:20:00', '12:40:00'),
(72, 4, '12:40:00', '13:00:00'),
(73, 4, '13:00:00', '13:20:00'),
(74, 4, '13:20:00', '13:40:00'),
(75, 4, '13:40:00', '14:00:00'),
(76, 4, '14:00:00', '14:20:00'),
(77, 4, '14:20:00', '14:40:00'),
(78, 4, '14:40:00', '15:00:00'),
(79, 4, '15:00:00', '15:20:00'),
(80, 4, '15:20:00', '15:40:00'),
(81, 4, '15:40:00', '16:00:00'),
(82, 4, '16:00:00', '16:20:00'),
(83, 4, '16:20:00', '16:40:00'),
(84, 4, '16:40:00', '17:00:00'),
(85, 4, '17:00:00', '17:20:00'),
(86, 4, '17:20:00', '17:40:00'),
(87, 4, '17:40:00', '18:00:00'),
(88, 4, '18:00:00', '18:20:00'),
(89, 4, '18:20:00', '18:40:00'),
(90, 4, '18:40:00', '19:00:00'),
(91, 5, '09:00:00', '09:20:00'),
(92, 5, '09:20:00', '09:40:00'),
(93, 5, '09:40:00', '10:00:00'),
(94, 5, '10:00:00', '10:20:00'),
(95, 5, '10:20:00', '10:40:00'),
(96, 5, '10:40:00', '11:00:00'),
(97, 5, '11:00:00', '11:20:00'),
(98, 5, '11:20:00', '11:40:00'),
(99, 5, '11:40:00', '12:00:00'),
(100, 5, '12:00:00', '12:20:00'),
(101, 5, '12:20:00', '12:40:00'),
(102, 5, '12:40:00', '13:00:00'),
(103, 5, '13:00:00', '13:20:00'),
(104, 5, '13:20:00', '13:40:00'),
(105, 5, '13:40:00', '14:00:00'),
(106, 5, '14:00:00', '14:20:00'),
(107, 5, '14:20:00', '14:40:00'),
(108, 5, '14:40:00', '15:00:00'),
(109, 5, '15:00:00', '15:20:00'),
(110, 5, '15:20:00', '15:40:00'),
(111, 5, '15:40:00', '16:00:00'),
(112, 5, '16:00:00', '16:20:00'),
(113, 5, '16:20:00', '16:40:00'),
(114, 5, '16:40:00', '17:00:00'),
(115, 5, '17:00:00', '17:20:00'),
(116, 5, '17:20:00', '17:40:00'),
(117, 5, '17:40:00', '18:00:00'),
(118, 5, '18:00:00', '18:20:00'),
(119, 5, '18:20:00', '18:40:00'),
(120, 5, '18:40:00', '19:00:00'),
(121, 6, '09:00:00', '09:20:00'),
(122, 6, '09:20:00', '09:40:00'),
(123, 6, '09:40:00', '10:00:00'),
(124, 6, '10:00:00', '10:20:00'),
(125, 6, '10:20:00', '10:40:00'),
(126, 6, '10:40:00', '11:00:00'),
(127, 6, '11:00:00', '11:20:00'),
(128, 6, '11:20:00', '11:40:00'),
(129, 6, '11:40:00', '12:00:00'),
(130, 6, '12:00:00', '12:20:00'),
(131, 6, '12:20:00', '12:40:00'),
(132, 6, '12:40:00', '13:00:00'),
(133, 6, '13:00:00', '13:20:00'),
(134, 6, '13:20:00', '13:40:00'),
(135, 6, '13:40:00', '14:00:00'),
(136, 6, '14:00:00', '14:20:00'),
(137, 6, '14:20:00', '14:40:00'),
(138, 6, '14:40:00', '15:00:00'),
(139, 6, '15:00:00', '15:20:00'),
(140, 6, '15:20:00', '15:40:00'),
(141, 6, '15:40:00', '16:00:00'),
(142, 6, '16:00:00', '16:20:00'),
(143, 6, '16:20:00', '16:40:00'),
(144, 6, '16:40:00', '17:00:00'),
(145, 6, '17:00:00', '17:20:00'),
(146, 6, '17:20:00', '17:40:00'),
(147, 6, '17:40:00', '18:00:00'),
(148, 6, '18:00:00', '18:20:00'),
(149, 6, '18:20:00', '18:40:00'),
(150, 6, '18:40:00', '19:00:00'),
(151, 7, '09:00:00', '09:20:00'),
(152, 7, '09:20:00', '09:40:00'),
(153, 7, '09:40:00', '10:00:00'),
(154, 7, '10:00:00', '10:20:00'),
(155, 7, '10:20:00', '10:40:00'),
(156, 7, '10:40:00', '11:00:00'),
(157, 7, '11:00:00', '11:20:00'),
(158, 7, '11:20:00', '11:40:00'),
(159, 7, '11:40:00', '12:00:00'),
(160, 7, '12:00:00', '12:20:00'),
(161, 7, '12:20:00', '12:40:00'),
(162, 7, '12:40:00', '13:00:00'),
(163, 7, '13:00:00', '13:20:00'),
(164, 7, '13:20:00', '13:40:00'),
(165, 7, '13:40:00', '14:00:00'),
(166, 7, '14:00:00', '14:20:00'),
(167, 7, '14:20:00', '14:40:00'),
(168, 7, '14:40:00', '15:00:00'),
(169, 7, '15:00:00', '15:20:00'),
(170, 7, '15:20:00', '15:40:00'),
(171, 7, '15:40:00', '16:00:00'),
(172, 7, '16:00:00', '16:20:00'),
(173, 7, '16:20:00', '16:40:00'),
(174, 7, '16:40:00', '17:00:00'),
(175, 7, '17:00:00', '17:20:00'),
(176, 7, '17:20:00', '17:40:00'),
(177, 7, '17:40:00', '18:00:00'),
(178, 7, '18:00:00', '18:20:00'),
(179, 7, '18:20:00', '18:40:00'),
(180, 7, '18:40:00', '19:00:00');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_perfil`
--

CREATE TABLE `tab_perfil` (
  `id` int(11) NOT NULL,
  `nome` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_perfil`
--

TRUNCATE TABLE `tab_perfil`;
--
-- Despejando dados para a tabela `tab_perfil`
--

INSERT INTO `tab_perfil` (`id`, `nome`) VALUES
(1, 'Administrador'),
(3, 'Barbeiro'),
(4, 'Cliente'),
(2, 'Recepcionista');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_perfil_permissao`
--

CREATE TABLE `tab_perfil_permissao` (
  `perfil_id` int(11) NOT NULL,
  `permissao_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_perfil_permissao`
--

TRUNCATE TABLE `tab_perfil_permissao`;
--
-- Despejando dados para a tabela `tab_perfil_permissao`
--

INSERT INTO `tab_perfil_permissao` (`perfil_id`, `permissao_id`) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(1, 3),
(3, 3),
(1, 4),
(2, 4),
(1, 5),
(2, 5),
(1, 6),
(1, 7),
(1, 8),
(2, 8);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_permissao`
--

CREATE TABLE `tab_permissao` (
  `id` int(11) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_permissao`
--

TRUNCATE TABLE `tab_permissao`;
--
-- Despejando dados para a tabela `tab_permissao`
--

INSERT INTO `tab_permissao` (`id`, `nome`, `descricao`) VALUES
(1, 'AGENDAR_HORARIO', 'Permite que o cliente ou recepcionista crie um agendamento'),
(2, 'CANCELAR_AGENDAMENTO', 'Permite cancelar um horário agendado'),
(3, 'VISUALIZAR_PROPRIA_AGENDA', 'Permite ao barbeiro ver apenas os seus atendimentos'),
(4, 'GERENCIAR_TODAS_AGENDAS', 'Permite à recepção/adm mover e organizar horários de todos'),
(5, 'GERENCIAR_CLIENTES', 'Permite cadastrar, editar ou bloquear perfis de clientes'),
(6, 'GERENCIAR_SERVICOS', 'Permite alterar preços e cadastrar novos serviços (Cabelo, Barba, etc)'),
(7, 'VISUALIZAR_FATURAMENTO', 'Permite ver relatórios financeiros e comissões da equipe'),
(8, 'GERENCIAR_ESTOQUE', 'Permite dar entrada e saída em produtos (Pomadas, Shampoos)');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_produto`
--

CREATE TABLE `tab_produto` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(150) NOT NULL,
  `descricao` text DEFAULT NULL,
  `preco` decimal(10,2) NOT NULL,
  `estoque` int(11) DEFAULT 0,
  `marca` varchar(100) DEFAULT NULL,
  `categoria` varchar(50) DEFAULT NULL,
  `data_cadastro` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_produto`
--

TRUNCATE TABLE `tab_produto`;
--
-- Despejando dados para a tabela `tab_produto`
--

INSERT INTO `tab_produto` (`id`, `nome`, `descricao`, `preco`, `estoque`, `marca`, `categoria`, `data_cadastro`) VALUES
(1, 'Pomada Modeladora', 'Efeito Matte de alta fixação', 45.90, 20, 'Barba de Respeito', 'Estilização', '2026-05-07 19:28:21'),
(2, 'Óleo Vikings', 'Hidratação profunda para fios rebeldes', 32.00, 15, 'Vikings', 'Cuidado Facial', '2026-05-07 19:28:21'),
(3, 'Shampoo Ice', 'Sensação refrescante e limpeza profunda', 28.50, 10, 'QOD Barber Shop', 'Limpeza', '2026-05-07 19:28:21');

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_profissional`
--

CREATE TABLE `tab_profissional` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `sobrenome` varchar(100) DEFAULT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `cpf` varchar(14) DEFAULT NULL,
  `observacao` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_profissional`
--

TRUNCATE TABLE `tab_profissional`;
--
-- Despejando dados para a tabela `tab_profissional`
--

INSERT INTO `tab_profissional` (`id`, `nome`, `sobrenome`, `telefone`, `cpf`, `observacao`) VALUES
(2, 'Carlos', 'Tesoura', '(11) 99999-1111', NULL, NULL),
(9, 'Tiago (Especialista em Fade)', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_profissional_endereco`
--

CREATE TABLE `tab_profissional_endereco` (
  `profissional_id` bigint(20) NOT NULL,
  `endereco_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_profissional_endereco`
--

TRUNCATE TABLE `tab_profissional_endereco`;
--
-- Despejando dados para a tabela `tab_profissional_endereco`
--

INSERT INTO `tab_profissional_endereco` (`profissional_id`, `endereco_id`) VALUES
(2, 3),
(2, 4);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_servico`
--

CREATE TABLE `tab_servico` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `preco` decimal(10,2) NOT NULL,
  `duracao` int(11) DEFAULT NULL,
  `tipo` varchar(50) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT 1,
  `imagem` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_servico`
--

TRUNCATE TABLE `tab_servico`;
--
-- Despejando dados para a tabela `tab_servico`
--

INSERT INTO `tab_servico` (`id`, `nome`, `descricao`, `preco`, `duracao`, `tipo`, `ativo`, `imagem`) VALUES
(1, 'Corte de Cabelo Masculino', 'Promocional', 45.00, 30, 'Tradicional', 1, '/img/Corte-cabelo-masculino.jpeg'),
(2, 'Barba Completa (Toalha Quente)', 'O mais pedido', 35.00, 20, 'Tradicional', 1, '/img/photo-1621605815971-fbc98d665033.jpeg'),
(3, 'Corte moicano', 'Corte estilo moicano', 130.00, 90, 'Exótico', 1, NULL),
(4, 'Trança rastafari', 'Estilo cultural', 230.00, 200, 'Cultural', 1, NULL),
(7, 'Combo (Cabelo + Barba)', 'Combo', 98.00, 110, 'Mais procurado', 1, NULL),
(8, 'Corte Infantil', 'Infanto', 45.00, 40, 'Infanto-Juvenil', 1, NULL);

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_usuario`
--

CREATE TABLE `tab_usuario` (
  `id` bigint(20) NOT NULL,
  `perfil_id` bigint(20) DEFAULT NULL,
  `nome_usuario` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `status` tinyint(1) DEFAULT 1,
  `token` varchar(255) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `data_cadastro` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Tabela truncada antes do insert `tab_usuario`
--

TRUNCATE TABLE `tab_usuario`;
--
-- Despejando dados para a tabela `tab_usuario`
--

INSERT INTO `tab_usuario` (`id`, `perfil_id`, `nome_usuario`, `email`, `senha`, `status`, `token`, `ip`, `data_cadastro`) VALUES
(2, 3, 'barbeiro1', 'carlos@email.com', '123', 1, NULL, NULL, '2026-05-07 19:28:21'),
(3, 4, 'cliente1', 'marcos@email.com', '{sha256}pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', 1, 'byscKmvPh87emtl7fAjYKJHkOFfzPlYH83TDsZtX372c0lmEVkgfzKALwGjCBT9S', '192.168.0.1', '2026-05-07 19:28:21'),
(4, 1, 'Pedro123', 'pedro.cefas@yahoo.com.br', '4321', 1, '111', NULL, '2026-05-13 18:35:00'),
(6, 1, 'Teste', 'teste@teste.com', '123456', 1, NULL, NULL, '2026-05-25 18:45:20'),
(7, 1, 'Gabriel', 'gabriel@admin.com', '{sha256}CsTZVEv8fvog4F4xISxq3jbQHsrxfStz9x6Yd0vPWvo=', 1, NULL, NULL, '2026-06-06 16:57:50'),
(8, 1, 'Wallace', 'wallace@admin.com', '{sha256}UfLgCc5Yfrh7z7g4eCFkVwhlFSGPuNjMGB048x7OjTw=', 1, NULL, NULL, '2026-06-06 18:11:23'),
(9, 3, 'tiago', 'tiago.3554@gmail.com', '{sha256}jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 1, NULL, NULL, '2026-06-11 19:56:53');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `permissoes`
--
ALTER TABLE `permissoes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`);

--
-- Índices de tabela `tab_agenda`
--
ALTER TABLE `tab_agenda`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_agenda_cliente` (`cliente_id`),
  ADD KEY `fk_agenda_profissional` (`profissional_id`);

--
-- Índices de tabela `tab_agendamento`
--
ALTER TABLE `tab_agendamento`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_agendamento_grade_horarios` (`grade_horarios_id`);

--
-- Índices de tabela `tab_agenda_servico`
--
ALTER TABLE `tab_agenda_servico`
  ADD PRIMARY KEY (`agenda_id`,`servico_id`),
  ADD KEY `fk_link_servico` (`servico_id`);

--
-- Índices de tabela `tab_cliente`
--
ALTER TABLE `tab_cliente`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cpf` (`cpf`);

--
-- Índices de tabela `tab_cliente_endereco`
--
ALTER TABLE `tab_cliente_endereco`
  ADD PRIMARY KEY (`cliente_id`,`endereco_id`),
  ADD KEY `fk_link_endereco` (`endereco_id`);

--
-- Índices de tabela `tab_dias_funcionamento`
--
ALTER TABLE `tab_dias_funcionamento`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_endereco`
--
ALTER TABLE `tab_endereco`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_grade_horarios`
--
ALTER TABLE `tab_grade_horarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `dia_funcionamento_id` (`dia_funcionamento_id`);

--
-- Índices de tabela `tab_perfil`
--
ALTER TABLE `tab_perfil`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`);

--
-- Índices de tabela `tab_perfil_permissao`
--
ALTER TABLE `tab_perfil_permissao`
  ADD PRIMARY KEY (`perfil_id`,`permissao_id`),
  ADD KEY `permissao_id` (`permissao_id`);

--
-- Índices de tabela `tab_permissao`
--
ALTER TABLE `tab_permissao`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`);

--
-- Índices de tabela `tab_produto`
--
ALTER TABLE `tab_produto`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_profissional`
--
ALTER TABLE `tab_profissional`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cpf` (`cpf`);

--
-- Índices de tabela `tab_profissional_endereco`
--
ALTER TABLE `tab_profissional_endereco`
  ADD PRIMARY KEY (`profissional_id`,`endereco_id`),
  ADD KEY `fk_link_end_prof` (`endereco_id`);

--
-- Índices de tabela `tab_servico`
--
ALTER TABLE `tab_servico`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_usuario`
--
ALTER TABLE `tab_usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome_usuario` (`nome_usuario`),
  ADD UNIQUE KEY `email_unique` (`email`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `permissoes`
--
ALTER TABLE `permissoes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `tab_agenda`
--
ALTER TABLE `tab_agenda`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de tabela `tab_agendamento`
--
ALTER TABLE `tab_agendamento`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT de tabela `tab_dias_funcionamento`
--
ALTER TABLE `tab_dias_funcionamento`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `tab_endereco`
--
ALTER TABLE `tab_endereco`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de tabela `tab_grade_horarios`
--
ALTER TABLE `tab_grade_horarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=181;

--
-- AUTO_INCREMENT de tabela `tab_perfil`
--
ALTER TABLE `tab_perfil`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `tab_permissao`
--
ALTER TABLE `tab_permissao`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `tab_produto`
--
ALTER TABLE `tab_produto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de tabela `tab_servico`
--
ALTER TABLE `tab_servico`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `tab_usuario`
--
ALTER TABLE `tab_usuario`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `tab_agenda`
--
ALTER TABLE `tab_agenda`
  ADD CONSTRAINT `fk_agenda_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `tab_cliente` (`id`),
  ADD CONSTRAINT `fk_agenda_profissional` FOREIGN KEY (`profissional_id`) REFERENCES `tab_profissional` (`id`);

--
-- Restrições para tabelas `tab_agendamento`
--
ALTER TABLE `tab_agendamento`
  ADD CONSTRAINT `fk_agendamento_grade_horarios` FOREIGN KEY (`grade_horarios_id`) REFERENCES `tab_grade_horarios` (`id`) ON DELETE SET NULL;

--
-- Restrições para tabelas `tab_agenda_servico`
--
ALTER TABLE `tab_agenda_servico`
  ADD CONSTRAINT `fk_link_agenda` FOREIGN KEY (`agenda_id`) REFERENCES `tab_agenda` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_link_servico` FOREIGN KEY (`servico_id`) REFERENCES `tab_servico` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `tab_cliente`
--
ALTER TABLE `tab_cliente`
  ADD CONSTRAINT `fk_cliente_usuario` FOREIGN KEY (`id`) REFERENCES `tab_usuario` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `tab_cliente_endereco`
--
ALTER TABLE `tab_cliente_endereco`
  ADD CONSTRAINT `fk_link_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `tab_cliente` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_link_endereco` FOREIGN KEY (`endereco_id`) REFERENCES `tab_endereco` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `tab_grade_horarios`
--
ALTER TABLE `tab_grade_horarios`
  ADD CONSTRAINT `tab_grade_horarios_ibfk_1` FOREIGN KEY (`dia_funcionamento_id`) REFERENCES `tab_dias_funcionamento` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `tab_perfil_permissao`
--
ALTER TABLE `tab_perfil_permissao`
  ADD CONSTRAINT `tab_perfil_permissao_ibfk_1` FOREIGN KEY (`perfil_id`) REFERENCES `tab_perfil` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tab_perfil_permissao_ibfk_2` FOREIGN KEY (`permissao_id`) REFERENCES `tab_permissao` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `tab_profissional`
--
ALTER TABLE `tab_profissional`
  ADD CONSTRAINT `fk_profissional_usuario` FOREIGN KEY (`id`) REFERENCES `tab_usuario` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `tab_profissional_endereco`
--
ALTER TABLE `tab_profissional_endereco`
  ADD CONSTRAINT `fk_link_end_prof` FOREIGN KEY (`endereco_id`) REFERENCES `tab_endereco` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_link_prof` FOREIGN KEY (`profissional_id`) REFERENCES `tab_profissional` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
