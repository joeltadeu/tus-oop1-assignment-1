package com.lms.library.model;

import java.time.LocalDate;

public final class Journal extends LibraryItem {

    private String issn;
    private String publisher;
    private int volume;
    private int issue;

    public Journal() {
    }

    public Journal(String title, String author, LocalDate publicationDate,
                   String issn, String publisher, int volume, int issue) {
        super(title, author, publicationDate);
        this.issn = issn;
        this.publisher = publisher;
        this.volume = volume;
        this.issue = issue;
    }

    public String getIssn() {
        return issn;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getVolume() {
        return volume;
    }

    public int getIssue() {
        return issue;
    }

    @Override
    public String getType() {
        return "JOURNAL";
    }
}
