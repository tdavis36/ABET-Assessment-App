package com.ABETAppTeam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating and managing database connections with enhanced logging
 */
public class DataSourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);
    private static HikariDataSource dataSource;
    private static ScheduledExecutorService connectionPoolMonitor;

    static {
        try {
            initializeDataSource();
            startConnectionPoolMonitoring();
        } catch (Exception e) {
            logger.error("Critical error initializing database connection pool: {}", e.getMessage(), e);
        }
    }

    /**
     * Initialize the data source with connection pooling
     */
    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();

        // Load database configuration
        DatabaseConfig dbConfig = loadDatabaseConfig();

        // Set up the JDBC URL with allowPublicKeyRetrieval=true to avoid SSL issues
        String jdbcUrl = "jdbc:mariadb://" + dbConfig.host + ":" + dbConfig.port + "/" + dbConfig.name +
                "?allowPublicKeyRetrieval=true&useSSL=false";

        // Log database connection info
        logger.info("Initializing database connection pool:");
        logger.info("URL: {}", jdbcUrl);
        logger.info("User: {}", dbConfig.username);
        logger.info("Password provided: {}", !dbConfig.password.isEmpty());

        // Configure Hikari connection pool
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbConfig.username);
        config.setPassword(dbConfig.password);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // Pool configuration with explicit settings for better logging and diagnostics
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        // Add a pool name for better log identification
        config.setPoolName("ABETApp-DB-Pool");

        // Enable metrics for monitoring (used by the pool monitor)
        config.setMetricsTrackerFactory(null); // Using default metrics tracker

        try {
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");

            // Test the connection
            testConnection();
        } catch (HikariPool.PoolInitializationException e) {
            logger.error("Failed to initialize database connection pool: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection failed during initialization", e);
        } catch (Exception e) {
            logger.error("Unexpected error initializing connection pool: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection failed unexpectedly", e);
        }

        // Initialize the database schema if needed
        try {
            initializeDatabase();
        } catch (Exception e) {
            logger.error("Failed to initialize database schema: {}", e.getMessage(), e);
        }
    }

    /**
     * Load database configuration from environment variables or .env file
     *
     * @return Database configuration object
     */
    private static DatabaseConfig loadDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();

        // First try to load from environment variables
        config.host = System.getenv("DB_HOST");
        config.port = System.getenv("DB_PORT");
        config.name = System.getenv("DB_NAME");
        config.username = System.getenv("DB_USERNAME");
        config.password = System.getenv("DB_PASSWORD");

        // Load from .env file if environment variables are not set
        if (config.host == null || config.port == null || config.name == null ||
                config.username == null || config.password == null) {

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

                    logger.debug("Loaded database configuration from .env file");
                } catch (IOException e) {
                    logger.error("Error reading .env file: {}", e.getMessage(), e);
                }
            } else {
                logger.warn("No .env file found at {}. Using default database settings.", envFile.getAbsolutePath());
            }

            // Set configuration from properties if not set from environment variables
            if (config.host == null) config.host = envProps.getProperty("DB_HOST", "localhost");
            if (config.port == null) config.port = envProps.getProperty("DB_PORT", "3306");
            if (config.name == null) config.name = envProps.getProperty("DB_NAME", "abetapp");
            if (config.username == null) config.username = envProps.getProperty("DB_USERNAME", "user");
            if (config.password == null) config.password = envProps.getProperty("DB_PASSWORD", "");
        }

        // Set defaults if still not set
        config.host = (config.host != null && !config.host.isEmpty()) ? config.host : "localhost";
        config.port = (config.port != null && !config.port.isEmpty()) ? config.port : "3306";
        config.name = (config.name != null && !config.name.isEmpty()) ? config.name : "abetapp";
        config.username = (config.username != null && !config.username.isEmpty()) ? config.username : "user";
        config.password = (config.password != null) ? config.password : "";

        return config;
    }

    /**
     * Test the database connection
     */
    private static void testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                logger.info("Database connection test successful");
            } else {
                logger.error("Database connection test failed: Connection is not valid");
            }
        } catch (SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection test failed", e);
        }
    }

    /**
     * Initialize the database schema if needed
     */
    private static void initializeDatabase() {
        // Check if essential tables exist
        try (Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            logger.debug("Testing database schema by checking for User table");

            // Try to query the User table
            try {
                stmt.executeQuery("SELECT 1 FROM User LIMIT 1");
                logger.info("Database schema exists, tables found");
            } catch (SQLException e) {
                // Table doesn't exist, log this info but don't treat as an error
                logger.info("Database schema not found. Tables will be created automatically by Flyway migrations.");

                // The actual initialization happens through Flyway migrations
                // which are configured in the pom.xml file
            }
        } catch (SQLException e) {
            logger.error("Error checking database schema: {}", e.getMessage(), e);
        }
    }

    /**
     * Start monitoring the connection pool
     * This will log connection pool statistics periodically
     */
    private static void startConnectionPoolMonitoring() {
        if (connectionPoolMonitor != null) {
            connectionPoolMonitor.shutdownNow();
        }

        connectionPoolMonitor = Executors.newSingleThreadScheduledExecutor();
        connectionPoolMonitor.scheduleAtFixedRate(() -> {
            try {
                if (dataSource != null) {
                    int active = dataSource.getHikariPoolMXBean().getActiveConnections();
                    int idle = dataSource.getHikariPoolMXBean().getIdleConnections();
                    int total = dataSource.getHikariPoolMXBean().getTotalConnections();

                    // Use our DatabaseLogger for consistent logging
                    DatabaseLogger.logConnectionPoolStatus(active, idle, total);

                    // Log any potential issues
                    if (active == total && total == dataSource.getMaximumPoolSize()) {
                        logger.warn("Connection pool is at maximum capacity. This may indicate a connection leak or insufficient pool size.");
                    }
                }
            } catch (Exception e) {
                logger.error("Error monitoring connection pool: {}", e.getMessage());
            }
        }, 1, 5, TimeUnit.MINUTES);

        logger.debug("Connection pool monitoring started");
    }

    /**
     * Get the data source
     * @return The data source
     */
    public static HikariDataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null || dataSource.isClosed()) {
                    logger.info("Data source is null or closed. Reinitializing...");
                    initializeDataSource();
                }
            }
        }
        return dataSource;
    }

    /**
     * Close the data source
     */
    public static void closeDataSource() {
        if (connectionPoolMonitor != null) {
            connectionPoolMonitor.shutdownNow();
            connectionPoolMonitor = null;
            logger.debug("Connection pool monitoring stopped");
        }

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    /**
     * Inner class to hold database configuration
     */
    private static class DatabaseConfig {
        String host;
        String port;
        String name;
        String username;
        String password;
    }
}