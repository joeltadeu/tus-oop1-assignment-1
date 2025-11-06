package com.lms.library.util;

import com.lms.library.model.*;
import com.lms.library.repository.LibraryItemRepository;
import com.lms.library.repository.LoanItemRepository;
import com.lms.library.repository.LoanRepository;
import com.lms.library.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class providing helper methods for unit and integration tests.
 * This class includes methods to reset static state in repository classes
 * and to create preconfigured test entities such as {@link Member},
 * {@link Loan}, {@link LoanItem}, and various {@link LibraryItem} types.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
public class TestUtil {

    /**
     * Resets the static state of {@link MemberRepository} by clearing its internal store
     * and resetting its ID sequence to {@code 1}.
     *
     * @throws Exception if reflection access to repository fields fails
     */
    public static void resetMemberRepositoryState() throws Exception {
        var storeField = MemberRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        var store = (Map<Long, Member>) storeField.get(null);
        store.clear();

        var idSeqField = MemberRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        var idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    /**
     * Resets the static state of {@link LoanRepository} by clearing its internal store
     * and resetting its ID sequence to {@code 1}.
     *
     * @throws Exception if reflection access to repository fields fails
     */
    public static void resetLoanRepositoryState() throws Exception {
        var storeField = LoanRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        var store = (Map<Long, Loan>) storeField.get(null);
        store.clear();

        var idSeqField = LoanRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        var idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    /**
     * Resets the static state of {@link LoanItemRepository} by clearing its internal store
     * and resetting its ID sequence to {@code 1}.
     *
     * @throws Exception if reflection access to repository fields fails
     */
    public static void resetLoanItemRepositoryState() throws Exception {
        var storeField = LoanItemRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        var store = (Map<Long, LoanItem>) storeField.get(null);
        store.clear();

        var idSeqField = LoanItemRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        var idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    /**
     * Resets the static state of {@link LibraryItemRepository} by clearing its internal store
     * and resetting its ID sequence to {@code 1}.
     *
     * @throws Exception if reflection access to repository fields fails
     */
    public static void resetLibraryItemRepositoryState() throws Exception {
        // Reset the STORE map
        var storeField = LibraryItemRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        var store = (Map<Long, LibraryItem>) storeField.get(null);
        store.clear();

        // Reset the ID_SEQ counter
        var idSeqField = LibraryItemRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        var idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    /**
     * Creates a test {@link Loan} with the specified attributes and a single associated item.
     *
     * @param id       the ID to assign to the loan
     * @param member   the {@link Member} who made the loan
     * @param loanDate the date when the loan was created
     * @param dueDate  the due date for returning the loan
     * @param item     the {@link LibraryItem} being loaned
     * @return a new {@link Loan} instance populated with test data
     */
    public static Loan createTestLoan(Long id, Member member, LocalDate loanDate, LocalDate dueDate, LibraryItem item) {
        var loan = new Loan(member, loanDate, dueDate);
        loan.setId(id);
        loan.addItem(createTestLoanItem(loan, item));
        return loan;
    }

    /**
     * Creates a test {@link Loan} with a generated {@link Member}, a default date range,
     * and a single test {@link Book} item.
     *
     * @param id the ID to assign to the loan
     * @return a new {@link Loan} instance populated with test data
     */
    public static Loan createTestLoan(Long id) {
        Loan loan = new Loan(createMember(), LocalDate.now(), LocalDate.now().plusWeeks(2));
        loan.setId(id);
        loan.addItem(createTestLoanItem(loan, createTestBook(1L), false));

        return loan;
    }

    /**
     * Creates a {@link LoanItem} for the given loan and library item.
     *
     * @param loan the associated {@link Loan}
     * @param item the {@link LibraryItem} being loaned
     * @return a new {@link LoanItem} instance
     */
    public static LoanItem createTestLoanItem(Loan loan, LibraryItem item) {
        return new LoanItem(loan, item);
    }

    /**
     * Creates a {@link LoanItem} for the given loan and library item, with the option to mark it as returned.
     *
     * @param loan     the associated {@link Loan}
     * @param item     the {@link LibraryItem} being loaned
     * @param returned whether the item should be marked as returned
     * @return a new {@link LoanItem} instance
     */
    public static LoanItem createTestLoanItem(Loan loan, LibraryItem item, boolean returned) {
        LoanItem loanItem = new LoanItem(loan, item);
        if (returned) {
            loanItem.markReturned();
        }
        return loanItem;
    }

    /**
     * Creates a default {@link Member} instance for testing.
     *
     * @return a new {@link Member} with prefilled test data
     */
    public static Member createMember() {
        Member member = new Member("Test", "User", "test@test.com.br");
        member.setId(1L);
        return member;
    }

    /**
     * Creates a {@link Member} with the given details.
     *
     * @param firstName the member's first name
     * @param lastName  the member's last name
     * @param email     the member's email
     * @return a new {@link Member} instance
     */
    public static Member createTestMember(String firstName, String lastName, String email) {
        return new Member(firstName, lastName, email);
    }

    /**
     * Creates a {@link Member} with the given ID and name details, generating an email automatically.
     *
     * @param id        the ID to assign to the member
     * @param firstName the member's first name
     * @param lastName  the member's last name
     * @return a new {@link Member} instance
     */
    public static Member createTestMember(Long id, String firstName, String lastName) {
        var member = new Member(firstName, lastName, firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        member.setId(id);
        return member;
    }

    /**
     * Creates a test {@link Book} with the specified ID and default metadata.
     *
     * @param id the ID to assign to the book
     * @return a new {@link Book} instance populated with test data
     */
    public static LibraryItem createTestBook(Long id) {
        Book book = new Book("Test Book", "Test Author",
                LocalDate.now(), "1234567890", "Test Category", 100);
        book.setId(id);
        return book;
    }

    /**
     * Creates a test {@link Journal} with the specified ID and default metadata.
     *
     * @param id the ID to assign to the journal
     * @return a new {@link Journal} instance populated with test data
     */
    public static LibraryItem createTestJournal(Long id) {
        Journal journal = new Journal("Test Journal", "Test Publisher",
                LocalDate.now(), "11112222", "Test Publisher", 1, 1);
        journal.setId(id);
        return journal;
    }
}
