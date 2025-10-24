package com.lms.library.repository;

import com.lms.library.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    @Query("SELECT li FROM LibraryItem li WHERE li.available = true")
    List<LibraryItem> findAvailableItems();

    @Query("SELECT li FROM LibraryItem li WHERE li.id = :id AND li.available = true")
    Optional<LibraryItem> findAvailableItemById(@Param("id") Long id);

    List<LibraryItem> findByTitleContainingIgnoreCase(String title);

    // Explicit queries for subtypes to avoid proxy issues
    @Query("SELECT b FROM Book b WHERE b.available = true")
    List<LibraryItem> findAvailableBooks();

    @Query("SELECT j FROM Journal j WHERE j.available = true")
    List<LibraryItem> findAvailableJournals();
}