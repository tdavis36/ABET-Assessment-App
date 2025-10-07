package com.abetappteam.abetapp.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when business validation fails.
 * Returns HTTP 422 status code with field-level validation errors.
 */
public class ValidationException extends RuntimeException {

    private Map<String, String> errors = new HashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String field, String error) {
        super("Validation failed");
        this.errors.put(field, error);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String field, String error) {
        this.errors.put(field, error);
    }
}
