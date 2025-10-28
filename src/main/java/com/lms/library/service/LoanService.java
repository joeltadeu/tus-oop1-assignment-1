package com.lms.library.service;

import com.lms.library.dto.LoanRequest;
import com.lms.library.model.*;

import java.util.List;

public interface LoanService {
    Loan checkoutItems(Long memberId, LoanRequest loanRequest);
    List<Loan> getMemberLoans(Long memberId);
    Loan getLoanById(Long loanId);
    Loan returnItems(Long loanId, List<Long> itemIds);
}
