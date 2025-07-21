package br.edu.ifba.inf008.shell.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loan_id")
  private Integer loanId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(name = "loan_date", nullable = false)
  private LocalDate loanDate;

  @Column(name = "return_date")
  private LocalDate returnDate;

  public Loan() {}

  public Loan(User user, Book book, LocalDate loanDate, LocalDate returnDate) {
    this.user = user;
    this.book = book;
    this.loanDate = loanDate;
    this.returnDate = returnDate;
  }

  public Loan(Integer loanId, User user, Book book, LocalDate loanDate, LocalDate returnDate) {
    this.loanId = loanId;
    this.user = user;
    this.book = book;
    this.loanDate = loanDate;
    this.returnDate = returnDate;
  }

  public Integer getLoanId() {
    return loanId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Book getBook() {
    return book;
  }

  public void setBook(Book book) {
    this.book = book;
  }

  public LocalDate getLoanDate() {
    return loanDate;
  }

  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public void returnBook() {
    this.returnDate = LocalDate.now();
  }

  @Override
public boolean equals(Object obj) {
  if (this == obj) return true;
  if (obj == null || getClass() != obj.getClass()) return false;
  Loan loan = (Loan) obj;

  if (loanId != null && loan.loanId != null) {
    return loanId.equals(loan.loanId);
  }
  return Objects.equals(user, loan.user) &&
         Objects.equals(book, loan.book) &&
         Objects.equals(loanDate, loan.loanDate);
}

@Override
public int hashCode() {
  if (loanId != null) {
    return Objects.hash(loanId);
  }
  return Objects.hash(user, book, loanDate);
}

  @Override
  public String toString() {
    return String.format("Loan[id=%d, userId=%s, bookId=%s, loanDate=%s, returnDate=%s]", 
      loanId,
      user != null ? user.getUserId() : "null",
      book != null ? book.getBookId() : "null", 
      loanDate, 
      returnDate);
  }
}
