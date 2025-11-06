package com.lms.library.repository;

import com.lms.library.model.Book;
import com.lms.library.model.Journal;
import com.lms.library.model.LibraryItem;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing LibraryItem entities. Provides data access operations for books and
 * journals using an in-memory store.
 *
 * @author Joel Silva
 * @version 1.0
 * @see LibraryItem
 * @see Book
 * @see Journal
 * @since 2025
 */
@Repository
public class LibraryItemRepository {

  private static final Logger log = LoggerFactory.getLogger(LibraryItemRepository.class);

  private static final Map<Long, LibraryItem> STORE = new ConcurrentHashMap<>();
  private static final AtomicLong ID_SEQ = new AtomicLong(1);

  /** Default constructor for LibraryItemRepository. */
  public LibraryItemRepository() {}

  /**
   * Initializes the repository with sample data. Called automatically after dependency injection is
   * complete.
   */
  @PostConstruct
  public void init() {
    // --- Preload some Books ---
    save(
        new Book(
            "Clean Code",
            "Robert C. Martin",
            LocalDate.of(2008, 8, 1),
            "9780132350884",
            "Programming",
            464));

    save(
        new Book(
            "Effective Java",
            "Joshua Bloch",
            LocalDate.of(2018, 1, 6),
            "9780134685991",
            "Programming",
            416));

    save(
        new Book(
            "The Pragmatic Programmer",
            "Andrew Hunt",
            LocalDate.of(1999, 10, 30),
            "9780201616224",
            "Software Engineering",
            352));

    // --- Preload some Journals ---
    save(
        new Journal(
            "Nature Neuroscience",
            "Various",
            LocalDate.of(2024, 5, 10),
            "1234-5678",
            "Nature Publishing Group",
            29,
            5));

    save(
        new Journal(
            "IEEE Transactions on Computers",
            "Various",
            LocalDate.of(2023, 11, 20),
            "0018-9340",
            "IEEE",
            72,
            11));

    log.info("LibraryItemRepository initialized with {} items.", STORE.size());
  }

  /**
   * Saves a library item to the repository. If the item has no ID, generates a new one
   * automatically.
   *
   * @param item the library item to save
   * @return the saved library item with generated ID
   */
  public LibraryItem save(LibraryItem item) {
    if (item.getId() == null) {
      item.setId(ID_SEQ.getAndIncrement());
    }
    STORE.put(item.getId(), item);
    return item;
  }

  /**
   * Finds a library item by its ID.
   *
   * @param id the ID of the item to find
   * @return an Optional containing the found item, or empty if not found
   */
  public Optional<LibraryItem> findById(Long id) {
    return Optional.ofNullable(id).map(STORE::get);
  }

  /**
   * Finds an available library item by its ID. Only returns items that are currently available for
   * loan.
   *
   * @param id the ID of the item to find
   * @return an Optional containing the available item, or empty if not found or unavailable
   */
  public Optional<LibraryItem> findAvailableItemById(Long id) {
    return findById(id).filter(LibraryItem::isAvailable);
  }

  /**
   * Finds all available library items.
   *
   * @return a list of all available library items
   */
  public List<LibraryItem> findAvailableItems() {
    return STORE.values().stream().filter(LibraryItem::isAvailable).toList();
  }

  /**
   * Finds library items by title (case-insensitive partial match).
   *
   * @param title the title or partial title to search for
   * @return a list of items whose titles contain the search string
   */
  public List<LibraryItem> findByTitleContainingIgnoreCase(String title) {
    return STORE.values().stream()
        .filter(i -> i.getTitle().toLowerCase().contains(title.toLowerCase()))
        .toList();
  }

  /**
   * Finds all available books.
   *
   * @return a list of all available Book instances
   */
  public List<LibraryItem> findAvailableBooks() {
    return STORE.values().stream().filter(i -> i instanceof Book && i.isAvailable()).toList();
  }

  /**
   * Finds all available journals.
   *
   * @return a list of all available Journal instances
   */
  public List<LibraryItem> findAvailableJournals() {
    return STORE.values().stream().filter(i -> i instanceof Journal && i.isAvailable()).toList();
  }
}
