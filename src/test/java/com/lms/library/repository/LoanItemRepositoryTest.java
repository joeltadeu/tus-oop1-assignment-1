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
 * Unit tests for LoanItemRepository class
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
class LoanItemRepositoryTest {

    private LoanItemRepository repository;
    private Loan testLoan;
    private LibraryItem testBook;
    private LibraryItem testJournal;

    @BeforeEach
    void setUp() throws Exception {
        repository = new LoanItemRepository();
        resetRepositoryState();

        // Create test data
        testLoan = createTestLoan(1L);
        testBook = createTestBook(1L);
        testJournal = createTestJournal(2L);
    }

    /**
     * Helper method to reset the static state of the repository between tests
     */
    private void resetRepositoryState() throws Exception {
        Field storeField = LoanItemRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        Map<Long, LoanItem> store = (Map<Long, LoanItem>) storeField.get(null);
        store.clear();

        Field idSeqField = LoanItemRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        AtomicLong idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    private Loan createTestLoan(Long id) {
        Loan loan = new Loan(createMember(), LocalDate.now(), LocalDate.now().plusWeeks(2));
        loan.setId(id);
        loan.addItem(createTestLoanItem(loan, createTestBook(1L), false));

        return loan;
    }

    private Member createMember() {
        Member member = new Member("Test", "User", "test@test.com.br");
        member.setId(1L);
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

    private LoanItem createTestLoanItem(Loan loan, LibraryItem item, boolean returned) {
        LoanItem loanItem = new LoanItem(loan, item);
        if (returned) {
            loanItem.markReturned();
        }
        return loanItem;
    }

    @Test
    @DisplayName("Save new loan item without ID should generate and assign ID")
    void save_NewLoanItemWithoutId_ShouldGenerateId() {
        // Arrange
        LoanItem loanItem = createTestLoanItem(testLoan, testBook, false);

        // Act
        LoanItem savedItem = repository.save(loanItem);

        // Assert
        assertNotNull(savedItem.getId());
        assertEquals(1L, savedItem.getId());
        assertEquals(testLoan.getId(), savedItem.getLoan().getId());
        assertEquals(testBook.getId(), savedItem.getItem().getId());
        assertFalse(savedItem.isReturned());
    }

    @Test
    @DisplayName("Save existing loan item with ID should update the item")
    void save_ExistingLoanItemWithId_ShouldUpdateItem() {
        // Arrange
        LoanItem loanItem = createTestLoanItem(testLoan, testBook, false);
        LoanItem savedItem = repository.save(loanItem);
        Long itemId = savedItem.getId();

        savedItem.markReturned();
        LoanItem updatedItem = repository.save(savedItem);

        // Assert
        assertEquals(itemId, updatedItem.getId());
        assertTrue(updatedItem.isReturned());

        // Verify the item was actually updated in the store
        List<LoanItem> foundItems = repository.findByLoanId(testLoan.getId());
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.getFirst().isReturned());
    }

    @Test
    @DisplayName("Save multiple loan items should generate sequential IDs")
    void save_MultipleLoanItems_ShouldGenerateSequentialIds() {
        // Arrange
        LoanItem loanItem1 = createTestLoanItem(testLoan, testBook, false);
        LoanItem loanItem2 = createTestLoanItem(testLoan, testJournal, false);

        // Act
        LoanItem savedItem1 = repository.save(loanItem1);
        LoanItem savedItem2 = repository.save(loanItem2);

        // Assert
        assertEquals(1L, savedItem1.getId());
        assertEquals(2L, savedItem2.getId());
    }

    @Test
    @DisplayName("Find by loan ID should return all items for that loan")
    void findByLoanId_ExistingLoanId_ShouldReturnAllItems() {
        // Arrange
        Loan loan1 = createTestLoan(1L);
        Loan loan2 = createTestLoan(2L);

        LoanItem item1 = repository.save(createTestLoanItem(loan1, testBook, false));
        LoanItem item2 = repository.save(createTestLoanItem(loan1, testJournal, false));
        LoanItem item3 = repository.save(createTestLoanItem(loan2, testBook, true));

        // Act
        List<LoanItem> loan1Items = repository.findByLoanId(1L);
        List<LoanItem> loan2Items = repository.findByLoanId(2L);

        // Assert
        assertEquals(2, loan1Items.size());
        assertEquals(1, loan2Items.size());

        // Verify the items belong to the correct loan
        assertTrue(loan1Items.stream().allMatch(item -> item.getLoan().getId().equals(1L)));
        assertTrue(loan2Items.stream().allMatch(item -> item.getLoan().getId().equals(2L)));
    }

    @Test
    @DisplayName("Find by non-existing loan ID should return empty list")
    void findByLoanId_NonExistingLoanId_ShouldReturnEmptyList() {
        // Act
        List<LoanItem> items = repository.findByLoanId(999L);

        // Assert
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Find by null loan ID should return empty list")
    void findByLoanId_NullLoanId_ShouldReturnEmptyList() {
        // Act
        List<LoanItem> items = repository.findByLoanId(null);

        // Assert
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Find by loan ID and item ID should return matching loan item")
    void findByLoanIdAndItemId_ExistingIds_ShouldReturnLoanItem() {
        // Arrange
        LoanItem loanItem = createTestLoanItem(testLoan, testBook, false);
        repository.save(loanItem);

        // Act
        Optional<LoanItem> foundItem = repository.findByLoanIdAndItemId(testLoan.getId(), testBook.getId());

        // Assert
        assertTrue(foundItem.isPresent());
        assertEquals(testLoan.getId(), foundItem.get().getLoan().getId());
        assertEquals(testBook.getId(), foundItem.get().getItem().getId());
    }

    @Test
    @DisplayName("Find by loan ID and item ID with non-existing combination should return empty")
    void findByLoanIdAndItemId_NonExistingCombination_ShouldReturnEmpty() {
        // Arrange
        LoanItem loanItem = createTestLoanItem(testLoan, testBook, false);
        repository.save(loanItem);

        // Act - different item ID
        Optional<LoanItem> foundItem1 = repository.findByLoanIdAndItemId(testLoan.getId(), 999L);
        // Act - different loan ID
        Optional<LoanItem> foundItem2 = repository.findByLoanIdAndItemId(999L, testBook.getId());

        // Assert
        assertFalse(foundItem1.isPresent());
        assertFalse(foundItem2.isPresent());
    }

    @Test
    @DisplayName("Find by loan ID and item ID with null values should return empty")
    void findByLoanIdAndItemId_NullValues_ShouldReturnEmpty() {
        // Act
        Optional<LoanItem> foundItem1 = repository.findByLoanIdAndItemId(null, testBook.getId());
        Optional<LoanItem> foundItem2 = repository.findByLoanIdAndItemId(testLoan.getId(), null);
        Optional<LoanItem> foundItem3 = repository.findByLoanIdAndItemId(null, null);

        // Assert
        assertFalse(foundItem1.isPresent());
        assertFalse(foundItem2.isPresent());
        assertFalse(foundItem3.isPresent());
    }

    @Test
    @DisplayName("Find active items by loan ID should return only non-returned items")
    void findActiveItemsByLoanId_ShouldReturnOnlyNonReturnedItems() {
        // Arrange
        LoanItem activeItem1 = createTestLoanItem(testLoan, testBook, false);
        LoanItem activeItem2 = createTestLoanItem(testLoan, testJournal, false);
        LoanItem returnedItem = createTestLoanItem(testLoan, createTestBook(3L), true);

        repository.save(activeItem1);
        repository.save(activeItem2);
        repository.save(returnedItem);

        // Act
        List<LoanItem> activeItems = repository.findActiveItemsByLoanId(testLoan.getId());

        // Assert
        assertEquals(2, activeItems.size());
        assertTrue(activeItems.stream().noneMatch(LoanItem::isReturned));
    }

    @Test
    @DisplayName("Find active items by loan ID with all returned items should return empty list")
    void findActiveItemsByLoanId_AllReturnedItems_ShouldReturnEmptyList() {
        // Arrange
        LoanItem returnedItem1 = createTestLoanItem(testLoan, testBook, true);
        LoanItem returnedItem2 = createTestLoanItem(testLoan, testJournal, true);

        repository.save(returnedItem1);
        repository.save(returnedItem2);

        // Act
        List<LoanItem> activeItems = repository.findActiveItemsByLoanId(testLoan.getId());

        // Assert
        assertTrue(activeItems.isEmpty());
    }

    @Test
    @DisplayName("Find active items by non-existing loan ID should return empty list")
    void findActiveItemsByLoanId_NonExistingLoanId_ShouldReturnEmptyList() {
        // Act
        List<LoanItem> activeItems = repository.findActiveItemsByLoanId(999L);

        // Assert
        assertTrue(activeItems.isEmpty());
    }

    @Test
    @DisplayName("Save all should save multiple loan items")
    void saveAll_ShouldSaveMultipleLoanItems() {
        // Arrange
        LoanItem item1 = createTestLoanItem(testLoan, testBook, false);
        LoanItem item2 = createTestLoanItem(testLoan, testJournal, false);
        LoanItem item3 = createTestLoanItem(testLoan, createTestBook(3L), true);

        List<LoanItem> items = List.of(item1, item2, item3);

        // Act
        repository.saveAll(items);

        // Assert
        List<LoanItem> allItems = repository.findByLoanId(testLoan.getId());
        assertEquals(3, allItems.size());

        // Verify IDs were assigned
        assertTrue(allItems.stream().allMatch(item -> item.getId() != null));

        // Verify sequential IDs
        List<Long> ids = allItems.stream().map(LoanItem::getId).sorted().toList();
        assertEquals(List.of(1L, 2L, 3L), ids);
    }

    @Test
    @DisplayName("Save all with empty list should not throw exception")
    void saveAll_EmptyList_ShouldNotThrowException() {
        // Arrange
        List<LoanItem> emptyList = List.of();

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> repository.saveAll(emptyList));

        // Verify no items were added
        List<LoanItem> allItems = repository.findByLoanId(testLoan.getId());
        assertTrue(allItems.isEmpty());
    }

    @Test
    @DisplayName("Save all with existing items should update them")
    void saveAll_WithExistingItems_ShouldUpdateThem() {
        // Arrange - create and save initial items
        LoanItem item1 = repository.save(createTestLoanItem(testLoan, testBook, false));
        LoanItem item2 = repository.save(createTestLoanItem(testLoan, testJournal, false));

        // Update the items
        item1.markReturned();
        item2.markReturned();

        List<LoanItem> updatedItems = List.of(item1, item2);

        // Act
        repository.saveAll(updatedItems);

        // Assert
        List<LoanItem> allItems = repository.findByLoanId(testLoan.getId());
        assertEquals(2, allItems.size());
        assertTrue(allItems.stream().allMatch(LoanItem::isReturned));
    }

    @Test
    @DisplayName("Repository should handle concurrent access correctly")
    void repository_ConcurrentAccess_ShouldHandleCorrectly() throws InterruptedException {
        // Arrange
        int numberOfThreads = 10;
        int itemsPerThread = 10;

        // Act - Create multiple threads that save items concurrently
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    Loan loan = createTestLoan((long) threadId);
                    LibraryItem item = createTestBook((long) (threadId * itemsPerThread + j));
                    LoanItem loanItem = createTestLoanItem(loan, item, false);
                    repository.save(loanItem);
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
        // Total items should be numberOfThreads * itemsPerThread
        long totalItems = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            totalItems += repository.findByLoanId((long) i).size();
        }
        assertEquals(numberOfThreads * itemsPerThread, totalItems);

        // All IDs should be unique and sequential
        // Note: Due to concurrent access, IDs might not be in order but should be unique
        // We can verify uniqueness by checking the store size
        try {
            Field storeField = LoanItemRepository.class.getDeclaredField("STORE");
            storeField.setAccessible(true);
            Map<Long, LoanItem> store = (Map<Long, LoanItem>) storeField.get(null);
            assertEquals(numberOfThreads * itemsPerThread, store.size());

            // Verify all keys are present and unique
            for (long i = 1; i <= numberOfThreads * itemsPerThread; i++) {
                assertTrue(store.containsKey(i), "Missing key: " + i);
            }
        } catch (Exception e) {
            fail("Failed to access store via reflection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Mixed operations should work correctly")
    void mixedOperations_ShouldWorkCorrectly() {
        // Arrange - Create multiple loans and items
        Loan loan1 = createTestLoan(1L);
        Loan loan2 = createTestLoan(2L);

        LibraryItem book1 = createTestBook(1L);
        LibraryItem book2 = createTestBook(2L);
        LibraryItem journal1 = createTestJournal(3L);
        LibraryItem journal2 = createTestJournal(4L);

        // Act - Perform various operations
        LoanItem item1 = repository.save(createTestLoanItem(loan1, book1, false));
        LoanItem item2 = repository.save(createTestLoanItem(loan1, journal1, false));
        LoanItem item3 = repository.save(createTestLoanItem(loan2, book2, true));
        LoanItem item4 = repository.save(createTestLoanItem(loan2, journal2, false));

        // Assert - Verify all operations work together
        List<LoanItem> loan1Items = repository.findByLoanId(1L);
        assertEquals(2, loan1Items.size());

        List<LoanItem> loan2ActiveItems = repository.findActiveItemsByLoanId(2L);
        assertEquals(1, loan2ActiveItems.size()); // Only journal2 is active

        Optional<LoanItem> specificItem = repository.findByLoanIdAndItemId(1L, 1L);
        assertTrue(specificItem.isPresent());
        assertEquals(book1.getId(), specificItem.get().getItem().getId());

        // Update item1 to returned
        item1.markReturned();
        repository.save(item1);

        List<LoanItem> loan1ActiveItems = repository.findActiveItemsByLoanId(1L);
        assertEquals(1, loan1ActiveItems.size()); // Only journal1 remains active
    }
}
