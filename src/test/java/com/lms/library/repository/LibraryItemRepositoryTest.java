package com.lms.library.repository;

import static com.lms.library.util.TestUtil.resetLibraryItemRepositoryState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lms.library.model.Book;
import com.lms.library.model.Journal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    resetLibraryItemRepositoryState();
  }

  @Test
  @DisplayName("Save new item without ID should generate and assign ID")
  void save_NewItemWithoutId_ShouldGenerateId() {
    // Arrange
    var book =
        new Book("Test Book", "Test Author", LocalDate.now(), "1234567890", "Test Category", 100);

    // Act
    var savedItem = repository.save(book);

    // Assert
    assertNotNull(savedItem.getId());
    assertEquals(1L, savedItem.getId());
    assertEquals("Test Book", savedItem.getTitle());
  }

  @Test
  @DisplayName("Save multiple items should generate sequential IDs")
  void save_MultipleItems_ShouldGenerateSequentialIds() {
    // Arrange
    var book1 = new Book("Book 1", "Author 1", LocalDate.now(), "111", "Cat1", 100);
    var book2 = new Book("Book 2", "Author 2", LocalDate.now(), "222", "Cat2", 200);
    var journal =
        new Journal("Journal 1", "Publisher", LocalDate.now(), "333", "Publisher Co", 1, 1);

    // Act
    var savedBook1 = repository.save(book1);
    var savedBook2 = repository.save(book2);
    var savedJournal = repository.save(journal);

    // Assert
    assertEquals(1L, savedBook1.getId());
    assertEquals(2L, savedBook2.getId());
    assertEquals(3L, savedJournal.getId());
  }

  @Test
  @DisplayName("Find by existing ID should return the item")
  void findById_ExistingId_ShouldReturnItem() {
    // Arrange
    var book = new Book("Test Book", "Author", LocalDate.now(), "123", "Cat", 100);
    var savedItem = repository.save(book);
    var itemId = savedItem.getId();

    // Act
    var foundItem = repository.findById(itemId);

    // Assert
    assertTrue(foundItem.isPresent());
    assertEquals(itemId, foundItem.get().getId());
    assertEquals("Test Book", foundItem.get().getTitle());
  }

  @Test
  @DisplayName("Find by non-existing ID should return empty optional")
  void findById_NonExistingId_ShouldReturnEmpty() {
    // Act
    var foundItem = repository.findById(999L);

    // Assert
    assertFalse(foundItem.isPresent());
  }

  @Test
  @DisplayName("Find by null ID should return empty optional")
  void findById_NullId_ShouldReturnEmpty() {
    // Act
    var foundItem = repository.findById(null);

    // Assert
    assertFalse(foundItem.isPresent());
  }

  @Test
  @DisplayName("Find available item by ID for available item should return item")
  void findAvailableItemById_AvailableItem_ShouldReturnItem() {
    // Arrange
    var book = new Book("Available Book", "Author", LocalDate.now(), "123", "Cat", 100);
    book.setAvailable(true);
    var savedItem = repository.save(book);

    // Act
    var foundItem = repository.findAvailableItemById(savedItem.getId());

    // Assert
    assertTrue(foundItem.isPresent());
    assertEquals(savedItem.getId(), foundItem.get().getId());
    assertTrue(foundItem.get().isAvailable());
  }

  @Test
  @DisplayName("Find available item by ID for unavailable item should return empty")
  void findAvailableItemById_UnavailableItem_ShouldReturnEmpty() {
    // Arrange
    var book = new Book("Unavailable Book", "Author", LocalDate.now(), "123", "Cat", 100);
    book.setAvailable(false);
    var savedItem = repository.save(book);

    // Act
    var foundItem = repository.findAvailableItemById(savedItem.getId());

    // Assert
    assertFalse(foundItem.isPresent());
  }

  @Test
  @DisplayName("Find available items should return only available items")
  void findAvailableItems_ShouldReturnOnlyAvailableItems() {
    // Arrange
    var availableBook = new Book("Available Book", "Author", LocalDate.now(), "111", "Cat", 100);
    availableBook.setAvailable(true);

    var unavailableBook =
        new Book("Unavailable Book", "Author", LocalDate.now(), "222", "Cat", 200);
    unavailableBook.setAvailable(false);

    repository.save(availableBook);
    repository.save(unavailableBook);

    // Act
    var availableItems = repository.findAvailableItems();

    // Assert
    assertEquals(1, availableItems.size());
    assertEquals("Available Book", availableItems.getFirst().getTitle());
    assertTrue(availableItems.getFirst().isAvailable());
  }

  @Test
  @DisplayName("Find by title containing ignore case should return matching items")
  void findByTitleContainingIgnoreCase_ShouldReturnMatchingItems() {
    // Arrange
    var book1 = new Book("Java Programming", "Author 1", LocalDate.now(), "111", "Cat", 100);
    var book2 = new Book("Advanced JAVA Concepts", "Author 2", LocalDate.now(), "222", "Cat", 200);
    var book3 = new Book("Python Programming", "Author 3", LocalDate.now(), "333", "Cat", 300);

    repository.save(book1);
    repository.save(book2);
    repository.save(book3);

    // Act - Test case-insensitive search
    var javaBooks = repository.findByTitleContainingIgnoreCase("java");
    var programmingBooks = repository.findByTitleContainingIgnoreCase("PROGRAMMING");

    // Assert
    assertEquals(2, javaBooks.size()); // Should find both Java books
    assertEquals(2, programmingBooks.size()); // Should find Java Programming and Python Programming
  }

  @Test
  @DisplayName("Find by title containing ignore case with no matches should return empty list")
  void findByTitleContainingIgnoreCase_NoMatches_ShouldReturnEmptyList() {
    // Arrange
    var book = new Book("Java Programming", "Author", LocalDate.now(), "111", "Cat", 100);
    repository.save(book);

    // Act
    var results = repository.findByTitleContainingIgnoreCase("nonexistent");

    // Assert
    assertTrue(results.isEmpty());
  }

  @Test
  @DisplayName("Find available books should return only available books")
  void findAvailableBooks_ShouldReturnOnlyAvailableBooks() {
    // Arrange
    var availableBook = new Book("Available Book", "Author", LocalDate.now(), "111", "Cat", 100);
    availableBook.setAvailable(true);

    var unavailableBook =
        new Book("Unavailable Book", "Author", LocalDate.now(), "222", "Cat", 200);
    unavailableBook.setAvailable(false);

    var availableJournal =
        new Journal("Available Journal", "Publisher", LocalDate.now(), "333", "Pub", 1, 1);
    availableJournal.setAvailable(true);

    repository.save(availableBook);
    repository.save(unavailableBook);
    repository.save(availableJournal);

    // Act
    var availableBooks = repository.findAvailableBooks();

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
    var availableJournal =
        new Journal("Available Journal", "Publisher", LocalDate.now(), "111", "Pub", 1, 1);
    availableJournal.setAvailable(true);

    var unavailableJournal =
        new Journal("Unavailable Journal", "Publisher", LocalDate.now(), "222", "Pub", 2, 2);
    unavailableJournal.setAvailable(false);

    var availableBook = new Book("Available Book", "Author", LocalDate.now(), "333", "Cat", 100);
    availableBook.setAvailable(true);

    repository.save(availableJournal);
    repository.save(unavailableJournal);
    repository.save(availableBook);

    // Act
    var availableJournals = repository.findAvailableJournals();

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
    var allItems = repository.findAvailableItems();
    assertFalse(allItems.isEmpty());

    // Verify specific sample books and journals
    var books = repository.findAvailableBooks();
    var journals = repository.findAvailableJournals();

    assertFalse(books.isEmpty());
    assertFalse(journals.isEmpty());

    // Verify some specific titles from the sample data
    var cleanCodeBooks = repository.findByTitleContainingIgnoreCase("Clean Code");
    assertFalse(cleanCodeBooks.isEmpty());

    var natureJournals = repository.findByTitleContainingIgnoreCase("Nature");
    assertFalse(natureJournals.isEmpty());
  }

  @Test
  @DisplayName("Save and retrieve different item types should maintain type information")
  void saveAndRetrieve_DifferentItemTypes_ShouldMaintainType() {
    // Arrange
    var book = new Book("Test Book", "Author", LocalDate.now(), "123", "Category", 100);
    var journal =
        new Journal("Test Journal", "Publisher", LocalDate.now(), "456", "Publisher Co", 10, 2);

    // Act
    var savedBook = repository.save(book);
    var savedJournal = repository.save(journal);

    // Assert
    var retrievedBook = repository.findById(savedBook.getId());
    var retrievedJournal = repository.findById(savedJournal.getId());

    assertTrue(retrievedBook.isPresent());
    assertInstanceOf(Book.class, retrievedBook.get());

    assertTrue(retrievedJournal.isPresent());
    assertInstanceOf(Journal.class, retrievedJournal.get());

    var retrievedBookCast = (Book) retrievedBook.get();
    var retrievedJournalCast = (Journal) retrievedJournal.get();

    assertEquals(100, retrievedBookCast.getPageCount());
    assertEquals(10, retrievedJournalCast.getVolume());
  }
}
