/**
 * Represents a journal in the library system.
 * Extends LibraryItem and contains journal-specific properties like ISSN, publisher, volume, and issue.
 *
 * @author Joel Silva
 * @version 1.0
 * @see LibraryItem
 * @since 2025
 */
package com.lms.library.model;

import java.time.LocalDate;

public final class Journal extends LibraryItem {

    private String issn;
    private String publisher;
    private int volume;
    private int issue;

    /**
     * Default constructor for Journal.
     * Required for serialization and dependency injection frameworks.
     */
    public Journal() {
    }

    /**
     * Constructs a new Journal with specified details.
     *
     * @param title           the title of the journal
     * @param author          the author of the journal
     * @param publicationDate the date when the journal was published
     * @param issn            the International Standard Serial Number
     * @param publisher       the publisher of the journal
     * @param volume          the volume number of the journal
     * @param issue           the issue number of the journal
     */
    public Journal(String title, String author, LocalDate publicationDate,
                   String issn, String publisher, int volume, int issue) {
        super(title, author, publicationDate);
        this.issn = issn;
        this.publisher = publisher;
        this.volume = volume;
        this.issue = issue;
    }

    /**
     * Gets the ISSN of the journal.
     *
     * @return the ISSN string
     */
    public String getIssn() {
        return issn;
    }

    /**
     * Gets the publisher of the journal.
     *
     * @return the publisher name
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets the volume number of the journal.
     *
     * @return the volume number
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Gets the issue number of the journal.
     *
     * @return the issue number
     */
    public int getIssue() {
        return issue;
    }

    /**
     * Gets the type of library item.
     * Always returns "JOURNAL" for Journal instances.
     *
     * @return the string "JOURNAL"
     */
    @Override
    public String getType() {
        return "JOURNAL";
    }
}
