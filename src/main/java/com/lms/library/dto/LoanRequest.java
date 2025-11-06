package com.lms.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Data Transfer Object for loan creation requests.
 * Contains the list of item IDs to be included in a new loan.
 *
 * @param items the list of item IDs to be checked out
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@Schema(description = "Request for creating a loan")
public record LoanRequest(
        @Schema(description = "List of item IDs to include in the loan", example = "[1, 2, 3]")
        @NotEmpty(message = "Items list cannot be empty")
        List<Long> items) {

    /**
     * Compact constructor for LoanRequest that validates the items list.
     *
     * @throws IllegalArgumentException if the items list is null or empty
     */
    public LoanRequest {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty");
        }
    }
}
