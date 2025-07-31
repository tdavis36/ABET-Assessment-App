package com.ABETAppTeam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.AppUtils;

/**
 * Factory class for creating and managing database connections with enhanced logging
 */
public class DataSourceFactory {

    private static final LoggingService logger = LoggingService.getInstance();
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
        // Start timer for database initialization
        String timerId = logger.startTimer("databaseInitialization");

        try {
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
        } finally {
            // Stop the timer
            logger.stopTimer(timerId, null);
        }
    }

    /**
     * Load database configuration from environment variables or local.properties file
     *
     * @return Database configuration object
     */
    private static DatabaseConfig loadDatabaseConfig() {
        // Start timer for config loading
        String timerId = logger.startTimer("loadDatabaseConfig");

        DatabaseConfig config = new DatabaseConfig();

        try {
            // First try to load from environment variables
            config.host = System.getenv("DB_HOST");
            config.port = System.getenv("DB_PORT");
            config.name = System.getenv("DB_NAME");
            config.username = System.getenv("DB_USERNAME");
            config.password = System.getenv("DB_PASSWORD");

            // Load from local.properties file if environment variables are not set
            if (config.host == null || config.port == null || config.name == null ||
                    config.username == null || config.password == null) {

                Properties envProps = new Properties();
                File envFile = new File("local.properties");

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

                        logger.debug("Loaded database configuration from local.properties file");
                    } catch (IOException e) {
                        logger.error("Error reading local.properties file: {}", e.getMessage(), e);
                    }
                } else {
                    logger.warn("No local.properties file found at {}. Using default database settings.", envFile.getAbsolutePath());
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
        } catch (Exception e) {
            logger.error("Error loading database configuration: {}", e.getMessage(), e);
            // Set defaults in case of error
            config.host = "localhost";
            config.port = "3306";
            config.name = "abetapp";
            config.username = "user";
            config.password = "pass";
        } finally {
            // Stop the timer
            logger.stopTimer(timerId, null);
        }

        return config;
    }

    /**
     * Test the database connection
     */
    private static void testConnection() {
        // Start timer for connection test
        String timerId = logger.startTimer("testDatabaseConnection");

        try (Connection conn = dataSource.getConnection()) {
            // Log connection creation
            logger.logConnectionCreated(conn);

            if (conn.isValid(5)) {
                logger.info("Database connection test successful");
            } else {
                logger.error("Database connection test failed: Connection is not valid");
                throw new SQLException("Connection validation failed");
            }

            // Log connection close
            logger.logConnectionClosed(conn);
        } catch (SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection test failed", e);
        } finally {
            // Stop the timer
            logger.stopTimer(timerId, null);
        }
    }

    /**
     * Initialize the database schema if needed
     */
    private static void initializeDatabase() {
        // Start timer for database schema check
        String timerId = logger.startTimer("checkDatabaseSchema");

        // Check if essential tables exist
        try (Connection conn = dataSource.getConnection()) {
            logger.debug("Testing database schema by checking for User table");

            // Log connection creation
            logger.logConnectionCreated(conn);

            try {
                // Create and execute statement with logging
                java.sql.Statement stmt = conn.createStatement();
                logger.logStatementCreated(stmt, "SELECT 1 FROM User LIMIT 1");

                boolean hasResultSet = AppUtils.timeOperation("executeSchemaCheck", () -> {
                    try {
                        return stmt.execute("SELECT 1 FROM User LIMIT 1");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                long executionTime = 0; // Timing is now handled by AppUtils.timeOperation

                logger.logStatementExecuted(stmt, "SELECT 1 FROM User LIMIT 1", executionTime);

                if (hasResultSet) {
                    try (java.sql.ResultSet rs = stmt.getResultSet()) {
                        // Count rows (should be 1)
                        int rowCount = 0;
                        if (rs.next()) {
                            rowCount++;
                        }
                        logger.logResultSetFetched(rs, rowCount, 0);
                    }
                }

                logger.info("Database schema exists, tables found");
                stmt.close();
            } catch (SQLException e) {
                // Table doesn't exist, log this info but don't treat as an error
                logger.info("Database schema not found. Tables will be created automatically by Flyway migrations.");

                // Log SQL error
                logger.logSqlError("SELECT 1 FROM User LIMIT 1", e);
            } finally {
                // Log connection close
                logger.logConnectionClosed(conn);
            }
        } catch (SQLException e) {
            logger.error("Error checking database schema: {}", e.getMessage(), e);
        } finally {
            // Stop the timer
            logger.stopTimer(timerId, null);
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

        connectionPoolMonitor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DB-Pool-Monitor");
            t.setDaemon(true);
            return t;
        });

        connectionPoolMonitor.scheduleAtFixedRate(() -> {
            try {
                if (dataSource != null) {
                    int active = dataSource.getHikariPoolMXBean().getActiveConnections();
                    int idle = dataSource.getHikariPoolMXBean().getIdleConnections();
                    int total = dataSource.getHikariPoolMXBean().getTotalConnections();

                    // Use LoggingService for consistent logging
                    logger.logConnectionPoolStatus(active, idle, total);

                    // Log any potential issues
                    if (active == total && total == dataSource.getMaximumPoolSize()) {
                        logger.warn("Connection pool is at maximum capacity. This may indicate a connection leak or insufficient pool size.");
                    }

                    // Log waiting threads if any
                    int waiting = dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
                    if (waiting > 0) {
                        logger.warn("Connection pool has {} threads waiting for connections.", waiting);
                    }
                }
            } catch (Exception e) {
                logger.error("Error monitoring connection pool: {}", e.getMessage(), e);
            }
        }, 1, 5, TimeUnit.MINUTES);

        logger.debug("Connection pool monitoring started");
    }

    /**
     * Get the data source
     *
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
            try {
                connectionPoolMonitor.shutdownNow();
                if (!connectionPoolMonitor.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("Connection pool monitor did not terminate gracefully");
                }
                connectionPoolMonitor = null;
                logger.debug("Connection pool monitoring stopped");
            } catch (InterruptedException e) {
                logger.warn("Interrupted while shutting down connection pool monitor: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        if (dataSource != null && !dataSource.isClosed()) {
            String timerId = logger.startTimer("closeDataSource");
            try {
                dataSource.close();
                logger.info("Database connection pool closed successfully");
            } catch (Exception e) {
                logger.error("Error closing database connection pool: {}", e.getMessage(), e);
            } finally {
                logger.stopTimer(timerId, null);
            }
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
