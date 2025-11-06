package com.lms.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a loan transaction in the library system. Contains information about the member, loan
 * dates, status, and items being borrowed.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Member
 * @see LoanItem
 * @see LoanStatus
 * @since 2025
 */
public class Loan {

  private Long id;
  private Member member;
  private LocalDate loanDate;
  private LocalDate expectedReturnDate;
  private LoanStatus status = LoanStatus.OPEN;
  private final List<LoanItem> items = new ArrayList<>();

  /**
   * Default constructor for Loan. Required for serialization and dependency injection frameworks.
   */
  public Loan() {}

  /**
   * Constructs a new Loan with specified member and dates.
   *
   * @param member the member who is borrowing the items
   * @param loanDate the date when the loan is created
   * @param expectedReturnDate the expected return date for the items
   */
  public Loan(Member member, LocalDate loanDate, LocalDate expectedReturnDate) {
    this.member = member;
    this.loanDate = loanDate;
    this.expectedReturnDate = expectedReturnDate;
  }

  /**
   * Gets the unique identifier of the loan.
   *
   * @return the loan ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Gets the member who borrowed the items.
   *
   * @return the member entity
   */
  public Member getMember() {
    return member;
  }

  /**
   * Gets the date when the loan was created.
   *
   * @return the loan date
   */
  public LocalDate getLoanDate() {
    return loanDate;
  }

  /**
   * Gets the expected return date for the items.
   *
   * @return the expected return date
   */
  public LocalDate getExpectedReturnDate() {
    return expectedReturnDate;
  }

  /**
   * Gets the current status of the loan.
   *
   * @return the loan status
   */
  public LoanStatus getStatus() {
    return status;
  }

  /**
   * Gets the current status of the loan.
   *
   * @return the loan status
   */
  public List<LoanItem> getItems() {
    return List.copyOf(items);
  }

  /**
   * Sets the unique identifier of the loan.
   *
   * @param id the new ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Adds a loan item to this loan and establishes the bidirectional relationship.
   *
   * @param item the loan item to add
   */
  public void addItem(LoanItem item) {
    this.items.add(item);
    item.setLoan(this);
  }

  /**
   * Updates the loan status based on the return status of all items. If all items are returned,
   * sets status to CLOSED; otherwise, sets to OPEN.
   */
  public void updateStatus() {
    boolean allReturned = items.stream().allMatch(LoanItem::isReturned);
    this.status = allReturned ? LoanStatus.CLOSED : LoanStatus.OPEN;
  }
}
