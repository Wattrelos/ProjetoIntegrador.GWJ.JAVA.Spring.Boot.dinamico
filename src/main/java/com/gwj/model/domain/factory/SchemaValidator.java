package com.gwj.model.domain.factory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import com.gwj.AppConfig;
import com.gwj.model.domain.IEntity;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

public class SchemaValidator {

    public static void validateAllEntities() {
        try {
            String path = AppConfig.ENTITIES_PATH.replaceAll("\\.$", "");
            List<Class<?>> classes = getClassesFromPackage(path);

            for (Class<?> clazz : classes) {
                // 1. IGNORA Interfaces, Classes Abstratas e ENUMS
                if (clazz.isInterface() ||
                    Modifier.isAbstract(clazz.getModifiers()) ||
                    clazz.isEnum()) {
                    continue;
                }

                // NOVA VERIFICAÇÃO: Obrigatoriedade da IEntity
                if (!IEntity.class.isAssignableFrom(clazz)) {
                    throw new RuntimeException(
                        "\n[ERRO DE ARQUITETURA]: A classe '" + clazz.getSimpleName() + 
                        "' está no pacote de entidades mas NÃO implementa 'IEntity'.\n" +
                        "Toda entidade de domínio deve seguir este contrato para o DAO funcionar."
                    );
                }

                // Se passou, valida os nomes dos setters (relação entre entidades)
                validateNamingConvention((Class<? extends IEntity>) clazz);
            }
            System.out.println("✅ Todas as classes em '" + path + "' implementam IEntity e seguem os padrões.");
        } catch (Exception e) {
            System.err.println("❌ Falha na validação: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void validateNamingConvention(Class<? extends IEntity> clazz) {
        // 1. Validação de Campos (Coleções 1:N e N:N)
        for (Field field : clazz.getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                
                // Verifica se tem ao menos uma das duas anotações
                boolean hasOneToMany = field.isAnnotationPresent(OneToMany.class);
                boolean hasManyToMany = field.isAnnotationPresent(ManyToMany.class);

                if (!hasOneToMany && !hasManyToMany) {
                    throw new RuntimeException(
                        "\n[ERRO DE MAPEAMENTO]: Na classe '" + clazz.getSimpleName() + 
                        "', a coleção '" + field.getName() + "' precisa da anotação @OneToMany ou @ManyToMany " +
                        "para que o DAO saiba como carregá-la.");
                }

                // Validação Extra: Garante que a lista tenha um tipo genérico (ex: List<Acessorio>)
                if (!(field.getGenericType() instanceof ParameterizedType)) {
                    throw new RuntimeException(
                        "\n[ERRO DE GENERICO]: A coleção '" + field.getName() + "' na classe '" + 
                        clazz.getSimpleName() + "' deve especificar o tipo. Ex: List<Entidade>.");
                }
            }
        }

        // 2. Validação de Setters (O que já tínhamos antes)
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                if (IEntity.class.isAssignableFrom(paramType) && !method.getName().endsWith("Id")) {
                    // ... erro do sufixo Id ...
                }
            }
        }
    }


    // Método auxiliar para ler as classes do classpath
    private static List<Class<?>> getClassesFromPackage(String packageName) throws Exception {
    String path = packageName.replace('.', '/');
    Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
    List<Class<?>> classes = new ArrayList<>();
    
    while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        // O decode serve para tratar espaços ou caracteres especiais no caminho do diretório
        File directory = new File(java.net.URLDecoder.decode(resource.getFile(), "UTF-8"));
        
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    // REMOVE o ".class" do final
                    String fileName = file.getName().replace(".class", "");
                    // MONTA o nome completo: pacote.Classe (sem barras!)
                    String className = packageName + "." + fileName;
                    
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        System.err.println("Não foi possível carregar: " + className);
                    }
                }
            }
        }
    }
    return classes;
}
    public static void ensureUniqueEmailIndex() {
        try (java.sql.Connection conn = com.gwj.model.dataAccessObject.ConnectionDB.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            try {
                stmt.execute("ALTER TABLE `tab_usuario` ADD UNIQUE KEY `email_unique` (`email`)");
                System.out.println("✅ Índice UNIQUE no campo 'email' criado com sucesso na tabela tab_usuario.");
            } catch (java.sql.SQLException e) {
                // Código SQLState 42000 ou ER_DUP_KEYNAME (1061) indica que o índice já existe
                if (e.getErrorCode() == 1061 || "42000".equals(e.getSQLState())) {
                    System.out.println("ℹ️ O índice UNIQUE no campo 'email' já existe.");
                } else {
                    System.err.println("Aviso ao assegurar índice de e-mail UNIQUE: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter conexão para validação de Schema: " + e.getMessage());
        }
    }

    public static void ensureSettingsTableExists() {
        try (java.sql.Connection conn = com.gwj.model.dataAccessObject.ConnectionDB.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            // Cria a tabela tab_setting se ela não existir
            String createTableSql = "CREATE TABLE IF NOT EXISTS `tab_setting` (" +
                                    "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
                                    "  `chave` VARCHAR(255) NOT NULL UNIQUE," +
                                    "  `valor` TEXT" +
                                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.execute(createTableSql);
            System.out.println("✅ Tabela tab_setting criada ou já existente.");

            // Verifica se a tabela está vazia, se sim, insere os dados padrão da empresa
            try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `tab_setting`")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("ℹ️ Inserindo dados de configuração padrão da empresa...");
                    String insertSql = "INSERT INTO `tab_setting` (`chave`, `valor`) VALUES " +
                                       "('nome', 'Tgo\\'s Barbearia'), " +
                                       "('descricao', 'Tradição e estilo para o homem moderno. Cuidamos do seu visual com excelência, usando as melhores técnicas e produtos para você se sentir incrível.'), " +
                                       "('endereco_curto', 'Rua Principal, 123 - Centro'), " +
                                       "('endereco_completo', 'Rua Principal, 123, Centro\\nCidade, Estado - CEP 12345-678'), " +
                                       "('telefone', '(11) 99999-9999'), " +
                                       "('email', 'contato@tgosbarbearia.com'), " +
                                       "('horarios', 'Segunda a Sábado\\nDas 09:00 às 20:00'), " +
                                       "('sobre_titulo', 'Tradição e Estilo'), " +
                                       "('sobre_texto1', 'Fundada com o propósito de resgatar a clássica experiência de ir ao barbeiro, nossa Barbearia une o ambiente nostálgico com as mais modernas técnicas de visagismo masculino.'), " +
                                       "('sobre_texto2', 'Acreditamos que um bom corte de cabelo e uma barba bem feita são fundamentais para a autoestima do homem contemporâneo. Aqui, cada cliente é tratado como um amigo. Sente-se, tome um café ou uma cerveja gelada e deixe o visual por nossa conta.'), " +
                                       "('sobre_imagem', 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800&q=80'), " +
                                       "('logo_alt', 'Fatec-FV'), " +
                                       "('logo_url', '/img/logo.png');";
                    stmt.execute(insertSql);
                    System.out.println("✅ Configurações padrão da empresa inseridas com sucesso.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar tabela tab_setting: " + e.getMessage());
        }
    }

    public static void ensureProductImageColumnAndDataExist() {
        try (java.sql.Connection conn = com.gwj.model.dataAccessObject.ConnectionDB.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            // 1. Adiciona a coluna imagem se ela não existir
            try {
                stmt.execute("ALTER TABLE `tab_produto` ADD COLUMN `imagem` VARCHAR(255) DEFAULT NULL");
                System.out.println("✅ Coluna 'imagem' adicionada com sucesso na tabela tab_produto.");
            } catch (java.sql.SQLException e) {
                // Código 1060 indica que a coluna já existe
                if (e.getErrorCode() == 1060 || "42S21".equals(e.getSQLState())) {
                    System.out.println("ℹ️ A coluna 'imagem' já existe na tabela tab_produto.");
                } else {
                    System.err.println("Aviso ao adicionar coluna imagem: " + e.getMessage());
                }
            }

            // 2. Insere URLs de imagem padrão para os produtos iniciais se estiverem vazios/nulos
            try {
                stmt.execute("UPDATE `tab_produto` SET `imagem` = 'https://images.unsplash.com/photo-1599305090598-fe179d501227?w=600&q=80' WHERE `id` = 1 AND (`imagem` IS NULL OR `imagem` = '')");
                stmt.execute("UPDATE `tab_produto` SET `imagem` = 'https://images.unsplash.com/photo-1626015561531-77ec3411190c?w=600&q=80' WHERE `id` = 2 AND (`imagem` IS NULL OR `imagem` = '')");
                stmt.execute("UPDATE `tab_produto` SET `imagem` = 'https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=600&q=80' WHERE `id` = 3 AND (`imagem` IS NULL OR `imagem` = '')");
                System.out.println("✅ URLs de imagem padrão populadas para os produtos iniciais.");
            } catch (java.sql.SQLException e) {
                System.err.println("Aviso ao popular imagens dos produtos: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar coluna de imagem em tab_produto: " + e.getMessage());
        }
    }

    public static void ensureOrderTablesExist() {
        try (java.sql.Connection conn = com.gwj.model.dataAccessObject.ConnectionDB.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            // 1. Criar tab_pedidos se não existir
            String createPedidosSql = "CREATE TABLE IF NOT EXISTS `tab_pedidos` (" +
                                      "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
                                      "  `cliente_id` BIGINT NULL," +
                                      "  `nome_visitante` VARCHAR(100) NULL," +
                                      "  `telefone_visitante` VARCHAR(20) NULL," +
                                      "  `data_pedido` TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                      "  `forma_pagamento` VARCHAR(50) DEFAULT 'Pagamento na Retirada'," +
                                      "  `status` VARCHAR(50) DEFAULT 'Aguardando Retirada'," +
                                      "  `valor_total` DECIMAL(10, 2) NOT NULL," +
                                      "  FOREIGN KEY (`cliente_id`) REFERENCES `tab_cliente`(`id`) ON DELETE SET NULL" +
                                      ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.execute(createPedidosSql);
            System.out.println("✅ Tabela tab_pedidos criada ou já existente.");

            // 2. Criar tab_itens_pedido se não existir
            String createItensSql = "CREATE TABLE IF NOT EXISTS `tab_itens_pedido` (" +
                                    "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
                                    "  `pedido_id` BIGINT NOT NULL," +
                                    "  `produto_id` BIGINT NOT NULL," +
                                    "  `quantidade` INT NOT NULL," +
                                    "  `preco_unitario` DECIMAL(10, 2) NOT NULL," +
                                    "  FOREIGN KEY (`pedido_id`) REFERENCES `tab_pedidos`(`id`) ON DELETE CASCADE" +
                                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.execute(createItensSql);
            System.out.println("✅ Tabela tab_itens_pedido criada ou já existente.");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar tabelas de pedidos: " + e.getMessage());
        }
    }
}

