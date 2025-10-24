package com.lms.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private LocalDate expectedReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<LoanItem> items = new ArrayList<>();

    public Loan(Member member, LocalDate loanDate, LocalDate expectedReturnDate) {
        this.member = member;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = LoanStatus.OPEN;
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
