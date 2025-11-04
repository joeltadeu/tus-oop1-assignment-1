package com.lms.library.model;

import java.time.LocalDate;

/**
 * Represents a book in the library system.
 * Extends LibraryItem and contains book-specific properties like ISBN, genre, and page count.
 *
 * @author Joel Silva
 * @version 1.0
 * @see LibraryItem
 * @since 2025
 */
public final class Book extends LibraryItem {

    private String isbn;
    private String genre;
    private int pageCount;

    /**
     * Default constructor for Book.
     * Required for serialization and dependency injection frameworks.
     */
    public Book() {}

    /**
     * Constructs a new Book with specified details.
     *
     * @param title the title of the book
     * @param author the author of the book
     * @param publicationDate the date when the book was published
     * @param isbn the International Standard Book Number
     * @param genre the genre of the book
     * @param pageCount the number of pages in the book
     * @throws IllegalArgumentException if pageCount is not positive
     */
    public Book(String title, String author, LocalDate publicationDate,
                String isbn, String genre, int pageCount) {

        if (pageCount <= 0) {
            throw new IllegalArgumentException("Page count must be positive");
        }

        super(title, author, publicationDate);
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return the ISBN string
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Gets the genre of the book.
     *
     * @return the genre string
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Gets the page count of the book.
     *
     * @return the number of pages
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Gets the type of library item.
     * Always returns "BOOK" for Book instances.
     *
     * @return the string "BOOK"
     */
    @Override
    public String getType() {
        return "BOOK";
    }
}
