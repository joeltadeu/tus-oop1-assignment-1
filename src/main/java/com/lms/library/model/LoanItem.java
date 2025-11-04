package com.lms.library.model;

import java.time.LocalDate;

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
public class LoanItem {

    private Long id;
    private Loan loan;
    private LibraryItem item;
    private LocalDate returnedDate;

    /**
     * Default constructor for LoanItem.
     * Required for serialization and dependency injection frameworks.
     */
    public LoanItem() {
    }

    /**
     * Constructs a new LoanItem linking a loan and a library item.
     *
     * @param loan the loan that contains this item
     * @param item the library item being borrowed
     */
    public LoanItem(Loan loan, LibraryItem item) {
        this.loan = loan;
        this.item = item;
        this.returnedDate = null;
    }

    /**
     * Gets the unique identifier of the loan item.
     *
     * @return the loan item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the loan that contains this item.
     *
     * @return the loan entity
     */
    public Loan getLoan() {
        return loan;
    }

    /**
     * Gets the library item associated with this loan item.
     *
     * @return the library item entity
     */
    public LibraryItem getItem() {
        return item;
    }

    /**
     * Gets the date when this item was returned.
     *
     * @return the return date, or null if not yet returned
     */
    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    /**
     * Sets the unique identifier of the loan item.
     *
     * @param id the new ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if this item has been returned.
     *
     * @return true if the item has been returned, false otherwise
     */
    public boolean isReturned() {
        return returnedDate != null;
    }

    /**
     * Marks this item as returned and updates the library item availability.
     * Sets the returned date to current date and marks the library item as available.
     */
    public void markReturned() {
        this.returnedDate = LocalDate.now();
        this.item.setAvailable(true);
    }

    /**
     * Sets the loan for this item (package-private for bidirectional relationship management).
     *
     * @param loan the loan to associate with this item
     */
    protected void setLoan(Loan loan) {
        this.loan = loan;
    }
}
