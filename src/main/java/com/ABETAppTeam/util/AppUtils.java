package com.ABETAppTeam.util;

import com.ABETAppTeam.service.LoggingService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility class providing standardized methods for logging, error handling, and timing
 * across the ABET Assessment Application.
 * 
 * This class serves as a facade for the LoggingService and provides additional
 * convenience methods for common operations.
 */
public class AppUtils {
    private static final LoggingService logger = LoggingService.getInstance();
    
    /**
     * Private constructor to prevent instantiation
     */
    private AppUtils() {
        // Utility class should not be instantiated
    }
    
    /*
     * Standardized Logging Methods
     */
    
    /**
     * Log a trace message
     * 
     * @param message The message to log
     */
    public static void trace(String message) {
        logger.trace(message);
    }
    
    /**
     * Log a trace message with parameters
     * 
     * @param format The message format
     * @param args The parameters
     */
    public static void trace(String format, Object... args) {
        logger.trace(format, args);
    }
    
    /**
     * Log a debug message
     * 
     * @param message The message to log
     */
    public static void debug(String message) {
        logger.debug(message);
    }
    
    /**
     * Log a debug message with parameters
     * 
     * @param format The message format
     * @param args The parameters
     */
    public static void debug(String format, Object... args) {
        logger.debug(format, args);
    }
    
    /**
     * Log an info message
     * 
     * @param message The message to log
     */
    public static void info(String message) {
        logger.info(message);
    }
    
    /**
     * Log an info message with parameters
     * 
     * @param format The message format
     * @param args The parameters
     */
    public static void info(String format, Object... args) {
        logger.info(format, args);
    }
    
    /**
     * Log a warning message
     * 
     * @param message The message to log
     */
    public static void warn(String message) {
        logger.warn(message);
    }
    
    /**
     * Log a warning message with parameters
     * 
     * @param format The message format
     * @param args The parameters
     */
    public static void warn(String format, Object... args) {
        logger.warn(format, args);
    }
    
    /**
     * Log an error message
     * 
     * @param message The message to log
     */
    public static void error(String message) {
        logger.error(message);
    }
    
    /**
     * Log an error message with parameters
     * 
     * @param format The message format
     * @param args The parameters
     */
    public static void error(String format, Object... args) {
        logger.error(format, args);
    }
    
    /*
     * Standardized Error Handling Methods
     */
    
    /**
     * Log a detailed error with exception and stack trace
     * 
     * @param message Error message
     * @param throwable The exception to log
     */
    public static void logError(String message, Throwable throwable) {
        logger.logError(message, throwable);
    }
    
    /**
     * Log a categorized application error with exception details
     * 
     * @param category Error category (e.g., "UI", "Database", "Network", "Security")
     * @param component Affected component or class
     * @param message Error message
     * @param throwable The exception to log
     */
    public static void logCategorizedError(String category, String component, String message, Throwable throwable) {
        logger.logCategorizedError(category, component, message, throwable);
    }
    
    /**
     * Log a critical application error that might require immediate attention
     * 
     * @param message Error message
     * @param throwable The exception to log
     */
    public static void logCriticalError(String message, Throwable throwable) {
        logger.logCriticalError(message, throwable);
    }
    
    /**
     * Create a contextual exception log with user and request information
     * 
     * @param message Error message
     * @param throwable The exception to log
     * @param userId User ID (if available)
     * @param requestInfo Additional request information
     */
    public static void logContextualError(String message, Throwable throwable, String userId, Map<String, Object> requestInfo) {
        logger.logContextualError(message, throwable, userId, requestInfo);
    }
    
    /**
     * Create a contextual exception log with user and request information
     * 
     * @param message Error message
     * @param throwable The exception to log
     * @param userId User ID (if available)
     */
    public static void logContextualError(String message, Throwable throwable, String userId) {
        logger.logContextualError(message, throwable, userId, new HashMap<>());
    }
    
    /*
     * Standardized Timing Methods
     */
    
    /**
     * Start timing an operation
     * 
     * @param operationName Name of the operation to time
     * @return A unique timer ID to be used with stopTimer
     */
    public static String startTimer(String operationName) {
        return logger.startTimer(operationName);
    }
    
    /**
     * Stop timing an operation and log the duration
     * 
     * @param timerId The timer ID returned from startTimer
     * @param additionalInfo Optional additional information about the operation
     */
    public static void stopTimer(String timerId, String additionalInfo) {
        logger.stopTimer(timerId, additionalInfo);
    }
    
    /**
     * Stop timing an operation and log the duration without additional info
     * 
     * @param timerId The timer ID returned from startTimer
     */
    public static void stopTimer(String timerId) {
        logger.stopTimer(timerId, null);
    }
    
    /**
     * Execute a function and time its execution
     * 
     * @param <T> The return type of the function
     * @param operationName Name of the operation to time
     * @param function The function to execute
     * @return The result of the function
     */
    public static <T> T timeOperation(String operationName, Supplier<T> function) {
        String timerId = startTimer(operationName);
        try {
            T result = function.get();
            stopTimer(timerId);
            return result;
        } catch (Exception e) {
            stopTimer(timerId, "Operation failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Execute a runnable and time its execution
     * 
     * @param operationName Name of the operation to time
     * @param runnable The runnable to execute
     */
    public static void timeOperation(String operationName, Runnable runnable) {
        String timerId = startTimer(operationName);
        try {
            runnable.run();
            stopTimer(timerId);
        } catch (Exception e) {
            stopTimer(timerId, "Operation failed: " + e.getMessage());
            throw e;
        }
    }
    
    /*
     * Security Logging Methods
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
    public static void logSecurityEvent(String eventType, String userId, String ipAddress, 
                                       String details, boolean success) {
        logger.logSecurityEvent(eventType, userId, ipAddress, details, success);
    }
    
    /**
     * Log an access event (page view, resource access, etc.)
     * 
     * @param userId User ID
     * @param ipAddress IP address
     * @param resource Resource being accessed
     * @param action Action performed
     */
    public static void logAccess(String userId, String ipAddress, String resource, String action) {
        logger.logAccess(userId, ipAddress, resource, action);
    }
    
    /**
     * Set user context for logging
     * Call this at the beginning of a request to add user info to all subsequent logs
     * 
     * @param userId User ID
     * @param userRole User role
     */
    public static void setUserContext(String userId, String userRole) {
        logger.setUserContext(userId, userRole);
    }
    
    /**
     * Clear user context
     * Call this at the end of a request
     */
    public static void clearUserContext() {
        logger.clearUserContext();
    }
}