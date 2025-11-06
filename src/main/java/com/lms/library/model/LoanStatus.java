package com.lms.library.model;

/**
 * Represents an individual item within a loan. Links a specific library item to a loan and tracks
 * its return status.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Loan
 * @see LibraryItem
 * @since 2025
 */
public enum LoanStatus {
  /** Loan was created and items are currently borrowed. */
  OPEN,

  /** All items have been returned and loan is closed. */
  CLOSED,
}
