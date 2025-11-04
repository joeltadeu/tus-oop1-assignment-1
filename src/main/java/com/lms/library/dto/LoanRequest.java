/**
 * Data Transfer Object for loan creation requests.
 * Contains the list of item IDs to be included in a new loan.
 *
 * @param items the list of item IDs to be checked out
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
package com.lms.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request for creating a loan")
public record LoanRequest(
        @Schema(description = "List of item IDs to include in the loan", example = "[1, 2, 3]")
        @NotEmpty(message = "Items list cannot be empty")
        List<Long> items) {
    public LoanRequest {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty");
        }
    }
}
