package com.lms.library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a library member who can borrow items. Contains personal information and maintains a
 * list of current and past loans.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Loan
 * @since 2025
 */
public class Member {

  private Long id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final List<Loan> loans = new ArrayList<>();

  /**
   * Constructs a new Member with personal information.
   *
   * @param firstName the member's first name
   * @param lastName the member's last name
   * @param email the member's email address
   */
  public Member(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  /**
   * Gets the unique identifier of the member.
   *
   * @return the member ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Gets the member's first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Gets the member's last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Gets the member's email address.
   *
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets an immutable list of the member's loans.
   *
   * @return an unmodifiable list of loans
   */
  public List<Loan> getLoans() {
    return List.copyOf(loans);
  }

  /**
   * Sets the unique identifier of the member.
   *
   * @param id the new ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Adds a loan to the member's loan history.
   *
   * @param loan the loan to add
   */
  public void addLoan(Loan loan) {
    this.loans.add(loan);
  }
}
