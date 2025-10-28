package com.lms.library.controller;

import com.lms.library.dto.*;
import com.lms.library.model.Loan;
import com.lms.library.model.LoanItem;
import com.lms.library.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/members/{memberId}/loans")
    public ResponseEntity<LoanResponse> checkoutItems(
            @PathVariable Long memberId,
            @RequestBody LoanRequest loanRequest) {

        log.info("Checkout request for member {}: {} items", memberId, loanRequest.items().size());
        Loan loan = loanService.checkoutItems(memberId, loanRequest);

        // Convert to response
        List<LoanItemResponse> itemResponses = loan.getItems().stream()
                .map(this::convertToLoanItemResponse)
                .toList();

        LoanResponse response = new LoanResponse(
                loan.getId(),
                loan.getMember().getId(),
                loan.getLoanDate(),
                loan.getExpectedReturnDate(),
                itemResponses,
                loan.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/members/{memberId}/loans")
    public ResponseEntity<List<LoanSummaryResponse>> getMemberLoans(@PathVariable Long memberId) {
        log.info("Fetching loans for member {}", memberId);
        List<Loan> loans = loanService.getMemberLoans(memberId);

        List<LoanSummaryResponse> responses = loans.stream()
                .map(loan -> new LoanSummaryResponse(
                        loan.getId(),
                        loan.getLoanDate(),
                        loan.getExpectedReturnDate(),
                        loan.getStatus().name()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/loans/{loanId}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long loanId) {
        log.info("Fetching loan details for loan {}", loanId);
        Loan loan = loanService.getLoanById(loanId);

        List<LoanItemResponse> itemResponses = loan.getItems().stream()
                .map(this::convertToLoanItemResponse)
                .toList();

        LoanResponse response = new LoanResponse(
                loan.getId(),
                loan.getMember().getId(),
                loan.getLoanDate(),
                loan.getExpectedReturnDate(),
                itemResponses,
                loan.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/loans/{loanId}/returns")
    public ResponseEntity<LoanResponse> returnItems(
            @PathVariable Long loanId,
            @RequestBody(required = false) LoanRequest returnRequest) {

        log.info("Return request for loan {}", loanId);

        var returnedLoan = loanService.returnItems(loanId, returnRequest.items());

        // Convert to response
        List<LoanItemResponse> itemResponses = returnedLoan.getItems().stream()
                .map(this::convertToLoanItemResponse)
                .toList();

        LoanResponse response = new LoanResponse(
                returnedLoan.getId(),
                returnedLoan.getMember().getId(),
                returnedLoan.getLoanDate(),
                returnedLoan.getExpectedReturnDate(),
                itemResponses,
                returnedLoan.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    private LoanItemResponse convertToLoanItemResponse(LoanItem loanItem) {
        return new LoanItemResponse(
                loanItem.getItem().getId(),
                loanItem.getItem().getTitle(),
                loanItem.getItem().getType(),
                loanItem.getReturnedDate()
        );
    }
}
