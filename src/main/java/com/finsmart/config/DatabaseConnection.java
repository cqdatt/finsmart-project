package com.finsmart.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Properties dbConfig;
    
    private DatabaseConnection() {
        loadConfig();
    }
    
    private void loadConfig() {
        dbConfig = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find db.properties");
            }
            dbConfig.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database config", e);
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load driver from config
        String driver = dbConfig.getProperty("db.driver");
        String url = dbConfig.getProperty("db.url");
        String username = dbConfig.getProperty("db.username");
        String password = dbConfig.getProperty("db.password");
        
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        
        // ✅ ĐẢM BẢO AUTO-COMMIT
        conn.setAutoCommit(true);
        
        return conn;
    }
}