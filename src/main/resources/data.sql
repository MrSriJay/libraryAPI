-- Insert 5 Borrowers
INSERT INTO Borrower (name, email) VALUES
('Sahan Fernando', 'sahan.fernando@yahoo.com'),
('Jane Smith', 'jane.smith@gmail.com'),
('Achini Silva', 'achini.silva@hotmail.com'),
('Tanvir Khan', 'tanvir.khan@gmail.com'),
('Amitave Roy', 'amitave.roy@hotmail.com'),
('Tahansha Perera', 'tahansha.perera@gmail.com');

-- Insert 10 Books (some with same ISBN, some borrowed)
INSERT INTO Book (isbn, title, author, borrowed_by) VALUES
('978-3-16-148410-0', 'The Great Gatsby', 'F. Scott Fitzgerald', NULL),
('978-3-16-148410-0', 'The Great Gatsby', 'F. Scott Fitzgerald', 1),
('978-0-14-243733-9', '1984', 'George Orwell', NULL),
('978-0-14-243733-9', '1984', 'George Orwell', 2),
('978-0-452-28423-4', 'To Kill a Mockingbird', 'Harper Lee', NULL),
('978-0-452-28423-4', 'To Kill a Mockingbird', 'Harper Lee', 3),
('978-0-7432-7356-5', 'The Da Vinci Code', 'Dan Brown', NULL),
('978-0-7432-7356-5', 'The Da Vinci Code', 'Dan Brown', NULL),
('978-0-670-81302-5', 'Pride and Prejudice', 'Jane Austen', 4),
('978-0-670-81302-5', 'Pride and Prejudice', 'Jane Austen', NULL);