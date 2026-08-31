package com.gwj.tools;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.gwj.model.domain.factory.SchemaValidator;

/**
 * Ferramenta de Diagnóstico e Checagem de Ambiente Pré-Execução.
 * Verifica Java 21+, portas de rede, conexão com MariaDB/MySQL,
 * existência do banco, tabelas essenciais e integridade das entidades.
 */
public class CheckEnvironment {

    // Cores ANSI para saída no terminal
    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";

    private static int totalChecks = 0;
    private static int passedChecks = 0;
    private static int warningChecks = 0;
    private static int failedChecks = 0;
    private static final List<String> remediationTips = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println();
        System.out.println(CYAN + BOLD + "=================================================================" + RESET);
        System.out.println(CYAN + BOLD + "   🔍 TGOS / GWJ - DIAGNÓSTICO E CHECAGEM DE AMBIENTE (PRÉ-VOO)  " + RESET);
        System.out.println(CYAN + BOLD + "=================================================================" + RESET);
        System.out.println("Iniciando verificação dos recursos necessários para a aplicação...\n");

        // 1. Checagem do Java
        checkJavaVersion();

        // 2. Leitura e validação de application.properties
        Properties props = checkApplicationProperties();

        // 3. Checagem da porta Web
        checkWebPort(props);

        // 4. Checagem do serviço de Banco de Dados (Host & Porta)
        String dbUrl = props != null ? props.getProperty("spring.datasource.url", "jdbc:mariadb://localhost:3306/gwj2") : "jdbc:mariadb://localhost:3306/gwj2";
        String dbUser = props != null ? props.getProperty("spring.datasource.username", "desenvolvedor") : "desenvolvedor";
        String dbPass = props != null ? props.getProperty("spring.datasource.password", "b2#FbXPQTu4FYw") : "b2#FbXPQTu4FYw";
        String tablePrefix = props != null ? props.getProperty("app.database.prefix", "tab_") : "tab_";

        ParsedDbUrl parsed = parseJdbcUrl(dbUrl);
        checkDatabaseSocket(parsed.host, parsed.port);

        // 5. Checagem de Conexão JDBC e Autenticação
        Connection conn = checkDatabaseAuthAndConnection(dbUrl, dbUser, dbPass, parsed);

        // 6. Checagem de Tabelas e Dados Essenciais
        if (conn != null) {
            checkDatabaseTablesAndData(conn, parsed.databaseName, tablePrefix);
            try {
                conn.close();
            } catch (Exception ignored) {}
        }

        // 7. Checagem de Entidades e Mapeamentos
        checkEntityMapping();

        // Resumo Final
        printSummary();

        if (failedChecks > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static void checkJavaVersion() {
        totalChecks++;
        String versionStr = System.getProperty("java.version");
        String vendor = System.getProperty("java.vendor");
        int majorVersion = getJavaMajorVersion(versionStr);

        System.out.print(BOLD + "[1/6] Versão do Java (JDK): " + RESET);
        if (majorVersion >= 21) {
            System.out.println(GREEN + "✅ OK! Java " + majorVersion + " (" + versionStr + " - " + vendor + ")" + RESET);
            passedChecks++;
        } else {
            System.out.println(RED + "❌ FALHA! Detectado Java " + majorVersion + " (" + versionStr + ")" + RESET);
            failedChecks++;
            remediationTips.add("O Spring Boot 3.2.5 e este projeto exigem o Java 21 LTS ou superior. Instale o JDK 21 e configure o JAVA_HOME.");
        }
    }

    private static Properties checkApplicationProperties() {
        totalChecks++;
        System.out.print(BOLD + "[2/6] Arquivo de Configuração (application.properties): " + RESET);
        Properties props = new Properties();
        try (InputStream is = CheckEnvironment.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
                System.out.println(GREEN + "✅ OK! Encontrado no classpath com " + props.size() + " propriedades." + RESET);
                passedChecks++;
                return props;
            } else {
                System.out.println(YELLOW + "⚠️ AVISO! Arquivo application.properties não encontrado no classpath." + RESET);
                warningChecks++;
                remediationTips.add("Verifique se o arquivo 'src/main/resources/application.properties' existe no projeto.");
                return null;
            }
        } catch (Exception e) {
            System.out.println(RED + "❌ ERRO ao carregar application.properties: " + e.getMessage() + RESET);
            failedChecks++;
            return null;
        }
    }

    private static void checkWebPort(Properties props) {
        totalChecks++;
        int port = 8089;
        if (props != null && props.getProperty("server.port") != null) {
            try {
                port = Integer.parseInt(props.getProperty("server.port").trim());
            } catch (NumberFormatException ignored) {}
        }

        System.out.print(BOLD + "[3/6] Porta Web (" + port + "): " + RESET);
        try (ServerSocket socket = new ServerSocket(port)) {
            // Se conseguiu abrir, a porta está livre
            System.out.println(GREEN + "✅ OK! A porta " + port + " está livre para o servidor Spring Boot." + RESET);
            passedChecks++;
        } catch (Exception e) {
            System.out.println(YELLOW + "⚠️ OCUPADA! A porta " + port + " já está em uso por outro processo." + RESET);
            warningChecks++;
            remediationTips.add("A porta " + port + " está em uso. Se for outro servidor Spring aberto, finalize-o, ou troque a propriedade 'server.port' no application.properties.");
        }
    }

    private static void checkDatabaseSocket(String host, int port) {
        totalChecks++;
        System.out.print(BOLD + "[4/6] Serviço de Banco de Dados (Host: " + host + ", Porta: " + port + "): " + RESET);
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2500);
            System.out.println(GREEN + "✅ OK! Porta " + port + " respondendo ativamente." + RESET);
            passedChecks++;
        } catch (Exception e) {
            System.out.println(RED + "❌ INACESSÍVEL! Não foi possível conectar ao banco em " + host + ":" + port + RESET);
            failedChecks++;
            remediationTips.add("O serviço MariaDB/MySQL não está rodando na porta " + port + ". Inicie o MySQL/MariaDB (ex: serviço do Windows/Linux, XAMPP ou docker compose up -d).");
        }
    }

    private static Connection checkDatabaseAuthAndConnection(String dbUrl, String dbUser, String dbPass, ParsedDbUrl parsed) {
        totalChecks++;
        System.out.print(BOLD + "[5/6] Conexão e Autenticação JDBC: " + RESET);
        
        // Garante carregamento dos drivers
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception e1) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (Exception ignored) {}
        }

        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            System.out.println(GREEN + "✅ OK! Conectado com sucesso com usuário '" + dbUser + "' no banco '" + parsed.databaseName + "'." + RESET);
            passedChecks++;
            return conn;
        } catch (Exception e) {
            System.out.println(RED + "❌ FALHA de Conexão! (" + e.getMessage() + ")" + RESET);
            failedChecks++;

            // Diagnóstico específico do erro de BD
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("access denied") || msg.contains("using password")) {
                remediationTips.add("Usuário '" + dbUser + "' ou senha incorretos. Execute o script 'gwj_criacao_usuario.sql' no seu MySQL/MariaDB como root.");
            } else if (msg.contains("unknown database")) {
                remediationTips.add("O banco de dados '" + parsed.databaseName + "' não existe. Crie-o executando 'gwj_criacao_usuario.sql' ou 'CREATE DATABASE " + parsed.databaseName + ";'.");
            } else {
                remediationTips.add("Verifique se as configurações em application.properties conferem com o seu banco local.");
            }
            return null;
        }
    }

    private static void checkDatabaseTablesAndData(Connection conn, String dbName, String prefix) {
        totalChecks++;
        System.out.print(BOLD + "[6/6] Verificação de Tabelas e Schema: " + RESET);

        Set<String> existingTables = new HashSet<>();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(dbName, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    existingTables.add(rs.getString("TABLE_NAME").toLowerCase());
                }
            }

            // Lista de tabelas esperadas no projeto
            List<String> expectedTables = Arrays.asList(
                prefix + "usuario",
                prefix + "cliente",
                prefix + "produto",
                prefix + "servico",
                prefix + "profissional",
                prefix + "setting",
                prefix + "pedidos",
                prefix + "itens_pedido",
                prefix + "endereco"
            );

            List<String> missingTables = new ArrayList<>();
            for (String table : expectedTables) {
                if (!existingTables.contains(table.toLowerCase())) {
                    missingTables.add(table);
                }
            }

            if (existingTables.isEmpty()) {
                System.out.println(RED + "❌ Nenhuma tabela encontrada no banco '" + dbName + "'!" + RESET);
                failedChecks++;
                remediationTips.add("O banco está vazio. Importe o arquivo 'gwj5.sql' (ou 'gwj_criacao_usuario.sql' + dump) para criar a estrutura das tabelas.");
            } else if (!missingTables.isEmpty()) {
                System.out.println(YELLOW + "⚠️ Banco contém " + existingTables.size() + " tabelas, mas faltam algumas esperadas: " + missingTables + RESET);
                warningChecks++;
                remediationTips.add("Tabelas ausentes: " + missingTables + ". Execute os scripts SQL de migração ou inicialize o servidor para que o SchemaValidator crie tabelas dinâmicas.");
            } else {
                // Checa quantidade de usuários cadastrados
                int userCount = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `" + prefix + "usuario`")) {
                    if (rs.next()) {
                        userCount = rs.getInt(1);
                    }
                } catch (Exception ignored) {}

                System.out.println(GREEN + "✅ OK! " + existingTables.size() + " tabelas encontradas (incluindo " + userCount + " usuário(s) cadastrado(s))." + RESET);
                passedChecks++;
            }

        } catch (Exception e) {
            System.out.println(RED + "❌ Erro ao consultar metadados do banco: " + e.getMessage() + RESET);
            failedChecks++;
        }
    }

    private static void checkEntityMapping() {
        System.out.print(BOLD + "[+] Validação Arquitetural de Entidades: " + RESET);
        try {
            SchemaValidator.validateAllEntities();
        } catch (Exception e) {
            System.out.println(RED + "❌ Erro na validação de entidades: " + e.getMessage() + RESET);
            failedChecks++;
        }
    }

    private static void printSummary() {
        System.out.println();
        System.out.println(CYAN + BOLD + "=================================================================" + RESET);
        System.out.println(CYAN + BOLD + "                       RESULTADO DO DIAGNÓSTICO                  " + RESET);
        System.out.println(CYAN + BOLD + "=================================================================" + RESET);
        System.out.println("  Checagens Realizadas: " + totalChecks);
        System.out.println("  " + GREEN + "✅ Sucessos: " + passedChecks + RESET);
        System.out.println("  " + YELLOW + "⚠️ Avisos:   " + warningChecks + RESET);
        System.out.println("  " + RED + "❌ Falhas:   " + failedChecks + RESET);
        System.out.println(CYAN + "-----------------------------------------------------------------" + RESET);

        if (failedChecks == 0) {
            System.out.println(GREEN + BOLD + "🎉 PARABÉNS! Seu ambiente está 100% pronto para rodar o projeto!" + RESET);
            System.out.println("Para iniciar a aplicação, execute:");
            System.out.println(CYAN + "   ./mvnw spring-boot:run" + RESET + " (Linux/macOS) ou " + CYAN + "mvnw.cmd spring-boot:run" + RESET + " (Windows)");
            System.out.println("   Ou execute a classe " + CYAN + "com.gwj.StartApplication" + RESET + " na sua IDE.");
        } else {
            System.out.println(RED + BOLD + "⚠️ FORAM ENCONTRADAS PENDÊNCIAS NO SEU AMBIENTE." + RESET);
            System.out.println("\n" + BOLD + "📌 DICAS PARA CORREÇÃO:" + RESET);
            for (int i = 0; i < remediationTips.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + remediationTips.get(i));
            }
            System.out.println();
            System.out.println("💡 Após realizar as correções acima, rode este diagnóstico novamente.");
        }
        System.out.println(CYAN + BOLD + "=================================================================" + RESET);
        System.out.println();
    }

    private static int getJavaMajorVersion(String version) {
        if (version == null) return 0;
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
            int dash = version.indexOf("-");
            if (dash != -1) {
                version = version.substring(0, dash);
            }
        }
        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static class ParsedDbUrl {
        String host = "localhost";
        int port = 3306;
        String databaseName = "gwj2";
    }

    private static ParsedDbUrl parseJdbcUrl(String jdbcUrl) {
        ParsedDbUrl parsed = new ParsedDbUrl();
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")) {
            return parsed;
        }
        try {
            // Remove prefixo jdbc:
            String cleanUri = jdbcUrl.substring(5);
            // Substitui mariadb: ou mysql: por http: para aproveitar o parser de URI
            cleanUri = cleanUri.replaceFirst("^[a-zA-Z0-9_-]+://", "http://");
            URI uri = new URI(cleanUri);
            if (uri.getHost() != null) {
                parsed.host = uri.getHost();
            }
            if (uri.getPort() > 0) {
                parsed.port = uri.getPort();
            }
            if (uri.getPath() != null && uri.getPath().length() > 1) {
                String path = uri.getPath().substring(1);
                int queryIdx = path.indexOf('?');
                parsed.databaseName = queryIdx != -1 ? path.substring(0, queryIdx) : path;
            }
        } catch (Exception ignored) {}
        return parsed;
    }
}
