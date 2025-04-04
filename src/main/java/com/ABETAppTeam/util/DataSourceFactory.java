package com.ABETAppTeam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
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

        // Default values if not set
        dbHost = (dbHost != null) ? dbHost : "localhost";
        dbPort = (dbPort != null) ? dbPort : "3306";
        dbName = (dbName != null) ? dbName : "abetapp";
        dbUsername = (dbUsername != null) ? dbUsername : "user"; // Changed from 'root' to match setup.py defaults
        dbPassword = (dbPassword != null) ? dbPassword : "pass"; // Make sure to read password correctly

        // Set up the JDBC URL
        String jdbcUrl = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/" + dbName;

        System.out.println("Connecting to database: " + jdbcUrl + " as user: " + dbUsername);

        // Configure Hikari connection pool
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword); // Ensure the password is being passed
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // Pool configuration
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Create the data source
        dataSource = new HikariDataSource(config);

        // Initialize the database schema if needed
        initializeDatabase();
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