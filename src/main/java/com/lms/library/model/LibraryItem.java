package com.lms.library.model;

import java.time.LocalDate;

public sealed abstract class LibraryItem permits Book, Journal {

    private Long id;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private boolean available = true;

    protected LibraryItem() {
    }

    protected LibraryItem(String title, String author, LocalDate publicationDate) {
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public abstract String getType();
}
