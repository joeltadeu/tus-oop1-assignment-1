package com.lms.library.controller;

import com.lms.library.dto.LoanItemResponse;
import com.lms.library.dto.LoanRequest;
import com.lms.library.dto.LoanResponse;
import com.lms.library.dto.LoanSummaryResponse;
import com.lms.library.exception.*;
import com.lms.library.model.Loan;
import com.lms.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing loan operations in the Library Management System.
 * Provides endpoints for checking out items, returning items, and retrieving loan information.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "Loan Management", description = "APIs for managing book loans and returns")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    /**
     * Constructs a new LoanController with the required LoanService.
     *
     * @param loanService the loan service to be used by the controller
     */
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Creates a new loan for a member with the specified items.
     *
     * @param memberId    the ID of the member checking out items
     * @param loanRequest the request containing list of item IDs to checkout
     * @return ResponseEntity containing the created loan details
     * @throws MemberNotFoundException   if the member is not found
     * @throws ItemNotFoundException     if any item is not found
     * @throws ItemNotAvailableException if any item is not available for checkout
     */
    @Operation(
            summary = "Checkout items for a member",
            description = "Creates a new loan for a member with the specified items"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Items checked out successfully",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member or item not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Item not available for checkout",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/members/{memberId}/loans")
    public ResponseEntity<LoanResponse> checkoutItems(
            @PathVariable Long memberId,
            @Valid @RequestBody LoanRequest loanRequest) {

        log.info("Checkout request for member {}: {} items", memberId, loanRequest.items().size());
        Loan loan = loanService.checkoutItems(memberId, loanRequest);

        // Convert to response
        List<LoanItemResponse> itemResponses = loan.getItems().stream()
                .map(LoanItemResponse::from)
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

    /**
     * Retrieves all loans for a specific member.
     *
     * @param memberId the ID of the member
     * @return ResponseEntity containing list of loan summaries for the member
     * @throws MemberNotFoundException if the member is not found
     */
    @Operation(
            summary = "Get member loans",
            description = "Retrieves all loans for a specific member"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loans retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LoanSummaryResponse[].class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
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

    /**
     * Retrieves detailed information about a specific loan.
     *
     * @param loanId the ID of the loan to retrieve
     * @return ResponseEntity containing detailed loan information
     * @throws LoanNotFoundException if the loan is not found
     */
    @Operation(
            summary = "Get loan details",
            description = "Retrieves detailed information about a specific loan"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/loans/{loanId}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long loanId) {
        log.info("Fetching loan details for loan {}", loanId);
        Loan loan = loanService.getLoanById(loanId);

        List<LoanItemResponse> itemResponses = loan.getItems().stream()
                .map(LoanItemResponse::from)
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

    /**
     * Processes the return of items for a loan.
     * If no specific items are provided, all items in the loan are returned.
     *
     * @param loanId        the ID of the loan to return items from
     * @param returnRequest optional list of specific item IDs to return
     * @return ResponseEntity containing updated loan details
     * @throws LoanNotFoundException if the loan is not found
     * @throws ItemNotFoundException if any specified item is not found in the loan
     * @throws IllegalStateException if the loan is already closed
     */
    @Operation(
            summary = "Return items",
            description = "Processes the return of items for a loan. If no items specified, all items are returned."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Items returned successfully",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid loan state",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping("/loans/{loanId}/returns")
    public ResponseEntity<LoanResponse> returnItems(
            @PathVariable Long loanId,
            @RequestBody(required = false) LoanRequest returnRequest) {

        log.info("Return request for loan {}", loanId);

        var returnedLoan = loanService.returnItems(loanId, returnRequest.items());

        // Convert to response
        List<LoanItemResponse> itemResponses = returnedLoan.getItems().stream()
                .map(LoanItemResponse::from)
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
}
