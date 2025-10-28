package com.lms.library.service;

import com.lms.library.dto.LoanRequest;
import com.lms.library.exception.ItemNotAvailableException;
import com.lms.library.exception.ItemNotFoundException;
import com.lms.library.exception.LoanNotFoundException;
import com.lms.library.exception.MemberNotFoundException;
import com.lms.library.model.*;
import com.lms.library.repository.LibraryItemRepository;
import com.lms.library.repository.LoanItemRepository;
import com.lms.library.repository.LoanRepository;
import com.lms.library.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final MemberRepository memberRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final LoanRepository loanRepository;
    private final LoanItemRepository loanItemRepository;

    public LoanServiceImpl(MemberRepository memberRepository, LibraryItemRepository libraryItemRepository, LoanRepository loanRepository, LoanItemRepository loanItemRepository) {
        this.memberRepository = memberRepository;
        this.libraryItemRepository = libraryItemRepository;
        this.loanRepository = loanRepository;
        this.loanItemRepository = loanItemRepository;
    }

    @Override
    public Loan checkoutItems(Long memberId, LoanRequest loanRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));

        LocalDate loanDate = LocalDate.now();
        LocalDate expectedReturnDate = loanDate.plusDays(14);

        Loan loan = new Loan(member, loanDate, expectedReturnDate);

        for (Long itemId : loanRequest.items()) {
            // Use the available item query to avoid proxy issues
            LibraryItem item = libraryItemRepository.findAvailableItemById(itemId)
                    .orElseThrow(() -> {
                        // If item exists but is not available
                        LibraryItem unavailableItem = libraryItemRepository.findById(itemId)
                                .orElseThrow(() -> new ItemNotFoundException("Item not found with ID: " + itemId));
                        return new ItemNotAvailableException(
                                "Item '" + unavailableItem.getTitle() + "' is currently loaned out"
                        );
                    });

            item.setAvailable(false);
            libraryItemRepository.save(item);

            LoanItem loanItem = new LoanItem(loan, item);
            loan.addItem(loanItem);

            log.info("Added item {} to loan for member {}", itemId, memberId);
        }

        Loan savedLoan = loanRepository.save(loan);
        member.addLoan(savedLoan);
        memberRepository.save(member);

        log.info("Created loan {} with {} items for member {}",
                savedLoan.getId(), loanRequest.items().size(), memberId);

        return savedLoan;
    }

    @Override
    public List<Loan> getMemberLoans(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }

        return loanRepository.findByMemberIdOrderByLoanDateDesc(memberId);
    }

    @Override
    public Loan getLoanById(Long loanId) {
        return loanRepository.findByIdWithMemberAndItems(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
    }

    @Override
    public Loan returnItems(Long loanId, List<Long> itemIds) {
        Loan loan = loanRepository.findByIdWithItems(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new IllegalStateException("Loan is already closed");
        }

        for (Long itemId : itemIds) {
            LoanItem loanItem = loanItemRepository.findByLoanIdAndItemId(loanId, itemId)
                    .orElseThrow(() -> new ItemNotFoundException(
                            "Item " + itemId + " not found in loan " + loanId));

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
