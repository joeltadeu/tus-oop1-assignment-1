package com.lms.library.model;

import java.time.LocalDate;

public final class Book extends LibraryItem {

    private String isbn;
    private String genre;
    private int pageCount;

    public Book() {}

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

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    public String getType() {
        return "BOOK";
    }
}
