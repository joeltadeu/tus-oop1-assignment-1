/**
 * Represents an individual item within a loan.
 * Links a specific library item to a loan and tracks its return status.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Loan
 * @see LibraryItem
 * @since 2025
 */
package com.lms.library.model;

public enum LoanStatus {
    OPEN,
    CLOSED,
}
