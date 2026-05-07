package com.gwj;

import java.util.Properties;

public final class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (var is = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Aviso: application.properties não encontrado, usando padrões.");
        }
    }

    // Construtor privado para evitar instanciação
    private AppConfig() {
        throw new UnsupportedOperationException("Esta é uma classe de constantes e não pode ser instanciada");
    }

    // Configurações de Banco (Lidas do arquivo ou com valor padrão)
    public static final String DB_URL          = props.getProperty("spring.datasource.url");
    public static final String DB_USER         = props.getProperty("spring.datasource.username");
    public static final String DB_PASS         = props.getProperty("spring.datasource.password");
    
    // Prefixo das tabelas
    public static final String TABLE_PREFIX    = props.getProperty("app.database.prefix", "table_");
    
    // Importante, pois aponta para o pacote onde as classes de entidade estão:
    public static final String ENTITIES_PATH   = props.getProperty("app.entities.path", "com.gwj.model.domain.entities");
    
    public static final int    LIMITE_USUARIOS = 100;
    public static final double VERSAO          = 1.0;
}
