/**
 * Abstract base class representing a library item.
 * Serves as the parent class for all library items like books and journals.
 * This is a sealed class that only permits Book and Journal as subclasses.
 *
 * @author Joel Silva
 * @version 1.0
 * @see Book
 * @see Journal
 * @since 2025
 */
package com.lms.library.model;

import java.time.LocalDate;

public sealed abstract class LibraryItem permits Book, Journal {

    private Long id;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private boolean available = true;

    /**
     * Protected default constructor for LibraryItem.
     * Required for JPA and serialization.
     */
    protected LibraryItem() {
    }

    /**
     * Constructs a new LibraryItem with basic details.
     *
     * @param title           the title of the item
     * @param author          the author of the item
     * @param publicationDate the publication date of the item
     */
    protected LibraryItem(String title, String author, LocalDate publicationDate) {
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
    }

    /**
     * Gets the unique identifier of the library item.
     *
     * @return the item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the title of the library item.
     *
     * @return the item title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the author of the library item.
     *
     * @return the author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the publication date of the library item.
     *
     * @return the publication date
     */
    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    /**
     * Checks if the library item is available for loan.
     *
     * @return true if the item is available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the unique identifier of the library item.
     *
     * @param id the new ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the availability status of the library item.
     *
     * @param available true to mark as available, false to mark as unavailable
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Gets the type of library item.
     * Must be implemented by subclasses to return their specific type.
     *
     * @return the type of library item as a string
     */
    public abstract String getType();
}
