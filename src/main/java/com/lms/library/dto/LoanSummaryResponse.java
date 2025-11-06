package com.lms.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * Data Transfer Object for loan summary responses.
 * Contains basic information about a loan without item details.
 *
 * @param loanId the unique identifier of the loan
 * @param loanDate the date when the loan was created
 * @param expectedReturnDate the expected return date for the items
 * @param status the current status of the loan (OPEN or CLOSED)
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@Schema(description = "Summary of a loan")
public record LoanSummaryResponse(
        @Schema(description = "Unique identifier of the loan", example = "123")
        Long loanId,

        @Schema(description = "Date when the loan was created", example = "2024-01-15")
        LocalDate loanDate,

        @Schema(description = "Expected return date", example = "2024-02-15")
        LocalDate expectedReturnDate,

        @Schema(description = "Current status of the loan", example = "OPEN")
        String status
) {
}
