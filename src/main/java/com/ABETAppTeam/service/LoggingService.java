package com.ABETAppTeam.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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
    private final Logger errorLogger;  // New dedicated logger for errors

    /**
     * Private constructor for a singleton pattern
     */
    private LoggingService() {
        this.appLogger = LoggerFactory.getLogger("com.ABETAppTeam.application");
        this.dbLogger = LoggerFactory.getLogger("com.ABETAppTeam.database");
        this.perfLogger = LoggerFactory.getLogger("com.ABETAppTeam.performance");
        this.securityLogger = LoggerFactory.getLogger("com.ABETAppTeam.security");
        this.accessLogger = LoggerFactory.getLogger("com.ABETAppTeam.access");
        this.errorLogger = LoggerFactory.getLogger("com.ABETAppTeam.error");  // New error logger
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

        // Get an instance to ensure it's created
        getInstance();

        // Log initialization
        getInstance().info("Logging system initialized");
    }

    /*
     * Standard application logging methods
     */

    public void trace(String message) {
        appLogger.trace(message);
    }

    public void trace(String format, Object... args) {
        appLogger.trace(format, args);
    }

    public void trace(String message, Throwable t) {
        appLogger.trace(message, t);
    }

    public void debug(String message) {
        appLogger.debug(message);
    }

    public void debug(String format, Object... args) {
        appLogger.debug(format, args);
    }

    public void debug(String message, Throwable t) {
        appLogger.debug(message, t);
    }

    public void info(String message) {
        appLogger.info(message);
    }

    public void info(String format, Object... args) {
        appLogger.info(format, args);
    }

    public void info(String message, Throwable t) {
        appLogger.info(message, t);
    }

    public void warn(String message) {
        appLogger.warn(message);
    }

    public void warn(String format, Object... args) {
        appLogger.warn(format, args);
    }

    public void warn(String message, Throwable t) {
        appLogger.warn(message, t);
    }

    public void error(String message) {
        appLogger.error(message);
    }

    public void error(String format, Object... args) {
        appLogger.error(format, args);
    }

    public void error(String message, Throwable t) {
        appLogger.error(message, t);
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
        // If a params array is part of a query with named parameters,
        // the parameter name might be at index-1
        if (index > 0 && params[index-1] instanceof String) {
            String paramName = ((String) params[index-1]).toLowerCase();
            return paramName.contains("password") || paramName.contains("pwd");
        }
        return false;
    }

    /**
     * Execute an SQL statement with logging and timing
     *
     * @param conn The database connection
     * @param sql The SQL query to execute
     * @return The result of the query execution
     * @throws SQLException If a database error occurs
     */
    public ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        try {
            logConnectionCreated(conn);
            stmt = conn.createStatement();
            logStatementCreated(stmt, sql);
            ResultSet rs = stmt.executeQuery(sql);
            long executionTime = System.currentTimeMillis() - startTime;
            logStatementExecuted(stmt, sql, executionTime);
            logSqlQuery(sql, executionTime);
            return rs;
        } catch (SQLException e) {
            logSqlError(sql, e);
            throw e;
        }
    }

    /**
     * Execute an SQL prepared statement with logging and timing
     *
     * @param conn The database connection
     * @param sql The SQL query to execute
     * @param params The parameters for the prepared statement
     * @return The result of the query execution
     * @throws SQLException If a database error occurs
     */
    public ResultSet executeQuery(Connection conn, String sql, Object... params) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        try {
            logConnectionCreated(conn);
            stmt = conn.prepareStatement(sql);
            logStatementCreated(stmt, sql);

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            long executionTime = System.currentTimeMillis() - startTime;
            logStatementExecuted(stmt, sql, executionTime);
            logSqlQuery(sql, params, executionTime);
            return rs;
        } catch (SQLException e) {
            logSqlError(sql, params, e);
            throw e;
        }
    }

    /**
     * Execute an update statement with logging and timing
     *
     * @param conn The database connection
     * @param sql The SQL statement to execute
     * @return The number of rows affected
     * @throws SQLException If a database error occurs
     */
    public int executeUpdate(Connection conn, String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        try {
            logConnectionCreated(conn);
            stmt = conn.createStatement();
            logStatementCreated(stmt, sql);

            int rowsAffected = stmt.executeUpdate(sql);
            long executionTime = System.currentTimeMillis() - startTime;
            logStatementExecuted(stmt, sql, executionTime);
            logSqlQuery(sql, executionTime);
            return rowsAffected;
        } catch (SQLException e) {
            logSqlError(sql, e);
            throw e;
        }
    }

    /**
     * Execute an update prepared statement with logging and timing
     *
     * @param conn The database connection
     * @param sql The SQL statement to execute
     * @param params The parameters for the prepared statement
     * @return The number of rows affected
     * @throws SQLException If a database error occurs
     */
    public int executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        try {
            logConnectionCreated(conn);
            stmt = conn.prepareStatement(sql);
            logStatementCreated(stmt, sql);

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            int rowsAffected = stmt.executeUpdate();
            long executionTime = System.currentTimeMillis() - startTime;
            logStatementExecuted(stmt, sql, executionTime);
            logSqlQuery(sql, params, executionTime);
            return rowsAffected;
        } catch (SQLException e) {
            logSqlError(sql, params, e);
            throw e;
        }
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
     * Start timing an operation
     *
     * @param operationName Name of the operation to time
     * @return A unique timer ID to be used with stopTimer
     */
    public String startTimer(String operationName) {
        String timerId = UUID.randomUUID().toString();
        MDC.put("timerId", timerId);
        MDC.put("operationName", operationName);
        MDC.put("startTime", String.valueOf(System.currentTimeMillis()));

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
        // Get stored values
        String storedTimerId = MDC.get("timerId");
        String operationName = MDC.get("operationName");
        String startTimeStr = MDC.get("startTime");

        if (timerId != null && timerId.equals(storedTimerId) && startTimeStr != null) {
            long startTime = Long.parseLong(startTimeStr);
            long duration = System.currentTimeMillis() - startTime;

            if (additionalInfo != null && !additionalInfo.isEmpty()) {
                perfLogger.info("Operation completed: {} ({}ms) - {}",
                        operationName, duration, additionalInfo);
            } else {
                perfLogger.info("Operation completed: {} ({}ms)",
                        operationName, duration);
            }
        }

        // Clear MDC for this thread
        MDC.remove("timerId");
        MDC.remove("operationName");
        MDC.remove("startTime");
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
        MDC.put("userId", userId);
        MDC.put("ipAddress", ipAddress);
        MDC.put("eventType", eventType);

        if (success) {
            securityLogger.info("Security Event: {} - Success - {}", eventType, details);
        } else {
            securityLogger.warn("Security Event: {} - Failed - {}", eventType, details);
        }

        MDC.remove("userId");
        MDC.remove("ipAddress");
        MDC.remove("eventType");
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
        MDC.put("userId", userId);
        MDC.put("ipAddress", ipAddress);

        accessLogger.info("Access: {} - {} - {}", userId, resource, action);

        MDC.remove("userId");
        MDC.remove("ipAddress");
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

    /*
     * Enhanced Error Logging Methods
     */

    /**
     * Log a detailed error with exception and stack trace
     * 
     * @param message Error message
     * @param throwable The exception to log
     */
    public void logError(String message, Throwable throwable) {
        String stackTrace = getStackTraceAsString(throwable);
        String errorType = throwable.getClass().getName();
        
        MDC.put("errorType", errorType);
        MDC.put("stackTrace", "available");
        
        errorLogger.error("ERROR [{}]: {} - {}", errorType, message, throwable.getMessage());
        errorLogger.debug("Stack trace for [{}]: \n{}", errorType, stackTrace);
        
        // Also log to application logger for consistency
        appLogger.error("{} - {} \n{}", message, throwable.getMessage(), stackTrace);
        
        MDC.remove("errorType");
        MDC.remove("stackTrace");
    }
    
    /**
     * Log a categorized application error with exception details
     * 
     * @param category Error category (e.g., "UI", "Database", "Network", "Security")
     * @param component Affected component or class
     * @param message Error message
     * @param throwable The exception to log
     */
    public void logCategorizedError(String category, String component, String message, Throwable throwable) {
        String errorType = throwable.getClass().getName();
        String stackTrace = getStackTraceAsString(throwable);
        
        MDC.put("errorCategory", category);
        MDC.put("component", component);
        MDC.put("errorType", errorType);
        
        Map<String, String> errorContext = new HashMap<>();
        errorContext.put("category", category);
        errorContext.put("component", component);
        errorContext.put("type", errorType);
        errorContext.put("message", throwable.getMessage());
        
        String contextStr = formatErrorContext(errorContext);
        
        errorLogger.error("ERROR [{}] in {}: {} - {}", category, component, message, throwable.getMessage());
        errorLogger.debug("Context: {} \nStack trace: \n{}", contextStr, stackTrace);
        
        MDC.remove("errorCategory");
        MDC.remove("component");
        MDC.remove("errorType");
    }
    
    /**
     * Format error context map into a readable string
     */
    private String formatErrorContext(Map<String, String> context) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : context.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
        }
        return sb.toString();
    }
    
    /**
     * Convert a Throwable's stack trace to a string
     * 
     * @param throwable The exception to convert
     * @return String representation of the stack trace
     */
    public String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Logs an exception chain (including all caused-by exceptions)
     * 
     * @param message Error message
     * @param throwable The root exception
     */
    public void logExceptionChain(String message, Throwable throwable) {
        if (throwable == null) {
            error(message);
            return;
        }
        
        StringBuilder chainMessage = new StringBuilder(message);
        chainMessage.append("\nException chain:");
        
        Throwable current = throwable;
        int depth = 0;
        
        while (current != null) {
            chainMessage.append("\n  ").append(depth).append(") ")
                       .append(current.getClass().getName())
                       .append(": ").append(current.getMessage());
            
            depth++;
            current = current.getCause();
        }
        
        errorLogger.error(chainMessage.toString());
        errorLogger.debug("Full stack trace: \n{}", getStackTraceAsString(throwable));
    }
    
    /**
     * Log a SQL error with enhanced details and full stack trace
     * 
     * @param sql The SQL query that caused the error
     * @param e The SQLException that occurred
     */
    public void logSqlErrorWithDetails(String sql, SQLException e) {
        String errorDetails = formatSqlError(e);
        String stackTrace = getStackTraceAsString(e);
        
        MDC.put("sqlState", e.getSQLState());
        MDC.put("errorCode", String.valueOf(e.getErrorCode()));
        
        dbLogger.error("SQL Error: {} - {}", sql, errorDetails);
        dbLogger.debug("SQL Stack Trace: \n{}", stackTrace);
        
        // Also log to error logger for centralized error tracking
        errorLogger.error("SQL Error [{}]: {} - {}", e.getSQLState(), sql, errorDetails);
        
        MDC.remove("sqlState");
        MDC.remove("errorCode");
    }
    
    /**
     * Log a critical application error that might require immediate attention
     * 
     * @param message Error message
     * @param throwable The exception to log
     */
    public void logCriticalError(String message, Throwable throwable) {
        String stackTrace = getStackTraceAsString(throwable);
        String errorType = throwable.getClass().getName();
        
        // Add markers to MDC
        MDC.put("CRITICAL", "true");
        MDC.put("errorType", errorType);
        
        // Log at error level
        errorLogger.error("CRITICAL ERROR: {} - {} \n{}", message, throwable.getMessage(), stackTrace);
        
        // Also log to application logger for consistency
        appLogger.error("CRITICAL ERROR: {} - {}", message, throwable.getMessage());
        
        MDC.remove("CRITICAL");
        MDC.remove("errorType");
    }

    /**
     * Create a contextual exception log with user and request information
     * 
     * @param message Error message
     * @param throwable The exception to log
     * @param userId User ID (if available)
     * @param requestInfo Additional request information (URL, parameters, etc.)
     */
    public void logContextualError(String message, Throwable throwable, String userId, Map<String, Object> requestInfo) {
        if (throwable == null) {
            return;
        }
        
        String stackTrace = getStackTraceAsString(throwable);
        String errorType = throwable.getClass().getName();
        
        // Set context in MDC
        MDC.put("userId", userId != null ? userId : "unknown");
        MDC.put("errorType", errorType);
        
        // Build context information
        StringBuilder contextBuilder = new StringBuilder("Context: ");
        contextBuilder.append("userId=").append(userId != null ? userId : "unknown");
        
        if (requestInfo != null) {
            for (Map.Entry<String, Object> entry : requestInfo.entrySet()) {
                contextBuilder.append(", ").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        
        // Log the error with context
        errorLogger.error("{} - {} - {}", message, throwable.getMessage(), contextBuilder.toString());
        errorLogger.debug("Stack trace: \n{}", stackTrace);
        
        // Clean up MDC
        MDC.remove("userId");
        MDC.remove("errorType");
    }
}