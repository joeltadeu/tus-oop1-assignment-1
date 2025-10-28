package com.lms.library.repository;

import com.lms.library.model.Book;
import com.lms.library.model.Journal;
import com.lms.library.model.LibraryItem;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LibraryItemRepository {

    private static final Logger log = LoggerFactory.getLogger(LibraryItemRepository.class);

    private static final Map<Long, LibraryItem> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    @PostConstruct
    public void init() {
        // --- Preload some Books ---
        save(new Book("Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1), "9780132350884", "Programming", 464));

        save(new Book("Effective Java", "Joshua Bloch",
                LocalDate.of(2018, 1, 6), "9780134685991", "Programming", 416));

        save(new Book("The Pragmatic Programmer", "Andrew Hunt",
                LocalDate.of(1999, 10, 30), "9780201616224", "Software Engineering", 352));

        // --- Preload some Journals ---
        save(new Journal("Nature Neuroscience", "Various",
                LocalDate.of(2024, 5, 10), "1234-5678", "Nature Publishing Group", 29, 5));

        save(new Journal("IEEE Transactions on Computers", "Various",
                LocalDate.of(2023, 11, 20), "0018-9340", "IEEE", 72, 11));

        log.info("LibraryItemRepository initialized with {} items.", STORE.size());
    }

    public LibraryItem save(LibraryItem item) {
        if (item.getId() == null) {
            item.setId(ID_SEQ.getAndIncrement());
        }
        STORE.put(item.getId(), item);
        return item;
    }

    public Optional<LibraryItem> findById(Long id) {
        return Optional.ofNullable(STORE.get(id));
    }

    public Optional<LibraryItem> findAvailableItemById(Long id) {
        return findById(id).filter(LibraryItem::isAvailable);
    }

    public List<LibraryItem> findAvailableItems() {
        return STORE.values().stream().filter(LibraryItem::isAvailable).toList();
    }

    public List<LibraryItem> findByTitleContainingIgnoreCase(String title) {
        return STORE.values().stream()
                .filter(i -> i.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    public List<LibraryItem> findAvailableBooks() {
        return STORE.values().stream()
                .filter(i -> i instanceof Book && i.isAvailable())
                .toList();
    }

    public List<LibraryItem> findAvailableJournals() {
        return STORE.values().stream()
                .filter(i -> i instanceof Journal && i.isAvailable())
                .toList();
    }
}