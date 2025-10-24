package com.lms.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Loan> loans = new ArrayList<>();

    public Member(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Defensive copying for loans
    public List<Loan> getLoans() {
        return Collections.unmodifiableList(new ArrayList<>(loans));
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan);
    }
}
