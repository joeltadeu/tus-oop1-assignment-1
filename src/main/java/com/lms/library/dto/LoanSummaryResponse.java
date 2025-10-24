package com.lms.library.dto;

import java.time.LocalDate;

public record LoanSummaryResponse(
        Long loanId,
        LocalDate loanDate,
        LocalDate expectedReturnDate,
        String status
) {}
