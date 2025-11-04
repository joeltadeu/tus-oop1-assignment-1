package com.lms.library.dto;

import com.lms.library.model.LoanItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * Data Transfer Object for loan item responses.
 * Contains information about a specific item within a loan.
 *
 * @param id           the unique identifier of the item
 * @param title        the title of the item
 * @param type         the type of item (BOOK or JOURNAL)
 * @param returnedDate the date when the item was returned, null if not returned
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@Schema(description = "Details of a loan item")
public record LoanItemResponse(
        @Schema(description = "Unique identifier of the item", example = "789")
        Long id,

        @Schema(description = "Title of the item", example = "The Great Gatsby")
        String title,

        @Schema(description = "Type of the item", example = "BOOK", allowableValues = {"BOOK", "JOURNAL"})
        String type,

        @Schema(description = "Date when the item was returned, null if not returned", example = "2024-01-20")
        LocalDate returnedDate
) {

    /**
     * Static factory method to create a LoanItemResponse from a LoanItem entity.
     *
     * @param loanItem the loan item entity to convert
     * @return a new LoanItemResponse instance
     */
    public static LoanItemResponse from(LoanItem loanItem) {
        return new LoanItemResponse(
                loanItem.getItem().getId(),
                loanItem.getItem().getTitle(),
                loanItem.getItem().getType(),
                loanItem.getReturnedDate()
        );
    }
}
