package com.lms.library.dto;

import java.time.LocalDate;
import java.util.List;

public record LoanResponse(
        Long id,
        Long memberId,
        LocalDate loanDate,
        LocalDate expectedReturnDate,
        List<LoanItemResponse> items,
        String status
) {}
