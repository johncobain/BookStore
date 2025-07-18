package br.edu.ifba.inf008.shell.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "book_id")
  private Integer bookId;

  @Column( nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 100)
  private String author;

  @Column(nullable = false, unique = true, length = 20)
  private String isbn;

  @Column(name = "published_year")
  private Integer publishedYear;

  @Column(name = "copies_available")
  private Integer copiesAvailable;

  public Book() {}

  public Book(String title, String author, String isbn, Integer publishedYear, Integer copiesAvailable) {
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.publishedYear = publishedYear;
    this.copiesAvailable = copiesAvailable;
  }

  public Book(Integer bookId, String title, String author, String isbn, Integer publishedYear, Integer copiesAvailable) {
    this.bookId = bookId;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.publishedYear = publishedYear;
    this.copiesAvailable = copiesAvailable;
  }

  public Integer getBookId() {
    return bookId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public Integer getPublishedYear() {
    return publishedYear;
  }

  public void setPublishedYear(Integer publishedYear) {
    this.publishedYear = publishedYear;
  }

  public Integer getCopiesAvailable() {
    return copiesAvailable;
  }

  public void setCopiesAvailable(Integer copiesAvailable) {
    this.copiesAvailable = copiesAvailable;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Book book = (Book) obj;
    
    if (bookId != null && book.bookId != null) {
      return Objects.equals(bookId, book.bookId);
    }
    
    return Objects.equals(isbn, book.isbn);
  }

  @Override
  public int hashCode() {
    if (bookId != null) {
      return Objects.hash(bookId);
    }
    return Objects.hash(isbn);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Book{");
    sb.append("bookId=").append(bookId);
    sb.append(", title=").append(title);
    sb.append(", author=").append(author);
    sb.append(", isbn=").append(isbn);
    sb.append(", publishedYear=").append(publishedYear);
    sb.append(", copiesAvailable=").append(copiesAvailable);
    sb.append('}');
    return sb.toString();
  }

  
}
