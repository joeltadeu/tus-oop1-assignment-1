package com.lms.library.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for error responses.
 * Provides standardized error information for API consumers.
 *
 * @param timestamp the date and time when the error occurred
 * @param status    the HTTP status code
 * @param error     the error type description
 * @param message   the detailed error message
 * @param path      the API path where the error occurred
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@Schema(description = "Error response")
public record ApiError(
        @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Error type", example = "Not Found")
        String error,

        @Schema(description = "Detailed error message", example = "Member not found with id: 123")
        String message,

        @Schema(description = "API path where the error occurred", example = "/v1/members/123/loans")
        String path
) {

    /**
     * Constructs a new ApiError with the specified details.
     *
     * @param status  the HTTP status code
     * @param error   the error type
     * @param message the detailed error message
     * @param path    the API path where the error occurred
     */
    public ApiError(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}
