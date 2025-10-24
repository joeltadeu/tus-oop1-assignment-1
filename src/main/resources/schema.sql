-- Drop tables if they exist (for clean startup)
DROP TABLE IF EXISTS loan_items;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS library_items;
DROP TABLE IF EXISTS members;

-- Create members table
CREATE TABLE members
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE
);

-- Create library_items table with SINGLE_TABLE inheritance
CREATE TABLE library_items
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_type        VARCHAR(31)  NOT NULL,
    title            VARCHAR(255) NOT NULL,
    author           VARCHAR(255) NOT NULL,
    publication_date DATE,
    available        BOOLEAN      NOT NULL DEFAULT true,
    -- Book specific fields
    isbn             VARCHAR(255),
    genre            VARCHAR(255),
    page_count       INT,
    -- Journal specific fields
    issn             VARCHAR(255),
    publisher        VARCHAR(255),
    volume           INT,
    issue            INT
);

-- Create loans table
CREATE TABLE loans
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id            BIGINT      NOT NULL,
    loan_date            DATE        NOT NULL,
    expected_return_date DATE        NOT NULL,
    status               VARCHAR(20) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members (id)
);

-- Create loan_items table
CREATE TABLE loan_items
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id       BIGINT NOT NULL,
    item_id       BIGINT NOT NULL,
    returned_date DATE,
    FOREIGN KEY (loan_id) REFERENCES loans (id),
    FOREIGN KEY (item_id) REFERENCES library_items (id)
);