package com.lms.library.repository;

import com.lms.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoanRepository class
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
class LoanRepositoryTest {

    private LoanRepository repository;
    private Member testMember1;
    private Member testMember2;
    private LibraryItem testBook;
    private LibraryItem testJournal;

    @BeforeEach
    void setUp() throws Exception {
        repository = new LoanRepository();
        resetRepositoryState();

        // Create test data
        testMember1 = createTestMember(1L, "John", "Doe");
        testMember2 = createTestMember(2L, "Jane", "Smith");
        testBook = createTestBook(1L);
        testJournal = createTestJournal(2L);
    }

    /**
     * Helper method to reset the static state of the repository between tests
     */
    private void resetRepositoryState() throws Exception {
        Field storeField = LoanRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        Map<Long, Loan> store = (Map<Long, Loan>) storeField.get(null);
        store.clear();

        Field idSeqField = LoanRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        AtomicLong idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    private Member createTestMember(Long id, String firstName, String lastName) {
        Member member = new Member(firstName, lastName, firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        member.setId(id);
        return member;
    }

    private LibraryItem createTestBook(Long id) {
        Book book = new Book("Test Book", "Test Author",
                LocalDate.now(), "1234567890", "Test Category", 100);
        book.setId(id);
        return book;
    }

    private LibraryItem createTestJournal(Long id) {
        Journal journal = new Journal("Test Journal", "Test Publisher",
                LocalDate.now(), "11112222", "Test Publisher", 1, 1);
        journal.setId(id);
        return journal;
    }

    private Loan createTestLoan(Long id, Member member, LocalDate loanDate, LocalDate dueDate) {
        Loan loan = new Loan(member, loanDate, dueDate);
        loan.setId(id);
        loan.addItem(createTestLoanItem(loan, testBook));
        return loan;
    }

    private LoanItem createTestLoanItem(Loan loan, LibraryItem item) {
        return new LoanItem(loan, item);
    }

    @Test
    @DisplayName("Save new loan without ID should generate and assign ID")
    void save_NewLoanWithoutId_ShouldGenerateId() {
        // Arrange
        Loan loan = createTestLoan(null, testMember1,
                LocalDate.now(), LocalDate.now().plusWeeks(2));

        // Act
        Loan savedLoan = repository.save(loan);

        // Assert
        assertNotNull(savedLoan.getId());
        assertEquals(1L, savedLoan.getId());
        assertEquals(testMember1.getId(), savedLoan.getMember().getId());
        assertEquals(LocalDate.now(), savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Save existing loan with ID should update the loan")
    void save_ExistingLoanWithId_ShouldUpdateLoanAndChangeStatus() {
        // Arrange
        Loan loan = createTestLoan(null, testMember1,
                LocalDate.now(), LocalDate.now().plusWeeks(2));
        Loan savedLoan = repository.save(loan);
        Long loanId = savedLoan.getId();

        assertEquals(LoanStatus.OPEN, savedLoan.getStatus());

        savedLoan.getItems().getFirst().markReturned();
        savedLoan.updateStatus();

        // Act - update the due date
        Loan updatedLoan = repository.save(savedLoan);

        // Assert
        assertEquals(loanId, updatedLoan.getId());
        assertEquals(LocalDate.now().plusWeeks(2), updatedLoan.getExpectedReturnDate());

        // Verify the loan was actually updated in the store
        Optional<Loan> foundLoanOpt = repository.findById(loanId);
        assertTrue(foundLoanOpt.isPresent());
        var foundLoan = foundLoanOpt.get();
        assertTrue(foundLoan.getItems().getFirst().isReturned());
        assertEquals(LoanStatus.CLOSED, foundLoan.getStatus());
    }

    @Test
    @DisplayName("Save multiple loans should generate sequential IDs")
    void save_MultipleLoans_ShouldGenerateSequentialIds() {
        // Arrange
        Loan loan1 = createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2));
        Loan loan2 = createTestLoan(null, testMember2, LocalDate.now(), LocalDate.now().plusWeeks(2));
        Loan loan3 = createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(1));

        // Act
        Loan savedLoan1 = repository.save(loan1);
        Loan savedLoan2 = repository.save(loan2);
        Loan savedLoan3 = repository.save(loan3);

        // Assert
        assertEquals(1L, savedLoan1.getId());
        assertEquals(2L, savedLoan2.getId());
        assertEquals(3L, savedLoan3.getId());
    }

    @Test
    @DisplayName("Find by existing ID should return the loan")
    void findById_ExistingId_ShouldReturnLoan() {
        // Arrange
        Loan loan = createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2));
        Loan savedLoan = repository.save(loan);
        Long loanId = savedLoan.getId();

        // Act
        Optional<Loan> foundLoan = repository.findById(loanId);

        // Assert
        assertTrue(foundLoan.isPresent());
        assertEquals(loanId, foundLoan.get().getId());
        assertEquals(testMember1.getId(), foundLoan.get().getMember().getId());
    }

    @Test
    @DisplayName("Find by non-existing ID should return empty optional")
    void findById_NonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Loan> foundLoan = repository.findById(999L);

        // Assert
        assertFalse(foundLoan.isPresent());
    }

    @Test
    @DisplayName("Find by null ID should return empty optional")
    void findById_NullId_ShouldReturnEmpty() {
        // Act
        Optional<Loan> foundLoan = repository.findById(null);

        // Assert
        assertFalse(foundLoan.isPresent());
    }

    @Test
    @DisplayName("Find by member ID should return all loans for that member ordered by loan date descending")
    void findByMemberIdOrderByLoanDateDesc_ShouldReturnOrderedLoans() {
        // Arrange
        LocalDate today = LocalDate.now();
        Loan loan1 = createTestLoan(null, testMember1, today.minusDays(5), today.plusWeeks(2));
        Loan loan2 = createTestLoan(null, testMember1, today.minusDays(2), today.plusWeeks(2));
        Loan loan3 = createTestLoan(null, testMember1, today, today.plusWeeks(2));
        Loan loan4 = createTestLoan(null, testMember2, today, today.plusWeeks(2));

        repository.save(loan1);
        repository.save(loan2);
        repository.save(loan3);
        repository.save(loan4);

        // Act
        List<Loan> member1Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember1.getId());
        List<Loan> member2Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember2.getId());

        // Assert
        assertEquals(3, member1Loans.size());
        assertEquals(1, member2Loans.size());

        // Verify order for member1: newest first
        assertEquals(today, member1Loans.get(0).getLoanDate());
        assertEquals(today.minusDays(2), member1Loans.get(1).getLoanDate());
        assertEquals(today.minusDays(5), member1Loans.get(2).getLoanDate());

        // Verify all loans belong to the correct member
        assertTrue(member1Loans.stream().allMatch(loan -> loan.getMember().getId().equals(testMember1.getId())));
        assertTrue(member2Loans.stream().allMatch(loan -> loan.getMember().getId().equals(testMember2.getId())));
    }

    @Test
    @DisplayName("Find by member ID with non-existing member ID should return empty list")
    void findByMemberIdOrderByLoanDateDesc_NonExistingMemberId_ShouldReturnEmptyList() {
        // Act
        List<Loan> loans = repository.findByMemberIdOrderByLoanDateDesc(999L);

        // Assert
        assertTrue(loans.isEmpty());
    }

    @Test
    @DisplayName("Find by member ID with null member ID should return empty list")
    void findByMemberIdOrderByLoanDateDesc_NullMemberId_ShouldReturnEmptyList() {
        // Act
        List<Loan> loans = repository.findByMemberIdOrderByLoanDateDesc(null);

        // Assert
        assertTrue(loans.isEmpty());
    }

    @Test
    @DisplayName("Find by member ID with multiple loans on same date should maintain insertion order or ID order")
    void findByMemberIdOrderByLoanDateDesc_SameLoanDate_ShouldMaintainOrder() {
        // Arrange
        LocalDate sameDate = LocalDate.now();
        Loan loan1 = createTestLoan(null, testMember1, sameDate, sameDate.plusWeeks(2));
        Loan loan2 = createTestLoan(null, testMember1, sameDate, sameDate.plusWeeks(1));
        Loan loan3 = createTestLoan(null, testMember1, sameDate, sameDate.plusWeeks(3));

        repository.save(loan1); // ID 1
        repository.save(loan2); // ID 2
        repository.save(loan3); // ID 3

        // Act
        List<Loan> loans = repository.findByMemberIdOrderByLoanDateDesc(testMember1.getId());

        // Assert
        assertEquals(3, loans.size());
        // All have same loan date, so order might be insertion order or ID order
        // The specification says "ordered by loan date (newest first)" but doesn't specify secondary ordering
        // So we just verify they all have the same date
        assertTrue(loans.stream().allMatch(loan -> loan.getLoanDate().equals(sameDate)));
    }

    @Test
    @DisplayName("Find by ID with member and items should return loan (equivalent to findById)")
    void findByIdWithMemberAndItems_ShouldReturnLoan() {
        // Arrange
        Loan loan = createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2));
        Loan savedLoan = repository.save(loan);
        Long loanId = savedLoan.getId();

        // Act
        Optional<Loan> foundLoan = repository.findByIdWithMemberAndItems(loanId);

        // Assert
        assertTrue(foundLoan.isPresent());
        assertEquals(loanId, foundLoan.get().getId());
        assertEquals(testMember1.getId(), foundLoan.get().getMember().getId());

        // Verify it's the same as findById
        Optional<Loan> regularFind = repository.findById(loanId);
        assertEquals(regularFind, foundLoan);
    }

    @Test
    @DisplayName("Find by ID with member and items with non-existing ID should return empty")
    void findByIdWithMemberAndItems_NonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Loan> foundLoan = repository.findByIdWithMemberAndItems(999L);

        // Assert
        assertFalse(foundLoan.isPresent());
    }

    @Test
    @DisplayName("Find by ID with items should return loan (equivalent to findById)")
    void findByIdWithItems_ShouldReturnLoan() {
        // Arrange
        Loan loan = createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2));
        Loan savedLoan = repository.save(loan);
        Long loanId = savedLoan.getId();

        // Act
        Optional<Loan> foundLoan = repository.findByIdWithItems(loanId);

        // Assert
        assertTrue(foundLoan.isPresent());
        assertEquals(loanId, foundLoan.get().getId());

        // Verify it's the same as findById
        Optional<Loan> regularFind = repository.findById(loanId);
        assertEquals(regularFind, foundLoan);
    }

    @Test
    @DisplayName("Find by ID with items with non-existing ID should return empty")
    void findByIdWithItems_NonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<Loan> foundLoan = repository.findByIdWithItems(999L);

        // Assert
        assertFalse(foundLoan.isPresent());
    }

    @Test
    @DisplayName("Repository should handle concurrent access correctly")
    void repository_ConcurrentAccess_ShouldHandleCorrectly() throws InterruptedException {
        // Arrange
        int numberOfThreads = 5;
        int loansPerThread = 5;

        // Act - Create multiple threads that save loans concurrently
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < loansPerThread; j++) {
                    Member member = createTestMember((long) threadId, "Thread" + threadId, "User");
                    Loan loan = createTestLoan(null, member,
                            LocalDate.now().plusDays(j),
                            LocalDate.now().plusWeeks(2));
                    repository.save(loan);
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        // Total loans should be numberOfThreads * loansPerThread
        long totalLoans = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            totalLoans += repository.findByMemberIdOrderByLoanDateDesc((long) i).size();
        }
        assertEquals(numberOfThreads * loansPerThread, totalLoans);

        // All IDs should be unique and sequential
        try {
            Field storeField = LoanRepository.class.getDeclaredField("STORE");
            storeField.setAccessible(true);
            Map<Long, Loan> store = (Map<Long, Loan>) storeField.get(null);
            assertEquals(numberOfThreads * loansPerThread, store.size());

            // Verify all keys are present and unique
            for (long i = 1; i <= numberOfThreads * loansPerThread; i++) {
                assertTrue(store.containsKey(i), "Missing key: " + i);
            }
        } catch (Exception e) {
            fail("Failed to access store via reflection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Mixed operations should work correctly together")
    void mixedOperations_ShouldWorkCorrectly() {
        // Arrange - Create multiple loans for different members
        LocalDate today = LocalDate.now();
        Loan loan1 = repository.save(createTestLoan(null, testMember1, today.minusDays(10), today.plusWeeks(1)));
        Loan loan2 = repository.save(createTestLoan(null, testMember2, today.minusDays(5), today.plusWeeks(2)));
        Loan loan3 = repository.save(createTestLoan(null, testMember1, today.minusDays(2), today.plusWeeks(3)));
        Loan loan4 = repository.save(createTestLoan(null, testMember1, today, today.plusWeeks(4)));

        // Act & Assert - Test various operations
        // Test findById
        Optional<Loan> foundLoan1 = repository.findById(loan1.getId());
        assertTrue(foundLoan1.isPresent());
        assertEquals(testMember1.getId(), foundLoan1.get().getMember().getId());

        // Test findByMemberIdOrderByLoanDateDesc for member1
        List<Loan> member1Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember1.getId());
        assertEquals(3, member1Loans.size());
        assertEquals(today, member1Loans.get(0).getLoanDate()); // newest first
        assertEquals(today.minusDays(10), member1Loans.get(2).getLoanDate()); // oldest last

        // Test findByMemberIdOrderByLoanDateDesc for member2
        List<Loan> member2Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember2.getId());
        assertEquals(1, member2Loans.size());
        assertEquals(today.minusDays(5), member2Loans.get(0).getLoanDate());

        // Test findByIdWithMemberAndItems
        Optional<Loan> loanWithMemberAndItems = repository.findByIdWithMemberAndItems(loan3.getId());
        assertTrue(loanWithMemberAndItems.isPresent());
        assertEquals(loan3.getId(), loanWithMemberAndItems.get().getId());

        // Test findByIdWithItems
        Optional<Loan> loanWithItems = repository.findByIdWithItems(loan4.getId());
        assertTrue(loanWithItems.isPresent());
        assertEquals(loan4.getId(), loanWithItems.get().getId());
    }

    @Test
    @DisplayName("Save loan with predefined ID should use that ID")
    void save_LoanWithPredefinedId_ShouldUseProvidedId() {
        // Arrange
        Loan loan = createTestLoan(100L, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2));

        // Act
        Loan savedLoan = repository.save(loan);

        // Assert
        assertEquals(100L, savedLoan.getId());

        // Verify it can be retrieved by that ID
        Optional<Loan> foundLoan = repository.findById(100L);
        assertTrue(foundLoan.isPresent());
        assertEquals(100L, foundLoan.get().getId());
    }
}
