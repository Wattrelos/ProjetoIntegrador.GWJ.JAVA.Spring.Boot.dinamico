-- 1. Criação do Banco de Dados
CREATE DATABASE IF NOT EXISTS `gwj2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `gwj2`;

-- 2. Configuração de Usuário (conforme application.properties)
CREATE USER IF NOT EXISTS 'desenvolvedor'@'%' IDENTIFIED BY 'b2#FbXPQTu4FYw';
GRANT ALL PRIVILEGES ON `gwj2`.* TO 'desenvolvedor'@'%';
FLUSH PRIVILEGES;