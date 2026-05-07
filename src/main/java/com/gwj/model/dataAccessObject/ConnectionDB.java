package com.gwj.model.dataAccessObject;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import com.gwj.AppConfig;

public class ConnectionDB {

    private static ConnectionDB instance;

    private ConnectionDB() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar Driver JDBC", e);
        }
    }

    public static synchronized ConnectionDB getInstance() {
        if (instance == null) {
            instance = new ConnectionDB();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DB_URL, AppConfig.DB_USER, AppConfig.DB_PASS);
    }
}
