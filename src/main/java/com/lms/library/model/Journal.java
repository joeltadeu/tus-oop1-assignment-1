package com.lms.library.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@DiscriminatorValue("JOURNAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public final class Journal extends LibraryItem {

    private String issn;
    private String publisher;
    private int volume;
    private int issue;

    public Journal(String title, String author, LocalDate publicationDate,
                   String issn, String publisher, int volume, int issue) {
        super(title, author, publicationDate);
        this.issn = issn;
        this.publisher = publisher;
        this.volume = volume;
        this.issue = issue;
    }

    @Override
    public String getType() {
        return "JOURNAL";
    }
}
