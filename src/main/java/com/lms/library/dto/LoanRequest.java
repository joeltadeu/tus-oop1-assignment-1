package com.lms.library.dto;

import java.util.List;

public record LoanRequest(List<Long> items) {
    public LoanRequest {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty");
        }
    }
}
