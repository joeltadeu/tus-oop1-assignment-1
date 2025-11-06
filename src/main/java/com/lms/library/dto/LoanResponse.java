package com.lms.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for detailed loan responses.
 * Contains complete information about a loan including all items.
 *
 * @param id the unique identifier of the loan
 * @param memberId the ID of the member who borrowed the items
 * @param loanDate the date when the loan was created
 * @param expectedReturnDate the expected return date for the items
 * @param items the list of items in the loan
 * @param status the current status of the loan (OPEN or CLOSED)
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@Schema(description = "Response containing loan details")
public record LoanResponse(
        @Schema(description = "Unique identifier of the loan", example = "123")
        Long id,

        @Schema(description = "ID of the member who borrowed the items", example = "456")
        Long memberId,

        @Schema(description = "Date when the loan was created", example = "2024-01-15")
        LocalDate loanDate,

        @Schema(description = "Expected return date for the items", example = "2024-02-15")
        LocalDate expectedReturnDate,

        @Schema(description = "List of items in the loan")
        List<LoanItemResponse> items,

        @Schema(description = "Current status of the loan", example = "OPEN", allowableValues = {"OPEN", "CLOSED"})
        String status
) {
}
