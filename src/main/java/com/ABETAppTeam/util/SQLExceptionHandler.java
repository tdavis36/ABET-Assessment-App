package com.ABETAppTeam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling SQL exceptions
 * Provides detailed analysis and helpful messages for SQL errors
 */
public class SQLExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SQLExceptionHandler.class);

    // Common SQL state codes and their meanings
    private static final Map<String, String> SQL_STATE_MEANINGS = new HashMap<>();

    // Common MariaDB/MySQL error codes and their meanings
    private static final Map<Integer, ErrorInfo> ERROR_CODES = new HashMap<>();

    static {
        // Initialize SQL state meanings
        SQL_STATE_MEANINGS.put("01000", "General warning");
        SQL_STATE_MEANINGS.put("01004", "String data, right truncation");
        SQL_STATE_MEANINGS.put("01006", "Privilege not revoked");
        SQL_STATE_MEANINGS.put("01007", "Privilege not granted");
        SQL_STATE_MEANINGS.put("08000", "Connection exception");
        SQL_STATE_MEANINGS.put("08001", "SQL client unable to establish SQL connection");
        SQL_STATE_MEANINGS.put("08003", "Connection does not exist");
        SQL_STATE_MEANINGS.put("08004", "SQL server rejected SQL connection");
        SQL_STATE_MEANINGS.put("08006", "Connection failure");
        SQL_STATE_MEANINGS.put("08007", "Transaction resolution unknown");
        SQL_STATE_MEANINGS.put("0A000", "Feature not supported");
        SQL_STATE_MEANINGS.put("22000", "Data exception");
        SQL_STATE_MEANINGS.put("22001", "String data, right truncation");
        SQL_STATE_MEANINGS.put("22003", "Numeric value out of range");
        SQL_STATE_MEANINGS.put("22007", "Invalid datetime format");
        SQL_STATE_MEANINGS.put("22012", "Division by zero");
        SQL_STATE_MEANINGS.put("22018", "Invalid character value for cast");
        SQL_STATE_MEANINGS.put("22027", "Trim error");
        SQL_STATE_MEANINGS.put("23000", "Integrity constraint violation");
        SQL_STATE_MEANINGS.put("23001", "Restrict violation");
        SQL_STATE_MEANINGS.put("23502", "Not null violation");
        SQL_STATE_MEANINGS.put("23503", "Foreign key violation");
        SQL_STATE_MEANINGS.put("23505", "Unique violation");
        SQL_STATE_MEANINGS.put("23514", "Check violation");
        SQL_STATE_MEANINGS.put("24000", "Invalid cursor state");
        SQL_STATE_MEANINGS.put("25000", "Invalid transaction state");
        SQL_STATE_MEANINGS.put("28000", "Invalid authorization specification");
        SQL_STATE_MEANINGS.put("42000", "Syntax error or access rule violation");
        SQL_STATE_MEANINGS.put("42601", "Syntax error");
        SQL_STATE_MEANINGS.put("42P01", "Table not found");
        SQL_STATE_MEANINGS.put("42S01", "Base table or view already exists");
        SQL_STATE_MEANINGS.put("42S02", "Base table or view not found");
        SQL_STATE_MEANINGS.put("HY000", "General error");
        SQL_STATE_MEANINGS.put("HY001", "Memory allocation error");
        SQL_STATE_MEANINGS.put("HYT00", "Timeout expired");
        SQL_STATE_MEANINGS.put("HYT01", "Connection timeout expired");
        SQL_STATE_MEANINGS.put("40001", "Serialization failure");
        SQL_STATE_MEANINGS.put("40003", "Statement completion unknown");
        SQL_STATE_MEANINGS.put("IM001", "Driver does not support this function");
        SQL_STATE_MEANINGS.put("IM002", "Data source name not found and no default driver specified");

        // Initialize MariaDB/MySQL specific error codes
        ERROR_CODES.put(1040, new ErrorInfo("Too many connections", "The maximum number of connections to the database server has been reached. Try closing some connections or increase the maximum connection limit."));
        ERROR_CODES.put(1045, new ErrorInfo("Access denied", "The username or password is incorrect, or the user does not have sufficient privileges to connect."));
        ERROR_CODES.put(1049, new ErrorInfo("Unknown database", "The specified database does not exist."));
        ERROR_CODES.put(1051, new ErrorInfo("Unknown table", "The specified table does not exist."));
        ERROR_CODES.put(1054, new ErrorInfo("Unknown column", "The specified column does not exist in the table."));
        ERROR_CODES.put(1062, new ErrorInfo("Duplicate entry", "A duplicate key error occurred. The data you are trying to insert would create a duplicate entry in a unique index or primary key."));
        ERROR_CODES.put(1064, new ErrorInfo("SQL syntax error", "There is a syntax error in your SQL statement."));
        ERROR_CODES.put(1146, new ErrorInfo("Table doesn't exist", "The table you are trying to access does not exist."));
        ERROR_CODES.put(1213, new ErrorInfo("Deadlock found", "A deadlock occurred while trying to get a lock. Try restarting the transaction."));
        ERROR_CODES.put(1451, new ErrorInfo("Foreign key constraint fails (cannot delete/update)", "Cannot delete or update a parent row because a foreign key constraint exists."));
        ERROR_CODES.put(1452, new ErrorInfo("Foreign key constraint fails (cannot insert/update)", "Cannot add or update a child row because a foreign key constraint fails."));
        ERROR_CODES.put(2002, new ErrorInfo("Cannot connect to server", "The database server is not running or is not accessible."));
        ERROR_CODES.put(2003, new ErrorInfo("Cannot connect to server", "The database server hostname or IP address is incorrect, or the server is not running."));
        ERROR_CODES.put(2005, new ErrorInfo("Unknown host", "The database server hostname cannot be resolved."));
        ERROR_CODES.put(2006, new ErrorInfo("Server has gone away", "The connection to the database server was lost during the query."));
        ERROR_CODES.put(2013, new ErrorInfo("Lost connection during query", "The connection to the database server was lost during the query."));
    }

    /**
     * Analyze a SQLException and log detailed information about it
     *
     * @param e The SQLException to analyze
     * @param sql The SQL statement that caused the exception (optional)
     * @param params The parameters used in the query (optional)
     * @return A human-readable error message with suggestions
     */
    public static String analyzeException(SQLException e, String sql, Object[] params) {
        int errorCode = e.getErrorCode();
        String sqlState = e.getSQLState();
        String message = e.getMessage();

        StringBuilder analysis = new StringBuilder();
        analysis.append("SQL Error Analysis:\n");

        // Add basic error information
        analysis.append("- Error code: ").append(errorCode).append("\n");
        analysis.append("- SQL state: ").append(sqlState).append("\n");
        analysis.append("- Message: ").append(message).append("\n");

        // Add SQL state meaning if available
        if (sqlState != null && !sqlState.isEmpty() && SQL_STATE_MEANINGS.containsKey(sqlState)) {
            analysis.append("- SQL state meaning: ").append(SQL_STATE_MEANINGS.get(sqlState)).append("\n");
        }

        // Add detailed error information and suggestions if available
        if (ERROR_CODES.containsKey(errorCode)) {
            ErrorInfo errorInfo = ERROR_CODES.get(errorCode);
            analysis.append("- Error type: ").append(errorInfo.name()).append("\n");
            analysis.append("- Suggestion: ").append(errorInfo.suggestion()).append("\n");
        }

        // Add the SQL statement if provided
        if (sql != null && !sql.isEmpty()) {
            analysis.append("- SQL statement: ").append(sql).append("\n");
        }

        // Add the parameters if provided
        if (params != null && params.length > 0) {
            analysis.append("- Parameters: ");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    analysis.append(", ");
                }
                analysis.append(params[i]);
            }
            analysis.append("\n");
        }

        // Log the detailed analysis
        logger.error(analysis.toString(), e);

        // Return a simplified message for application use
        return createUserFriendlyMessage(errorCode, sqlState, message);
    }

    /**
     * Create a user-friendly error message based on the SQL error
     *
     * @param errorCode The SQL error code
     * @param sqlState The SQL state
     * @param message The original error message
     * @return A user-friendly error message
     */
    private static String createUserFriendlyMessage(int errorCode, String sqlState, String message) {
        // Create a simplified message for application use
        if (ERROR_CODES.containsKey(errorCode)) {
            ErrorInfo errorInfo = ERROR_CODES.get(errorCode);
            return errorInfo.name() + ": " + errorInfo.suggestion();
        } else if (sqlState != null && !sqlState.isEmpty() && SQL_STATE_MEANINGS.containsKey(sqlState)) {
            return SQL_STATE_MEANINGS.get(sqlState) + ": " + message;
        } else {
            return "Database error: " + message;
        }
    }

    /**
     * Create a JDBC URL for logging (with password removed)
     *
     * @param url The original JDBC URL
     * @return A sanitized JDBC URL
     */
    public static String sanitizeJdbcUrl(String url) {
        if (url == null) {
            return null;
        }

        // Remove password parameter if present
        return url.replaceAll("password=([^&]*)", "password=*****");
    }

    /**
         * Helper class to store error information
         */
        private record ErrorInfo(String name, String suggestion) {
    }
}