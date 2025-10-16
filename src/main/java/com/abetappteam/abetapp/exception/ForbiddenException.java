package com.abetappteam.abetapp.exception;

/**
 * Exception thrown when user is authenticated but lacks permission.
 * Returns HTTP 403 status code.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
