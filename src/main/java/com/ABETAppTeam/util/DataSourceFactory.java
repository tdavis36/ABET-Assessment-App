package com.ABETAppTeam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.*;
import java.util.Properties;

/**
 * Factory class for creating and managing database connections
 */
public class DataSourceFactory {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        // First try to load from environment variables
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        // Load from .env file if environment variables are not set
        if (dbHost == null || dbPort == null || dbName == null || dbUsername == null || dbPassword == null) {
            Properties envProps = new Properties();
            File envFile = new File(".env");
            if (envFile.exists()) {
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("#") && line.contains("=")) {
                            String[] parts = line.split("=", 2);
                            if (parts.length == 2) {
                                envProps.setProperty(parts[0].trim(), parts[1].trim());
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading .env file: " + e.getMessage());
                }
            }

            if (dbHost == null) dbHost = envProps.getProperty("DB_HOST", "localhost");
            if (dbPort == null) dbPort = envProps.getProperty("DB_PORT", "3306");
            if (dbName == null) dbName = envProps.getProperty("DB_NAME", "abetapp");
            if (dbUsername == null) dbUsername = envProps.getProperty("DB_USERNAME", "user");
            if (dbPassword == null) dbPassword = envProps.getProperty("DB_PASSWORD", "");
        }

        // Default values if still not set
        dbHost = (dbHost != null) ? dbHost : "localhost";
        dbPort = (dbPort != null) ? dbPort : "3306";
        dbName = (dbName != null) ? dbName : "abetapp";
        dbUsername = (dbUsername != null) ? dbUsername : "user";
        dbPassword = (dbPassword != null) ? dbPassword : "";

        // Set up the JDBC URL with allowPublicKeyRetrieval=true to avoid SSL issues
        String jdbcUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/" + dbName +
                "?allowPublicKeyRetrieval=true&useSSL=false";

        // Debug output
        System.out.println("Database connection info:");
        System.out.println("URL: " + jdbcUrl);
        System.out.println("User: " + dbUsername);
        System.out.println("Password provided: " + !dbPassword.isEmpty());

        // Configure Hikari connection pool
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // Pool configuration
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        try {
            dataSource = new HikariDataSource(config);
            System.out.println("Database connection pool initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
            // Rethrow the exception but don't terminate the app yet
            throw new RuntimeException("Database connection failed", e);
        }

        // Initialize the database schema if needed
        try {
            initializeDatabase();
        } catch (Exception e) {
            System.err.println("Failed to initialize database schema: " + e.getMessage());
        }
    }

    /**
     * Get the data source
     * @return The data source
     */
    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Initialize the database schema if needed
     */
    private static void initializeDatabase() {
        // Check if essential tables exist
        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            // Try to query the User_Data table
            try {
                stmt.executeQuery("SELECT 1 FROM User_Data LIMIT 1");
                // Table exists, no need to initialize
            } catch (java.sql.SQLException e) {
                // Table doesn't exist, initialize the database
                System.out.println("Database schema not found. Tables will be created automatically by Flyway migrations.");

                // The actual initialization happens through Flyway migrations
                // which are configured in the pom.xml file
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error checking database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close the data source
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}