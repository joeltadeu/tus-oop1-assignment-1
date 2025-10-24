package com.lms.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "loan_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private LibraryItem item;

    private LocalDate returnedDate;

    public LoanItem(Loan loan, LibraryItem item) {
        this.loan = loan;
        this.item = item;
        this.returnedDate = null;
    }

    public boolean isReturned() {
        return returnedDate != null;
    }

    public void markReturned() {
        this.returnedDate = LocalDate.now();
        this.item.setAvailable(true);
    }

    // Protected setter for loan (used by Loan entity)
    protected void setLoan(Loan loan) {
        this.loan = loan;
    }
}
