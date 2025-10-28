package com.lms.library.model;

import java.time.LocalDate;

public class LoanItem {

    private Long id;
    private Loan loan;
    private LibraryItem item;
    private LocalDate returnedDate;

    public LoanItem() {}

    public LoanItem(Loan loan, LibraryItem item) {
        this.loan = loan;
        this.item = item;
        this.returnedDate = null;
    }

    public Long getId() {
        return id;
    }

    public Loan getLoan() {
        return loan;
    }

    public LibraryItem getItem() {
        return item;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isReturned() {
        return returnedDate != null;
    }

    public void markReturned() {
        this.returnedDate = LocalDate.now();
        this.item.setAvailable(true);
    }

    protected void setLoan(Loan loan) {
        this.loan = loan;
    }
}
