package br.edu.ifba.inf008.shell.model;

import java.time.LocalDate;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((loanId == null) ? 0 : loanId.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    result = prime * result + ((book == null) ? 0 : book.hashCode());
    result = prime * result + ((loanDate == null) ? 0 : loanDate.hashCode());
    result = prime * result + ((returnDate == null) ? 0 : returnDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Loan other = (Loan) obj;
    if (loanId == null) {
      if (other.loanId != null)
        return false;
    } else if (!loanId.equals(other.loanId))
      return false;
    if (user == null) {
      if (other.user != null)
        return false;
    } else if (!user.equals(other.user))
      return false;
    if (book == null) {
      if (other.book != null)
        return false;
    } else if (!book.equals(other.book))
      return false;
    if (loanDate == null) {
      if (other.loanDate != null)
        return false;
    } else if (!loanDate.equals(other.loanDate))
      return false;
    if (returnDate == null) {
      if (other.returnDate != null)
        return false;
    } else if (!returnDate.equals(other.returnDate))
      return false;
    return true;
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
