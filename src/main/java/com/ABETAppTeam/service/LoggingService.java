package com.ABETAppTeam.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unified logging service for the ABET Assessment Application
 * Handles all logging concerns including application events, database operations,
 * performance monitoring, security logging, and more.
 */
public class LoggingService {
    // Singleton instance
    private static LoggingService instance;

    // Category-specific loggers
    private final Logger appLogger;
    private final Logger dbLogger;
    private final Logger perfLogger;
    private final Logger securityLogger;
    private final Logger accessLogger;

    // Thread-local storage for nested timers
    private final ThreadLocal<Map<String, String>> timerStorage =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * Private constructor for singleton pattern
     */
    private LoggingService() {
        this.appLogger = LoggerFactory.getLogger("com.ABETAppTeam.application");
        this.dbLogger = LoggerFactory.getLogger("com.ABETAppTeam.database");
        this.perfLogger = LoggerFactory.getLogger("com.ABETAppTeam.performance");
        this.securityLogger = LoggerFactory.getLogger("com.ABETAppTeam.security");
        this.accessLogger = LoggerFactory.getLogger("com.ABETAppTeam.access");
    }

    /**
     * Get the singleton instance
     *
     * @return LoggingService instance
     */
    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }

    /**
     * Initialize the logging system.
     * Call this method at application startup.
     */
    public static void initialize() {
        // Configure SLF4J Bridge Handler for java.util.logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Set system properties to enable JDBC driver logging
        System.setProperty("org.mariadb.jdbc.logging.type", "slf4j");

        // Get instance to ensure it's created
        getInstance().info("Logging system initialized");
    }

    /*
     * Application logging methods - Using format string versions only to reduce API surface
     */

    public void trace(String format, Object... args) {
        appLogger.trace(format, args);
    }

    public void debug(String format, Object... args) {
        appLogger.debug(format, args);
    }

    public void info(String format, Object... args) {
        appLogger.info(format, args);
    }

    public void warn(String format, Object... args) {
        appLogger.warn(format, args);
    }

    public void error(String format, Object... args) {
        appLogger.error(format, args);
    }

    /*
     * Logger level check methods
     */

    public boolean isTraceEnabled() {
        return appLogger.isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return appLogger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return appLogger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return appLogger.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return appLogger.isErrorEnabled();
    }

    public boolean isDbDebugEnabled() {
        return dbLogger.isDebugEnabled();
    }

    /*
     * Database logging methods
     */

    /**
     * Log an SQL query with execution time
     *
     * @param sql The SQL query that was executed
     * @param timeMs The time in milliseconds that the query took
     */
    public void logSqlQuery(String sql, long timeMs) {
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug("SQL Query ({}ms): {}", timeMs, sql);
        }
    }

    /**
     * Log an SQL query with parameters and execution time
     *
     * @param sql The SQL query that was executed
     * @param params The parameters used in the query
     * @param timeMs The time in milliseconds that the query took
     */
    public void logSqlQuery(String sql, Object[] params, long timeMs) {
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug("SQL Query ({}ms): {} with params: {}",
                    timeMs, sql, formatParams(params));
        }
    }

    /**
     * Log an SQL error
     *
     * @param sql The SQL query that caused the error
     * @param e The SQLException that occurred
     */
    public void logSqlError(String sql, SQLException e) {
        String errorDetails = formatSqlError(e);
        dbLogger.error("SQL Error: {} - {}", sql, errorDetails, e);
    }

    /**
     * Log an SQL error with parameters
     *
     * @param sql The SQL query that caused the error
     * @param params The parameters used in the query
     * @param e The SQLException that occurred
     */
    public void logSqlError(String sql, Object[] params, SQLException e) {
        String errorDetails = formatSqlError(e);
        dbLogger.error("SQL Error: {} with params {} - {}",
                sql, formatParams(params), errorDetails, e);
    }

    /**
     * Format an SQLException for logging
     */
    private String formatSqlError(SQLException e) {
        return String.format("Error code: %d, SQL state: %s, Message: %s",
                e.getErrorCode(), e.getSQLState(), e.getMessage());
    }

    /**
     * Format parameter array for logging
     */
    private String formatParams(Object[] params) {
        if (params == null) return "[]";

        // Handle potentially sensitive data
        Object[] sanitizedParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            // Sanitize sensitive parameters (passwords, etc.)
            if (param instanceof String &&
                    (String.valueOf(param).length() > 100 ||
                            isPasswordParameter(i, params))) {
                sanitizedParams[i] = "[REDACTED]";
            } else {
                sanitizedParams[i] = param;
            }
        }
        return Arrays.toString(sanitizedParams);
    }

    /**
     * Check if a parameter might be a password
     */
    private boolean isPasswordParameter(int index, Object[] params) {
        // If params array is part of a query with named parameters,
        // the parameter name might be at index-1
        if (index > 0 && params[index-1] instanceof String) {
            String paramName = ((String) params[index-1]).toLowerCase();
            return paramName.contains("password") || paramName.contains("pwd");
        }
        return false;
    }

    /**
     * Execute an SQL statement with logging and timing
     * Centralized implementation for all SQL execution methods
     *
     * @param conn The database connection
     * @param sql The SQL statement to execute
     * @param isPrepared Whether this is a prepared statement
     * @param isQuery Whether this is a query (vs. update)
     * @param params Optional parameters for prepared statements
     * @return Object that can be cast to either ResultSet or Integer depending on isQuery
     * @throws SQLException If a database error occurs
     */
    private Object executeSql(Connection conn, String sql, boolean isPrepared, boolean isQuery, Object... params)
            throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        Object result = null;

        try {
            // Log connection use
            logConnectionCreated(conn);

            // Create statement based on type
            if (isPrepared) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                // Set parameters for prepared statement
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                stmt = pstmt;
            } else {
                stmt = conn.createStatement();
            }

            // Log statement creation
            logStatementCreated(stmt, sql);

            // Execute statement based on type
            if (isQuery) {
                ResultSet rs;
                if (isPrepared) {
                    rs = ((PreparedStatement)stmt).executeQuery();
                } else {
                    rs = stmt.executeQuery(sql);
                }
                result = rs;
            } else {
                int rowsAffected;
                if (isPrepared) {
                    rowsAffected = ((PreparedStatement)stmt).executeUpdate();
                } else {
                    rowsAffected = stmt.executeUpdate(sql);
                }
                result = rowsAffected;
            }

            // Calculate and log execution time
            long executionTime = System.currentTimeMillis() - startTime;
            logStatementExecuted(stmt, sql, executionTime);

            // Log the query with or without parameters
            if (isPrepared) {
                logSqlQuery(sql, params, executionTime);
            } else {
                logSqlQuery(sql, executionTime);
            }

            return result;
        } catch (SQLException e) {
            // Log SQL error
            if (isPrepared) {
                logSqlError(sql, params, e);
            } else {
                logSqlError(sql, e);
            }
            throw e;
        }
    }

    /**
     * Execute a query and return the result set
     */
    public ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        return (ResultSet)executeSql(conn, sql, false, true);
    }

    /**
     * Execute a prepared query with parameters and return the result set
     */
    public ResultSet executeQuery(Connection conn, String sql, Object... params) throws SQLException {
        return (ResultSet)executeSql(conn, sql, true, true, params);
    }

    /**
     * Execute an update and return the number of rows affected
     */
    public int executeUpdate(Connection conn, String sql) throws SQLException {
        return (Integer)executeSql(conn, sql, false, false);
    }

    /**
     * Execute a prepared update with parameters and return the number of rows affected
     */
    public int executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        return (Integer)executeSql(conn, sql, true, false, params);
    }

    /*
     * JDBC Connection logging methods
     */

    /**
     * Log a connection creation
     */
    public void logConnectionCreated(Connection conn) {
        if (dbLogger.isDebugEnabled()) {
            try {
                String url = conn.getMetaData().getURL();
                String user = conn.getMetaData().getUserName();
                // Sanitize the URL to remove password
                url = sanitizeJdbcUrl(url);
                dbLogger.debug("SQL Connection created: {} (User: {})", url, user);
            } catch (Exception e) {
                dbLogger.debug("SQL Connection created (Unable to get details: {})", e.getMessage());
            }
        }
    }

    /**
     * Log a connection close
     */
    public void logConnectionClosed(Connection conn) {
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug("SQL Connection closed");
        }
    }

    /**
     * Log a statement creation
     */
    public void logStatementCreated(Statement stmt, String sql) {
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug("SQL Statement created: {}", sql);
        }
    }

    /**
     * Log statement execution
     */
    public void logStatementExecuted(Statement stmt, String sql, long executionTime) {
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug("SQL Statement executed in {}ms: {}", executionTime, sql);
        }
    }

    /**
     * Log result set fetch
     */
    public void logResultSetFetched(ResultSet rs, int rowCount, long fetchTime) {
        if (dbLogger.isDebugEnabled()) {
            dbLogger.debug("SQL ResultSet fetched {} rows in {}ms", rowCount, fetchTime);
        }
    }

    /**
     * Log connection properties in a consistent format
     */
    public void logConnectionProperties(Properties props) {
        if (dbLogger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("SQL Connection properties: {");
            boolean first = true;

            for (String name : props.stringPropertyNames()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;

                String value = props.getProperty(name);
                // Don't log passwords
                if (name.toLowerCase().contains("password")) {
                    value = "*****";
                }

                sb.append(name).append("=").append(value);
            }

            sb.append("}");
            dbLogger.debug(sb.toString());
        }
    }

    /**
     * Log connection pool status information
     *
     * @param activeConnections Number of active connections
     * @param idleConnections Number of idle connections
     * @param totalConnections Total number of connections
     */
    public void logConnectionPoolStatus(int activeConnections, int idleConnections, int totalConnections) {
        if (dbLogger.isInfoEnabled()) {
            dbLogger.info("Connection pool status: active={}, idle={}, total={}",
                    activeConnections, idleConnections, totalConnections);
        }
    }

    /**
     * Create a JDBC URL for logging (with password removed)
     */
    public String sanitizeJdbcUrl(String url) {
        if (url == null) {
            return null;
        }
        // Remove password parameter if present
        return url.replaceAll("password=([^&]*)", "password=*****");
    }

    /*
     * Performance logging methods
     */

    /**
     * Start timing an operation with support for nested timers
     *
     * @param operationName Name of the operation to time
     * @return A unique timer ID to be used with stopTimer
     */
    public String startTimer(String operationName) {
        // Generate a unique ID for this timer
        String timerId = UUID.randomUUID().toString();

        // Store timer information in thread-local storage
        Map<String, String> timerMap = timerStorage.get();
        String timerKey = "timer_" + timerId;
        timerMap.put(timerKey + "_name", operationName);
        timerMap.put(timerKey + "_start", String.valueOf(System.currentTimeMillis()));

        // Also store in MDC for potential use by appenders
        MDC.put("currentOperation", operationName);

        if (perfLogger.isDebugEnabled()) {
            perfLogger.debug("Operation started: {}", operationName);
        }

        return timerId;
    }

    /**
     * Stop timing an operation and log the duration
     *
     * @param timerId The timer ID returned from startTimer
     * @param additionalInfo Optional additional information about the operation
     */
    public void stopTimer(String timerId, String additionalInfo) {
        if (timerId == null) {
            return;
        }

        // Retrieve timer information from thread-local storage
        Map<String, String> timerMap = timerStorage.get();
        String timerKey = "timer_" + timerId;
        String operationName = timerMap.remove(timerKey + "_name");
        String startTimeStr = timerMap.remove(timerKey + "_start");

        if (operationName != null && startTimeStr != null) {
            long startTime = Long.parseLong(startTimeStr);
            long duration = System.currentTimeMillis() - startTime;

            if (additionalInfo != null && !additionalInfo.isEmpty()) {
                perfLogger.info("Operation completed: {} ({}ms) - {}",
                        operationName, duration, additionalInfo);
            } else {
                perfLogger.info("Operation completed: {} ({}ms)",
                        operationName, duration);
            }

            // Clear the MDC key if there are no more timers
            if (timerMap.isEmpty()) {
                MDC.remove("currentOperation");
            } else {
                // Find the most recent timer and set it as the current operation
                String mostRecentTimer = null;
                long mostRecentStart = 0;

                for (String key : timerMap.keySet()) {
                    if (key.endsWith("_start")) {
                        String timeStr = timerMap.get(key);
                        long time = Long.parseLong(timeStr);
                        if (time > mostRecentStart) {
                            mostRecentStart = time;
                            // Extract timer name from key "timer_{id}_start"
                            String timerId2 = key.substring(6, key.length() - 6);
                            mostRecentTimer = timerMap.get("timer_" + timerId2 + "_name");
                        }
                    }
                }

                if (mostRecentTimer != null) {
                    MDC.put("currentOperation", mostRecentTimer);
                }
            }
        }
    }

    /*
     * Security logging methods
     */

    /**
     * Log a security event
     *
     * @param eventType Type of security event (e.g., "LOGIN_ATTEMPT", "PASSWORD_CHANGE")
     * @param userId User ID associated with the event
     * @param ipAddress IP address associated with the event
     * @param details Additional details about the event
     * @param success Whether the event was successful
     */
    public void logSecurityEvent(String eventType, String userId, String ipAddress,
                                 String details, boolean success) {
        // Save old values to restore after logging
        String oldUserId = MDC.get("userId");
        String oldIpAddress = MDC.get("ipAddress");
        String oldEventType = MDC.get("eventType");

        // Set context for this log entry
        MDC.put("userId", userId);
        MDC.put("ipAddress", ipAddress);
        MDC.put("eventType", eventType);

        try {
            if (success) {
                securityLogger.info("Security Event: {} - Success - {}", eventType, details);
            } else {
                securityLogger.warn("Security Event: {} - Failed - {}", eventType, details);
            }
        } finally {
            // Restore or clear context
            restoreMdcValue("userId", oldUserId);
            restoreMdcValue("ipAddress", oldIpAddress);
            restoreMdcValue("eventType", oldEventType);
        }
    }

    /**
     * Log an access event (page view, resource access, etc.)
     *
     * @param userId User ID
     * @param ipAddress IP address
     * @param resource Resource being accessed
     * @param action Action performed
     */
    public void logAccess(String userId, String ipAddress, String resource, String action) {
        // Save old values to restore after logging
        String oldUserId = MDC.get("userId");
        String oldIpAddress = MDC.get("ipAddress");

        // Set context for this log entry
        MDC.put("userId", userId);
        MDC.put("ipAddress", ipAddress);

        try {
            accessLogger.info("Access: {} - {} - {}", userId, resource, action);
        } finally {
            // Restore or clear context
            restoreMdcValue("userId", oldUserId);
            restoreMdcValue("ipAddress", oldIpAddress);
        }
    }

    /**
     * Set user context for logging
     * Call this at the beginning of a request to add user info to all subsequent logs
     *
     * @param userId User ID
     * @param userRole User role
     */
    public void setUserContext(String userId, String userRole) {
        MDC.put("userId", userId);
        MDC.put("userRole", userRole);
    }

    /**
     * Clear user context
     * Call this at the end of a request
     */
    public void clearUserContext() {
        MDC.remove("userId");
        MDC.remove("userRole");
    }

    /**
     * Helper method to restore an MDC value
     */
    private void restoreMdcValue(String key, String oldValue) {
        if (oldValue == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, oldValue);
        }
    }
}