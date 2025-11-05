package com.lms.library.repository;

import com.lms.library.model.Book;
import com.lms.library.model.Journal;
import com.lms.library.model.LibraryItem;
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
 * Unit tests for LibraryItemRepository class
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
class LibraryItemRepositoryTest {

    private LibraryItemRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new LibraryItemRepository();
        // Reset the static storage and ID sequence before each test
        resetRepositoryState();
    }

    /**
     * Helper method to reset the static state of the repository between tests
     */
    private void resetRepositoryState() throws Exception {
        // Reset the STORE map
        Field storeField = LibraryItemRepository.class.getDeclaredField("STORE");
        storeField.setAccessible(true);
        Map<Long, LibraryItem> store = (Map<Long, LibraryItem>) storeField.get(null);
        store.clear();

        // Reset the ID_SEQ counter
        Field idSeqField = LibraryItemRepository.class.getDeclaredField("ID_SEQ");
        idSeqField.setAccessible(true);
        AtomicLong idSeq = (AtomicLong) idSeqField.get(null);
        idSeq.set(1);
    }

    @Test
    @DisplayName("Save new item without ID should generate and assign ID")
    void save_NewItemWithoutId_ShouldGenerateId() {
        // Arrange
        Book book = new Book("Test Book", "Test Author",
                LocalDate.now(), "1234567890", "Test Category", 100);

        // Act
        LibraryItem savedItem = repository.save(book);

        // Assert
        assertNotNull(savedItem.getId());
        assertEquals(1L, savedItem.getId());
        assertEquals("Test Book", savedItem.getTitle());
    }

    @Test
    @DisplayName("Save multiple items should generate sequential IDs")
    void save_MultipleItems_ShouldGenerateSequentialIds() {
        // Arrange
        Book book1 = new Book("Book 1", "Author 1", LocalDate.now(), "111", "Cat1", 100);
        Book book2 = new Book("Book 2", "Author 2", LocalDate.now(), "222", "Cat2", 200);
        Journal journal = new Journal("Journal 1", "Publisher", LocalDate.now(),
                "333", "Publisher Co", 1, 1);

        // Act
        LibraryItem savedBook1 = repository.save(book1);
        LibraryItem savedBook2 = repository.save(book2);
        LibraryItem savedJournal = repository.save(journal);

        // Assert
        assertEquals(1L, savedBook1.getId());
        assertEquals(2L, savedBook2.getId());
        assertEquals(3L, savedJournal.getId());
    }

    @Test
    @DisplayName("Find by existing ID should return the item")
    void findById_ExistingId_ShouldReturnItem() {
        // Arrange
        Book book = new Book("Test Book", "Author", LocalDate.now(), "123", "Cat", 100);
        LibraryItem savedItem = repository.save(book);
        Long itemId = savedItem.getId();

        // Act
        Optional<LibraryItem> foundItem = repository.findById(itemId);

        // Assert
        assertTrue(foundItem.isPresent());
        assertEquals(itemId, foundItem.get().getId());
        assertEquals("Test Book", foundItem.get().getTitle());
    }

    @Test
    @DisplayName("Find by non-existing ID should return empty optional")
    void findById_NonExistingId_ShouldReturnEmpty() {
        // Act
        Optional<LibraryItem> foundItem = repository.findById(999L);

        // Assert
        assertFalse(foundItem.isPresent());
    }

    @Test
    @DisplayName("Find by null ID should return empty optional")
    void findById_NullId_ShouldReturnEmpty() {
        // Act
        Optional<LibraryItem> foundItem = repository.findById(null);

        // Assert
        assertFalse(foundItem.isPresent());
    }

    @Test
    @DisplayName("Find available item by ID for available item should return item")
    void findAvailableItemById_AvailableItem_ShouldReturnItem() {
        // Arrange
        Book book = new Book("Available Book", "Author", LocalDate.now(), "123", "Cat", 100);
        book.setAvailable(true);
        LibraryItem savedItem = repository.save(book);

        // Act
        Optional<LibraryItem> foundItem = repository.findAvailableItemById(savedItem.getId());

        // Assert
        assertTrue(foundItem.isPresent());
        assertEquals(savedItem.getId(), foundItem.get().getId());
        assertTrue(foundItem.get().isAvailable());
    }

    @Test
    @DisplayName("Find available item by ID for unavailable item should return empty")
    void findAvailableItemById_UnavailableItem_ShouldReturnEmpty() {
        // Arrange
        Book book = new Book("Unavailable Book", "Author", LocalDate.now(), "123", "Cat", 100);
        book.setAvailable(false);
        LibraryItem savedItem = repository.save(book);

        // Act
        Optional<LibraryItem> foundItem = repository.findAvailableItemById(savedItem.getId());

        // Assert
        assertFalse(foundItem.isPresent());
    }

    @Test
    @DisplayName("Find available items should return only available items")
    void findAvailableItems_ShouldReturnOnlyAvailableItems() {
        // Arrange
        Book availableBook = new Book("Available Book", "Author", LocalDate.now(), "111", "Cat", 100);
        availableBook.setAvailable(true);

        Book unavailableBook = new Book("Unavailable Book", "Author", LocalDate.now(), "222", "Cat", 200);
        unavailableBook.setAvailable(false);

        repository.save(availableBook);
        repository.save(unavailableBook);

        // Act
        List<LibraryItem> availableItems = repository.findAvailableItems();

        // Assert
        assertEquals(1, availableItems.size());
        assertEquals("Available Book", availableItems.getFirst().getTitle());
        assertTrue(availableItems.getFirst().isAvailable());
    }

    @Test
    @DisplayName("Find by title containing ignore case should return matching items")
    void findByTitleContainingIgnoreCase_ShouldReturnMatchingItems() {
        // Arrange
        Book book1 = new Book("Java Programming", "Author 1", LocalDate.now(), "111", "Cat", 100);
        Book book2 = new Book("Advanced JAVA Concepts", "Author 2", LocalDate.now(), "222", "Cat", 200);
        Book book3 = new Book("Python Programming", "Author 3", LocalDate.now(), "333", "Cat", 300);

        repository.save(book1);
        repository.save(book2);
        repository.save(book3);

        // Act - Test case-insensitive search
        List<LibraryItem> javaBooks = repository.findByTitleContainingIgnoreCase("java");
        List<LibraryItem> programmingBooks = repository.findByTitleContainingIgnoreCase("PROGRAMMING");

        // Assert
        assertEquals(2, javaBooks.size()); // Should find both Java books
        assertEquals(2, programmingBooks.size()); // Should find Java Programming and Python Programming
    }

    @Test
    @DisplayName("Find by title containing ignore case with no matches should return empty list")
    void findByTitleContainingIgnoreCase_NoMatches_ShouldReturnEmptyList() {
        // Arrange
        Book book = new Book("Java Programming", "Author", LocalDate.now(), "111", "Cat", 100);
        repository.save(book);

        // Act
        List<LibraryItem> results = repository.findByTitleContainingIgnoreCase("nonexistent");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Find available books should return only available books")
    void findAvailableBooks_ShouldReturnOnlyAvailableBooks() {
        // Arrange
        Book availableBook = new Book("Available Book", "Author", LocalDate.now(), "111", "Cat", 100);
        availableBook.setAvailable(true);

        Book unavailableBook = new Book("Unavailable Book", "Author", LocalDate.now(), "222", "Cat", 200);
        unavailableBook.setAvailable(false);

        Journal availableJournal = new Journal("Available Journal", "Publisher",
                LocalDate.now(), "333", "Pub", 1, 1);
        availableJournal.setAvailable(true);

        repository.save(availableBook);
        repository.save(unavailableBook);
        repository.save(availableJournal);

        // Act
        List<LibraryItem> availableBooks = repository.findAvailableBooks();

        // Assert
        assertEquals(1, availableBooks.size());
        assertInstanceOf(Book.class, availableBooks.getFirst());
        assertEquals("Available Book", availableBooks.getFirst().getTitle());
        assertTrue(availableBooks.getFirst().isAvailable());
    }

    @Test
    @DisplayName("Find available journals should return only available journals")
    void findAvailableJournals_ShouldReturnOnlyAvailableJournals() {
        // Arrange
        Journal availableJournal = new Journal("Available Journal", "Publisher",
                LocalDate.now(), "111", "Pub", 1, 1);
        availableJournal.setAvailable(true);

        Journal unavailableJournal = new Journal("Unavailable Journal", "Publisher",
                LocalDate.now(), "222", "Pub", 2, 2);
        unavailableJournal.setAvailable(false);

        Book availableBook = new Book("Available Book", "Author", LocalDate.now(), "333", "Cat", 100);
        availableBook.setAvailable(true);

        repository.save(availableJournal);
        repository.save(unavailableJournal);
        repository.save(availableBook);

        // Act
        List<LibraryItem> availableJournals = repository.findAvailableJournals();

        // Assert
        assertEquals(1, availableJournals.size());
        assertInstanceOf(Journal.class, availableJournals.getFirst());
        assertEquals("Available Journal", availableJournals.getFirst().getTitle());
        assertTrue(availableJournals.getFirst().isAvailable());
    }

    @Test
    @DisplayName("Initialize repository should load sample data")
    void init_ShouldLoadSampleData() throws Exception {
        // Act - Call init method manually
        repository.init();

        // Assert - Verify that sample data was loaded
        List<LibraryItem> allItems = repository.findAvailableItems();
        assertFalse(allItems.isEmpty());

        // Verify specific sample books and journals
        List<LibraryItem> books = repository.findAvailableBooks();
        List<LibraryItem> journals = repository.findAvailableJournals();

        assertFalse(books.isEmpty());
        assertFalse(journals.isEmpty());

        // Verify some specific titles from the sample data
        List<LibraryItem> cleanCodeBooks = repository.findByTitleContainingIgnoreCase("Clean Code");
        assertFalse(cleanCodeBooks.isEmpty());

        List<LibraryItem> natureJournals = repository.findByTitleContainingIgnoreCase("Nature");
        assertFalse(natureJournals.isEmpty());
    }

    @Test
    @DisplayName("Save and retrieve different item types should maintain type information")
    void saveAndRetrieve_DifferentItemTypes_ShouldMaintainType() {
        // Arrange
        Book book = new Book("Test Book", "Author", LocalDate.now(), "123", "Category", 100);
        Journal journal = new Journal("Test Journal", "Publisher", LocalDate.now(),
                "456", "Publisher Co", 10, 2);

        // Act
        LibraryItem savedBook = repository.save(book);
        LibraryItem savedJournal = repository.save(journal);

        // Assert
        Optional<LibraryItem> retrievedBook = repository.findById(savedBook.getId());
        Optional<LibraryItem> retrievedJournal = repository.findById(savedJournal.getId());

        assertTrue(retrievedBook.isPresent());
        assertInstanceOf(Book.class, retrievedBook.get());

        assertTrue(retrievedJournal.isPresent());
        assertInstanceOf(Journal.class, retrievedJournal.get());

        Book retrievedBookCast = (Book) retrievedBook.get();
        Journal retrievedJournalCast = (Journal) retrievedJournal.get();

        assertEquals(100, retrievedBookCast.getPageCount());
        assertEquals(10, retrievedJournalCast.getVolume());
    }
}