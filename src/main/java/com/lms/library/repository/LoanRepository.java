package com.lms.library.repository;

import com.lms.library.model.Loan;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing Loan entities. Provides data access operations for loans using an
 * in-memory store.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Loan
 * @since 2025
 */
@Repository
public class LoanRepository {
  private static final Map<Long, Loan> STORE = new ConcurrentHashMap<>();
  private static final AtomicLong ID_SEQ = new AtomicLong(1);

  /** Default constructor for LoanRepository. */
  public LoanRepository() {}

  /**
   * Saves a loan to the repository. If the loan has no ID, generates a new one automatically.
   *
   * @param loan the loan to save
   * @return the saved loan with generated ID
   */
  public Loan save(Loan loan) {
    if (loan.getId() == null) {
      loan.setId(ID_SEQ.getAndIncrement());
    }
    STORE.put(loan.getId(), loan);
    return loan;
  }

  /**
   * Finds a loan by its ID.
   *
   * @param id the ID of the loan to find
   * @return an Optional containing the found loan, or empty if not found
   */
  public Optional<Loan> findById(Long id) {
    return Optional.ofNullable(id).map(STORE::get);
  }

  /**
   * Finds all loans for a specific member, ordered by loan date (newest first).
   *
   * @param memberId the ID of the member
   * @return a list of loans for the member, sorted by loan date descending
   */
  public List<Loan> findByMemberIdOrderByLoanDateDesc(Long memberId) {
    return STORE.values().stream()
        .filter(l -> l.getMember().getId().equals(memberId))
        .sorted(Comparator.comparing(Loan::getLoanDate).reversed())
        .toList();
  }

  /**
   * Finds a loan by ID including member and items information. In this in-memory implementation,
   * equivalent to findById.
   *
   * @param id the ID of the loan to find
   * @return an Optional containing the found loan with member and items, or empty if not found
   */
  public Optional<Loan> findByIdWithMemberAndItems(Long id) {
    return findById(id); // no lazy loading needed
  }

  /**
   * Finds a loan by ID including items information. In this in-memory implementation, equivalent to
   * findById.
   *
   * @param id the ID of the loan to find
   * @return an Optional containing the found loan with items, or empty if not found
   */
  public Optional<Loan> findByIdWithItems(Long id) {
    return findById(id);
  }
}
