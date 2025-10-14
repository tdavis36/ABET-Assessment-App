package com.abetappteam.abetapp.exception;

/**
 * Exception thrown when a request conflicts with existing data.
 * Returns HTTP 409 status code (e.g., duplicate email, conflicting state).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}