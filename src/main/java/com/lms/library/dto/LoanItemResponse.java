package com.lms.library.dto;

import java.time.LocalDate;

public record LoanItemResponse(
        Long id,
        String title,
        String type,
        LocalDate returnedDate
) {}
