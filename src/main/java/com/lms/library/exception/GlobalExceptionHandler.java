package com.lms.library.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the Library Management System.
 * Provides centralized exception handling and standardized error responses for REST APIs.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Default constructor for GlobalExceptionHandler.
     */
    public GlobalExceptionHandler() {
    }

    /**
     * Handles resource not found exceptions (Member, Item, Loan not found).
     *
     * @param ex      the exception that was thrown
     * @param request the HTTP request that caused the exception
     * @return ResponseEntity with NOT_FOUND status and error details
     */
    @ExceptionHandler({
            MemberNotFoundException.class,
            ItemNotFoundException.class,
            LoanNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundExceptions(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        var apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles item not available exceptions.
     *
     * @param ex      the ItemNotAvailableException that was thrown
     * @param request the HTTP request that caused the exception
     * @return ResponseEntity with CONFLICT status and error details
     */
    @ExceptionHandler(ItemNotAvailableException.class)
    public ResponseEntity<ApiError> handleItemNotAvailable(
            ItemNotAvailableException ex, HttpServletRequest request) {
        log.warn("Item not available: {}", ex.getMessage());
        var apiError = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * Handles illegal argument exceptions (validation errors).
     *
     * @param ex      the IllegalArgumentException that was thrown
     * @param request the HTTP request that caused the exception
     * @return ResponseEntity with BAD_REQUEST status and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Invalid argument: {}", ex.getMessage());
        var apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles illegal state exceptions (business rule violations).
     *
     * @param ex      the IllegalStateException that was thrown
     * @param request the HTTP request that caused the exception
     * @return ResponseEntity with CONFLICT status and error details
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        log.warn("Illegal state: {}", ex.getMessage());
        var apiError = new ApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex      the exception that was thrown
     * @param request the HTTP request that caused the exception
     * @return ResponseEntity with INTERNAL_SERVER_ERROR status and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        var apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
