package com.lms.library.service;

import com.lms.library.dto.LoanRequest;
import com.lms.library.exception.ItemNotAvailableException;
import com.lms.library.exception.ItemNotFoundException;
import com.lms.library.exception.LoanNotFoundException;
import com.lms.library.exception.MemberNotFoundException;
import com.lms.library.model.Loan;
import com.lms.library.model.LoanItem;
import com.lms.library.model.LoanStatus;
import com.lms.library.repository.LibraryItemRepository;
import com.lms.library.repository.LoanItemRepository;
import com.lms.library.repository.LoanRepository;
import com.lms.library.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the LoanService interface. Provides business logic for loan operations
 * including checkout, return, and retrieval.
 *
 * @author Joel Silva
 * @version 1.0
 * @see LoanService
 * @since 2025
 */
@Service
public class LoanServiceImpl implements LoanService {

  private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);

  private final MemberRepository memberRepository;
  private final LibraryItemRepository libraryItemRepository;
  private final LoanRepository loanRepository;
  private final LoanItemRepository loanItemRepository;

  /**
   * Constructs a new LoanServiceImpl with required dependencies.
   *
   * @param memberRepository repository for member operations
   * @param libraryItemRepository repository for library item operations
   * @param loanRepository repository for loan operations
   * @param loanItemRepository repository for loan item operations
   */
  public LoanServiceImpl(
      MemberRepository memberRepository,
      LibraryItemRepository libraryItemRepository,
      LoanRepository loanRepository,
      LoanItemRepository loanItemRepository) {
    this.memberRepository = memberRepository;
    this.libraryItemRepository = libraryItemRepository;
    this.loanRepository = loanRepository;
    this.loanItemRepository = loanItemRepository;
  }

  /** {@inheritDoc} */
  @Override
  public Loan checkoutItems(Long memberId, LoanRequest loanRequest) {
    var member =
        memberRepository
            .findById(memberId)
            .orElseThrow(
                () ->
                    new MemberNotFoundException(
                        "Member not found with ID: %s".formatted(memberId)));

    var loanDate = LocalDate.now();
    var expectedReturnDate = loanDate.plusDays(14);
    var loan = new Loan(member, loanDate, expectedReturnDate);

    for (Long itemId : loanRequest.items()) {
      // Use the available item query to avoid proxy issues
      var item =
          libraryItemRepository
              .findAvailableItemById(itemId)
              .orElseThrow(
                  () -> {
                    // If item exists but is not available
                    var unavailableItem =
                        libraryItemRepository
                            .findById(itemId)
                            .orElseThrow(
                                () ->
                                    new ItemNotFoundException(
                                        "Item not found with ID: %s".formatted(itemId)));
                    return new ItemNotAvailableException(
                        "Item '%s' is currently loaned out".formatted(unavailableItem.getTitle()));
                  });

      item.setAvailable(false);
      libraryItemRepository.save(item);

      LoanItem loanItem = new LoanItem(loan, item);
      loan.addItem(loanItem);

      log.info("Added item {} to loan for member {}", itemId, memberId);
    }

    var savedLoan = loanRepository.save(loan);
    loanItemRepository.saveAll(loan.getItems());

    log.info(
        "Created loan {} with {} items for member {}",
        savedLoan.getId(),
        loanRequest.items().size(),
        memberId);

    return savedLoan;
  }

  /** {@inheritDoc} */
  @Override
  public List<Loan> getMemberLoans(Long memberId) {
    if (!memberRepository.existsById(memberId)) {
      throw new MemberNotFoundException("Member not found with ID: %s".formatted(memberId));
    }

    return loanRepository.findByMemberIdOrderByLoanDateDesc(memberId);
  }

  /** {@inheritDoc} */
  @Override
  public Loan getLoanById(Long loanId) {
    return loanRepository
        .findByIdWithMemberAndItems(loanId)
        .orElseThrow(
            () -> new LoanNotFoundException("Loan not found with ID: %s".formatted(loanId)));
  }

  /** {@inheritDoc} */
  @Override
  public Loan returnItems(Long loanId, List<Long> itemIds) {
    var loan =
        loanRepository
            .findByIdWithItems(loanId)
            .orElseThrow(
                () -> new LoanNotFoundException("Loan not found with ID: %s".formatted(loanId)));

    if (loan.getStatus() == LoanStatus.CLOSED) {
      throw new IllegalStateException("Loan is already closed");
    }

    for (Long itemId : itemIds) {
      var loanItem =
          loanItemRepository
              .findByLoanIdAndItemId(loanId, itemId)
              .orElseThrow(
                  () ->
                      new ItemNotFoundException(
                          "Item %s not found in loan %s".formatted(itemId, loanId)));

      if (!loanItem.isReturned()) {
        loanItem.markReturned();
        loanItemRepository.save(loanItem);
        log.info("Returned item {} from loan {}", itemId, loanId);
      }
    }

    // Update loan status
    loan.updateStatus();
    Loan updatedLoan = loanRepository.save(loan);

    log.info("Updated loan {} status to {}", loanId, updatedLoan.getStatus());
    return updatedLoan;
  }
}
