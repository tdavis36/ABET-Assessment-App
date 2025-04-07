package com.ABETAppTeam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * JDBC Logger proxy to intercept and format JDBC driver logs
 * This helps make the native Java SQL logs look similar to our custom SQL logging format
 */
public class JDBCLogger {

    private static final Logger logger = LoggerFactory.getLogger(JDBCLogger.class);

    /**
     * Initialize the JDBC logging system
     * Call this at application startup to enable JDBC driver logging
     */
    public static void initialize() {
        // Set system properties to enable JDBC driver logging

        // If using Log4j, you could use:
        // System.setProperty("org.mariadb.jdbc.logging.type", "log4j");

        // With our SLF4J/Logback setup, use:
        System.setProperty("org.mariadb.jdbc.logging.type", "slf4j");

        // Configure more verbose logging if needed
        // System.setProperty("mariadb.logging.level", "FINEST");

        // Log that we've initialized the JDBC logging
        logger.info("JDBC driver logging initialized");
    }

    /**
     * Log a connection creation
     */
    public static void logConnectionCreated(Connection conn) {
        if (logger.isDebugEnabled()) {
            try {
                String url = conn.getMetaData().getURL();
                String user = conn.getMetaData().getUserName();
                // Sanitize the URL to remove password
                url = SQLExceptionHandler.sanitizeJdbcUrl(url);
                logger.debug("SQL Connection created: {} (User: {})", url, user);
            } catch (Exception e) {
                logger.debug("SQL Connection created (Unable to get details: {})", e.getMessage());
            }
        }
    }

    /**
     * Log a connection close
     */
    public static void logConnectionClosed(Connection conn) {
        if (logger.isDebugEnabled()) {
            logger.debug("SQL Connection closed");
        }
    }

    /**
     * Log a statement creation
     */
    public static void logStatementCreated(Statement stmt, String sql) {
        if (logger.isDebugEnabled()) {
            logger.debug("SQL Statement created: {}", sql);
        }
    }

    /**
     * Log statement execution
     */
    public static void logStatementExecuted(Statement stmt, String sql, long executionTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("SQL Statement executed in {}ms: {}", executionTime, sql);
        }
    }

    /**
     * Log result set fetch
     */
    public static void logResultSetFetched(ResultSet rs, int rowCount, long fetchTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("SQL ResultSet fetched {} rows in {}ms", rowCount, fetchTime);
        }
    }

    /**
     * Log connection properties in a consistent format
     */
    public static void logConnectionProperties(Properties props) {
        if (logger.isDebugEnabled()) {
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
            logger.debug(sb.toString());
        }
    }
}