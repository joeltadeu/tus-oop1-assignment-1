package com.lms.library.service;

import com.lms.library.dto.LoanRequest;
import com.lms.library.exception.ItemNotAvailableException;
import com.lms.library.exception.ItemNotFoundException;
import com.lms.library.exception.LoanNotFoundException;
import com.lms.library.exception.MemberNotFoundException;
import com.lms.library.model.*;
import java.util.List;

/**
 * Service interface for loan management operations.
 * Defines the contract for loan-related business logic.
 *
 * @author Joel Silva
 * @version 1.0
 * @see LoanServiceImpl
 * @since 2025
 */
public interface LoanService {
    /**
     * Checks out items for a member, creating a new loan.
     *
     * @param memberId    the ID of the member checking out items
     * @param loanRequest the request containing item IDs to checkout
     * @return the created loan with all items
     * @throws MemberNotFoundException   if member is not found
     * @throws ItemNotFoundException     if any item is not found
     * @throws ItemNotAvailableException if any item is not available
     */
    Loan checkoutItems(Long memberId, LoanRequest loanRequest);

    /**
     * Retrieves all loans for a specific member.
     *
     * @param memberId the ID of the member
     * @return list of loans for the member, ordered by loan date descending
     * @throws MemberNotFoundException if member is not found
     */
    List<Loan> getMemberLoans(Long memberId);

    /**
     * Retrieves a specific loan by its ID.
     *
     * @param loanId the ID of the loan to retrieve
     * @return the loan with member and items information
     * @throws LoanNotFoundException if loan is not found
     */
    Loan getLoanById(Long loanId);

    /**
     * Processes the return of items for a loan.
     *
     * @param loanId  the ID of the loan to return items from
     * @param itemIds list of specific item IDs to return, or empty list to return all items
     * @return the updated loan with return status
     * @throws LoanNotFoundException if loan is not found
     * @throws ItemNotFoundException if any specified item is not found in the loan
     * @throws IllegalStateException if loan is already closed
     */
    Loan returnItems(Long loanId, List<Long> itemIds);
}
