package com.lms.library.repository;

import static com.lms.library.util.TestUtil.createTestBook;
import static com.lms.library.util.TestUtil.createTestLoan;
import static com.lms.library.util.TestUtil.createTestMember;
import static com.lms.library.util.TestUtil.resetLoanRepositoryState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.lms.library.model.LibraryItem;
import com.lms.library.model.Loan;
import com.lms.library.model.LoanStatus;
import com.lms.library.model.Member;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

  @BeforeEach
  void setUp() throws Exception {
    repository = new LoanRepository();
    resetLoanRepositoryState();

    // Create test data
    testMember1 = createTestMember(1L, "John", "Doe");
    testMember2 = createTestMember(2L, "Jane", "Smith");
    testBook = createTestBook(1L);
  }

  @Test
  @DisplayName("Save new loan without ID should generate and assign ID")
  void save_NewLoanWithoutId_ShouldGenerateId() {
    // Arrange
    var loan =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);

    // Act
    var savedLoan = repository.save(loan);

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
    var loan =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);
    var savedLoan = repository.save(loan);
    var loanId = savedLoan.getId();

    assertEquals(LoanStatus.OPEN, savedLoan.getStatus());

    savedLoan.getItems().getFirst().markReturned();
    savedLoan.updateStatus();

    // Act - update the due date
    var updatedLoan = repository.save(savedLoan);

    // Assert
    assertEquals(loanId, updatedLoan.getId());
    assertEquals(LocalDate.now().plusWeeks(2), updatedLoan.getExpectedReturnDate());

    // Verify the loan was actually updated in the store
    var foundLoanOpt = repository.findById(loanId);
    assertTrue(foundLoanOpt.isPresent());
    var foundLoan = foundLoanOpt.get();
    assertTrue(foundLoan.getItems().getFirst().isReturned());
    assertEquals(LoanStatus.CLOSED, foundLoan.getStatus());
  }

  @Test
  @DisplayName("Save multiple loans should generate sequential IDs")
  void save_MultipleLoans_ShouldGenerateSequentialIds() {
    // Arrange
    var loan1 =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);
    var loan2 =
        createTestLoan(null, testMember2, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);
    var loan3 =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(1), testBook);

    // Act
    var savedLoan1 = repository.save(loan1);
    var savedLoan2 = repository.save(loan2);
    var savedLoan3 = repository.save(loan3);

    // Assert
    assertEquals(1L, savedLoan1.getId());
    assertEquals(2L, savedLoan2.getId());
    assertEquals(3L, savedLoan3.getId());
  }

  @Test
  @DisplayName("Find by existing ID should return the loan")
  void findById_ExistingId_ShouldReturnLoan() {
    // Arrange
    var loan =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);
    var savedLoan = repository.save(loan);
    var loanId = savedLoan.getId();

    // Act
    var foundLoan = repository.findById(loanId);

    // Assert
    assertTrue(foundLoan.isPresent());
    assertEquals(loanId, foundLoan.get().getId());
    assertEquals(testMember1.getId(), foundLoan.get().getMember().getId());
  }

  @Test
  @DisplayName("Find by non-existing ID should return empty optional")
  void findById_NonExistingId_ShouldReturnEmpty() {
    // Act
    var foundLoan = repository.findById(999L);

    // Assert
    assertFalse(foundLoan.isPresent());
  }

  @Test
  @DisplayName("Find by null ID should return empty optional")
  void findById_NullId_ShouldReturnEmpty() {
    // Act
    var foundLoan = repository.findById(null);

    // Assert
    assertFalse(foundLoan.isPresent());
  }

  @Test
  @DisplayName(
      "Find by member ID should return all loans for that member ordered by loan date descending")
  void findByMemberIdOrderByLoanDateDesc_ShouldReturnOrderedLoans() {
    // Arrange
    var today = LocalDate.now();
    var loan1 = createTestLoan(null, testMember1, today.minusDays(5), today.plusWeeks(2), testBook);
    var loan2 = createTestLoan(null, testMember1, today.minusDays(2), today.plusWeeks(2), testBook);
    var loan3 = createTestLoan(null, testMember1, today, today.plusWeeks(2), testBook);
    var loan4 = createTestLoan(null, testMember2, today, today.plusWeeks(2), testBook);

    repository.save(loan1);
    repository.save(loan2);
    repository.save(loan3);
    repository.save(loan4);

    // Act
    var member1Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember1.getId());
    var member2Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember2.getId());

    // Assert
    assertEquals(3, member1Loans.size());
    assertEquals(1, member2Loans.size());

    // Verify order for member1: newest first
    assertEquals(today, member1Loans.get(0).getLoanDate());
    assertEquals(today.minusDays(2), member1Loans.get(1).getLoanDate());
    assertEquals(today.minusDays(5), member1Loans.get(2).getLoanDate());

    // Verify all loans belong to the correct member
    assertTrue(
        member1Loans.stream()
            .allMatch(loan -> loan.getMember().getId().equals(testMember1.getId())));
    assertTrue(
        member2Loans.stream()
            .allMatch(loan -> loan.getMember().getId().equals(testMember2.getId())));
  }

  @Test
  @DisplayName("Find by member ID with non-existing member ID should return empty list")
  void findByMemberIdOrderByLoanDateDesc_NonExistingMemberId_ShouldReturnEmptyList() {
    // Act
    var loans = repository.findByMemberIdOrderByLoanDateDesc(999L);

    // Assert
    assertTrue(loans.isEmpty());
  }

  @Test
  @DisplayName("Find by member ID with null member ID should return empty list")
  void findByMemberIdOrderByLoanDateDesc_NullMemberId_ShouldReturnEmptyList() {
    // Act
    var loans = repository.findByMemberIdOrderByLoanDateDesc(null);

    // Assert
    assertTrue(loans.isEmpty());
  }

  @Test
  @DisplayName(
      "Find by member ID with multiple loans on same date should maintain insertion order or ID order")
  void findByMemberIdOrderByLoanDateDesc_SameLoanDate_ShouldMaintainOrder() {
    // Arrange
    var sameDate = LocalDate.now();
    var loan1 = createTestLoan(null, testMember1, sameDate, sameDate.plusWeeks(2), testBook);
    var loan2 = createTestLoan(null, testMember1, sameDate, sameDate.plusWeeks(1), testBook);
    var loan3 = createTestLoan(null, testMember1, sameDate, sameDate.plusWeeks(3), testBook);

    repository.save(loan1); // ID 1
    repository.save(loan2); // ID 2
    repository.save(loan3); // ID 3

    // Act
    var loans = repository.findByMemberIdOrderByLoanDateDesc(testMember1.getId());

    // Assert
    assertEquals(3, loans.size());
    // All have same loan date, so order might be insertion order or ID order
    // The specification says "ordered by loan date (newest first)" but doesn't specify secondary
    // ordering
    // So we just verify they all have the same date
    assertTrue(loans.stream().allMatch(loan -> loan.getLoanDate().equals(sameDate)));
  }

  @Test
  @DisplayName("Find by ID with member and items should return loan (equivalent to findById)")
  void findByIdWithMemberAndItems_ShouldReturnLoan() {
    // Arrange
    var loan =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);
    var savedLoan = repository.save(loan);
    var loanId = savedLoan.getId();

    // Act
    var foundLoan = repository.findByIdWithMemberAndItems(loanId);

    // Assert
    assertTrue(foundLoan.isPresent());
    assertEquals(loanId, foundLoan.get().getId());
    assertEquals(testMember1.getId(), foundLoan.get().getMember().getId());

    // Verify it's the same as findById
    var regularFind = repository.findById(loanId);
    assertEquals(regularFind, foundLoan);
  }

  @Test
  @DisplayName("Find by ID with member and items with non-existing ID should return empty")
  void findByIdWithMemberAndItems_NonExistingId_ShouldReturnEmpty() {
    // Act
    var foundLoan = repository.findByIdWithMemberAndItems(999L);

    // Assert
    assertFalse(foundLoan.isPresent());
  }

  @Test
  @DisplayName("Find by ID with items should return loan (equivalent to findById)")
  void findByIdWithItems_ShouldReturnLoan() {
    // Arrange
    var loan =
        createTestLoan(null, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);
    var savedLoan = repository.save(loan);
    var loanId = savedLoan.getId();

    // Act
    var foundLoan = repository.findByIdWithItems(loanId);

    // Assert
    assertTrue(foundLoan.isPresent());
    assertEquals(loanId, foundLoan.get().getId());

    // Verify it's the same as findById
    var regularFind = repository.findById(loanId);
    assertEquals(regularFind, foundLoan);
  }

  @Test
  @DisplayName("Find by ID with items with non-existing ID should return empty")
  void findByIdWithItems_NonExistingId_ShouldReturnEmpty() {
    // Act
    var foundLoan = repository.findByIdWithItems(999L);

    // Assert
    assertFalse(foundLoan.isPresent());
  }

  @Test
  @DisplayName("Repository should handle concurrent access correctly")
  void repository_ConcurrentAccess_ShouldHandleCorrectly() throws InterruptedException {
    // Arrange
    var numberOfThreads = 5;
    var loansPerThread = 5;

    // Act - Create multiple threads that save loans concurrently
    var threads = new Thread[numberOfThreads];
    for (int i = 0; i < numberOfThreads; i++) {
      final int threadId = i;
      threads[i] =
          new Thread(
              () -> {
                for (int j = 0; j < loansPerThread; j++) {
                  var member = createTestMember((long) threadId, "Thread" + threadId, "User");
                  var loan =
                      createTestLoan(
                          null,
                          member,
                          LocalDate.now().plusDays(j),
                          LocalDate.now().plusWeeks(2),
                          testBook);
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
      var storeField = LoanRepository.class.getDeclaredField("STORE");
      storeField.setAccessible(true);
      var store = (Map<Long, Loan>) storeField.get(null);
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
    var today = LocalDate.now();
    var loan1 =
        repository.save(
            createTestLoan(null, testMember1, today.minusDays(10), today.plusWeeks(1), testBook));
    repository.save(
        createTestLoan(null, testMember2, today.minusDays(5), today.plusWeeks(2), testBook));
    var loan3 =
        repository.save(
            createTestLoan(null, testMember1, today.minusDays(2), today.plusWeeks(3), testBook));
    var loan4 =
        repository.save(createTestLoan(null, testMember1, today, today.plusWeeks(4), testBook));

    // Act & Assert - Test various operations
    // Test findById
    var foundLoan1 = repository.findById(loan1.getId());
    assertTrue(foundLoan1.isPresent());
    assertEquals(testMember1.getId(), foundLoan1.get().getMember().getId());

    // Test findByMemberIdOrderByLoanDateDesc for member1
    var member1Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember1.getId());
    assertEquals(3, member1Loans.size());
    assertEquals(today, member1Loans.get(0).getLoanDate()); // newest first
    assertEquals(today.minusDays(10), member1Loans.get(2).getLoanDate()); // oldest last

    // Test findByMemberIdOrderByLoanDateDesc for member2
    var member2Loans = repository.findByMemberIdOrderByLoanDateDesc(testMember2.getId());
    assertEquals(1, member2Loans.size());
    assertEquals(today.minusDays(5), member2Loans.get(0).getLoanDate());

    // Test findByIdWithMemberAndItems
    var loanWithMemberAndItems = repository.findByIdWithMemberAndItems(loan3.getId());
    assertTrue(loanWithMemberAndItems.isPresent());
    assertEquals(loan3.getId(), loanWithMemberAndItems.get().getId());

    // Test findByIdWithItems
    var loanWithItems = repository.findByIdWithItems(loan4.getId());
    assertTrue(loanWithItems.isPresent());
    assertEquals(loan4.getId(), loanWithItems.get().getId());
  }

  @Test
  @DisplayName("Save loan with predefined ID should use that ID")
  void save_LoanWithPredefinedId_ShouldUseProvidedId() {
    // Arrange
    var loan =
        createTestLoan(100L, testMember1, LocalDate.now(), LocalDate.now().plusWeeks(2), testBook);

    // Act
    var savedLoan = repository.save(loan);

    // Assert
    assertEquals(100L, savedLoan.getId());

    // Verify it can be retrieved by that ID
    var foundLoan = repository.findById(100L);
    assertTrue(foundLoan.isPresent());
    assertEquals(100L, foundLoan.get().getId());
  }
}
