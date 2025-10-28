package com.lms.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Loan {

    private Long id;
    private Member member;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LoanStatus status = LoanStatus.OPEN;
    private final List<LoanItem> items = new ArrayList<>();

    public Loan() {}

    public Loan(Member member, LocalDate loanDate, LocalDate expectedReturnDate) {
        this.member = member;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public List<LoanItem> getItems() {
        return List.copyOf(items);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addItem(LoanItem item) {
        this.items.add(item);
        item.setLoan(this);
    }

    public void updateStatus() {
        boolean allReturned = items.stream().allMatch(LoanItem::isReturned);
        this.status = allReturned ? LoanStatus.CLOSED : LoanStatus.OPEN;
    }
}
