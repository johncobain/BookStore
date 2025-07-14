CREATE DATABASE IF NOT EXISTS bookstore;
USE bookstore;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    published_year INT,
    copies_available INT DEFAULT 0
);

CREATE TABLE loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    loan_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

INSERT INTO users (name, email) VALUES
('Alice Johnson', 'alice@example.com'),
('Bob Smith', 'bob@example.com'),
('Carol White', 'carol@example.com');

INSERT INTO books (title, author, isbn, published_year, copies_available) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 1925, 3),
('1984', 'George Orwell', '9780451524935', 1949, 5),
('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 1960, 2);

INSERT INTO loans (user_id, book_id, loan_date, return_date) VALUES
(1, 2, '2025-07-01', NULL),
(2, 1, '2025-07-03', '2025-07-07'),
(3, 3, '2025-07-05', NULL);
