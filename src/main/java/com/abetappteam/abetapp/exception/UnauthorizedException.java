package com.abetappteam.abetapp.exception;

/**
 * Exception thrown when authentication is required but not provided or invalid.
 * Returns HTTP 401 status code.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}