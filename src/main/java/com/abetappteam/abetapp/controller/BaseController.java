package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * Base controller class providing common REST API functionality.
 * All API controllers should extend this class.
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // Default pagination values
    protected static final int DEFAULT_PAGE_SIZE = 20;
    protected static final int MAX_PAGE_SIZE = 100;
    protected static final String DEFAULT_SORT_FIELD = "id";
    protected static final String DEFAULT_SORT_DIRECTION = "asc";

    /**
     * Create a standardized success response with message
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Create a standardized created response
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Resource created successfully"));
    }

    /**
     * Create a standardized error response
     */
    protected ResponseEntity<ApiResponse<Object>> error(String message, HttpStatus status) {
        ApiResponse<Object> response = ApiResponse.error(message);
        response.setMessage(message);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Create a standardized validation error response
     */
    protected ResponseEntity<ApiResponse<Object>> validationError(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        bindingResult.getFieldErrors().forEach(error ->
                errorMessage.append(error.getField())
                        .append(" ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );

        return error(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Create standardized pageable object with validation
     */
    protected Pageable createPageable(int page, int size, String sort, String direction) {
        // Validate and adjust page parameters
        if (page < 0) page = 0;
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        if (size > MAX_PAGE_SIZE) size = MAX_PAGE_SIZE;

        // Determine sort direction
        String sortDir = (direction != null && !direction.trim().isEmpty()) ? direction : DEFAULT_SORT_DIRECTION;
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Determine sort field
        String sortField = (sort != null && !sort.trim().isEmpty()) ? sort : DEFAULT_SORT_FIELD;
        Sort sortObj = Sort.by(sortDirection, sortField);
        return PageRequest.of(page, size, sortObj);
    }

    /**
     * Create standardized paged response
     */
    protected <T> ResponseEntity<PagedResponse<T>> pagedSuccess(Page<T> page) {
        PagedResponse<T> response = new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Validate required path variables
     */
    protected void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID provided");
        }
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle bad request exceptions
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle conflict exceptions (duplicate data, etc.)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
        logger.warn("Conflict: {}", ex.getMessage());
        return error(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handle unauthorized exceptions
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
        logger.warn("Unauthorized access attempt: {}", ex.getMessage());
        return error(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle forbidden exceptions
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(ForbiddenException ex) {
        logger.warn("Forbidden access attempt: {}", ex.getMessage());
        return error(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handle validation exceptions with field errors
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(ValidationException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        if (!ex.getErrors().isEmpty()) {
            response.setData(ex.getErrors());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Handle business logic exceptions
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /**
     * Handle illegal arguments
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        return validationError(ex.getBindingResult());
    }

    /**
     * Generic exception handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return error("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Health check endpoint that can be overridden
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return success("Service is healthy", "Health check passed");
    }
}