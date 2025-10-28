package com.lms.library.model;

import java.util.*;

public class Member {

    private Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<Loan> loans = new ArrayList<>();

    public Member(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<Loan> getLoans() {
        return List.copyOf(loans);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan);
    }
}
