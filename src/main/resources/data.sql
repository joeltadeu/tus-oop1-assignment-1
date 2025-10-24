-- Clear existing data
DELETE FROM loan_items;
DELETE FROM loans;
DELETE FROM library_items;
DELETE FROM members;

-- Reset sequences (H2 specific)
ALTER TABLE members ALTER COLUMN id RESTART WITH 1;
ALTER TABLE library_items ALTER COLUMN id RESTART WITH 1;
ALTER TABLE loans ALTER COLUMN id RESTART WITH 1;
ALTER TABLE loan_items ALTER COLUMN id RESTART WITH 1;

-- Insert sample members
INSERT INTO members (id, first_name, last_name, email)
VALUES (1, 'John', 'Doe', 'john.doe@email.com');

INSERT INTO members (id, first_name, last_name, email)
VALUES (2, 'Jane', 'Smith', 'jane.smith@email.com');

INSERT INTO members (id, first_name, last_name, email)
VALUES (3, 'Bob', 'Johnson', 'bob.johnson@email.com');

-- Insert sample books
INSERT INTO library_items (item_type, title, author, publication_date, available, isbn, genre, page_count)
VALUES ('BOOK', 'Clean Code', 'Robert C. Martin', '2008-08-01', TRUE, '978-0132350884', 'Programming', 464);

INSERT INTO library_items (item_type, title, author, publication_date, available, isbn, genre, page_count)
VALUES ('BOOK', 'Effective Java', 'Joshua Bloch', '2018-01-01', TRUE, '978-0134685991', 'Programming', 416);

INSERT INTO library_items (item_type, title, author, publication_date, available, isbn, genre, page_count)
VALUES ('BOOK', '1984', 'George Orwell', '1949-06-08', TRUE, '978-0451524935', 'Dystopian', 328);

-- Insert sample journals
INSERT INTO library_items (item_type, title, author, publication_date, available, issn, publisher, volume, issue)
VALUES ('JOURNAL', 'Journal 2 Method 1', 'Research Team', '2024-01-01', TRUE, '0028-0836', 'Springer Nature', 625, 7996);

INSERT INTO library_items (item_type, title, author, publication_date, available, issn, publisher, volume, issue)
VALUES ('JOURNAL', 'Paper 1 Method 1', 'Science Group', '2024-02-01', TRUE, '0036-8075', 'AAAS', 383, 6682);