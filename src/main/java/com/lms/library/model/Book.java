package com.lms.library.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("BOOK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

public final class Book extends LibraryItem {

    private String isbn;
    private String genre;
    private int pageCount;

    public Book(String title, String author, LocalDate publicationDate,
                String isbn, String genre, int pageCount) {
        super(title, author, publicationDate);
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
    }

    @Override
    public String getType() {
        return "BOOK";
    }
}
