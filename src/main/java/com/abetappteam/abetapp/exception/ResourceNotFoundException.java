package com.abetappteam.abetapp.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * Returns HTTP 404 status code when handled by BaseController.
 */
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructor with custom message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with resource details
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor with resource name and ID
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        this(resourceName, "id", id);
    }

    /**
     * Constructor with cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}