package com.ABETAppTeam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.ABETAppTeam.service.LoggingService;

/**
 * Utility class for database logging
 * This class is now a thin wrapper around LoggingService for backward compatibility
 */
public class DatabaseLogger {

    private static final LoggingService loggingService = LoggingService.getInstance();

    /**
     * Log an SQL query error with details
     *
     * @param sql The SQL query that caused the error
     * @param e The SQLException that occurred
     */
    public static void logSqlError(String sql, SQLException e) {
        loggingService.logSqlError(sql, e);
    }

    /**
     * Log a SQL query error with parameters
     *
     * @param sql The SQL query that caused the error
     * @param params The parameters used in the query
     * @param e The SQLException that occurred
     */
    public static void logSqlError(String sql, Object[] params, SQLException e) {
        loggingService.logSqlError(sql, params, e);
    }

    /**
     * Log a successful SQL query for debugging
     *
     * @param sql The SQL query that was executed
     * @param timeMs The time in milliseconds that the query took to execute
     */
    public static void logSqlQuery(String sql, long timeMs) {
        loggingService.logSqlQuery(sql, timeMs);
    }

    /**
     * Log a successful SQL query with parameters for debugging
     *
     * @param sql The SQL query that was executed
     * @param params The parameters used in the query
     * @param timeMs The time in milliseconds that the query took to execute
     */
    public static void logSqlQuery(String sql, Object[] params, long timeMs) {
        loggingService.logSqlQuery(sql, params, timeMs);
    }

    /**
     * Execute an SQL statement with logging and timing
     *
     * @param conn The database connection
     * @param sql The SQL query to execute
     * @return The result of the query execution
     * @throws SQLException If a database error occurs
     */
    public static ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            long executionTime = System.currentTimeMillis() - startTime;
            loggingService.logSqlQuery(sql, executionTime);
            return rs;
        } catch (SQLException e) {
            loggingService.logSqlError(sql, e);
            throw e;
        }
    }

    /**
     * Execute a SQL prepared statement with logging and timing
     *
     * @param conn The database connection
     * @param sql The SQL query to execute
     * @param params The parameters for the prepared statement
     * @return The result of the query execution
     * @throws SQLException If a database error occurs
     */
    public static ResultSet executeQuery(Connection conn, String sql, Object... params) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = stmt.executeQuery();
            long executionTime = System.currentTimeMillis() - startTime;
            loggingService.logSqlQuery(sql, params, executionTime);
            return rs;
        } catch (SQLException e) {
            loggingService.logSqlError(sql, params, e);
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
    public static int executeUpdate(Connection conn, String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            long executionTime = System.currentTimeMillis() - startTime;
            loggingService.logSqlQuery(sql, executionTime);
            return rowsAffected;
        } catch (SQLException e) {
            loggingService.logSqlError(sql, e);
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
    public static int executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            int rowsAffected = stmt.executeUpdate();
            long executionTime = System.currentTimeMillis() - startTime;
            loggingService.logSqlQuery(sql, params, executionTime);
            return rowsAffected;
        } catch (SQLException e) {
            loggingService.logSqlError(sql, params, e);
            throw e;
        }
    }

    /**
     * Log connection pool status information
     *
     * @param activeConnections Number of active connections
     * @param idleConnections Number of idle connections
     * @param totalConnections Total number of connections
     */
    public static void logConnectionPoolStatus(int activeConnections, int idleConnections, int totalConnections) {
        loggingService.logConnectionPoolStatus(activeConnections, idleConnections, totalConnections);
    }
}