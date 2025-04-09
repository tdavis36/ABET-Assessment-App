package com.ABETAppTeam.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import com.ABETAppTeam.service.LoggingService;

/**
 * JDBC Logger proxy to intercept and format JDBC driver logs
 * This class is now a thin wrapper around LoggingService for backward compatibility
 */
public class JDBCLogger {

    private static final LoggingService loggingService = LoggingService.getInstance();

    /**
     * Initialize the JDBC logging system
     * Call this at application startup to enable JDBC driver logging
     */
    public static void initialize() {
        // Set system properties to enable JDBC driver logging
        System.setProperty("org.mariadb.jdbc.logging.type", "slf4j");

        // Log that we've initialized the JDBC logging
        loggingService.info("JDBC driver logging initialized");
    }

    /**
     * Log a connection creation
     */
    public static void logConnectionCreated(Connection conn) {
        loggingService.logConnectionCreated(conn);
    }

    /**
     * Log a connection close
     */
    public static void logConnectionClosed(Connection conn) {
        loggingService.logConnectionClosed(conn);
    }

    /**
     * Log a statement creation
     */
    public static void logStatementCreated(Statement stmt, String sql) {
        loggingService.logStatementCreated(stmt, sql);
    }

    /**
     * Log statement execution
     */
    public static void logStatementExecuted(Statement stmt, String sql, long executionTime) {
        loggingService.logStatementExecuted(stmt, sql, executionTime);
    }

    /**
     * Log result set fetch
     */
    public static void logResultSetFetched(ResultSet rs, int rowCount, long fetchTime) {
        if (loggingService.isDebugEnabled()) {
            loggingService.debug("SQL ResultSet fetched " + rowCount + " rows in " + fetchTime + "ms");
        }
    }

    /**
     * Log connection properties in a consistent format
     */
    public static void logConnectionProperties(Properties props) {
        if (loggingService.isDebugEnabled()) {
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
            loggingService.debug(sb.toString());
        }
    }
}