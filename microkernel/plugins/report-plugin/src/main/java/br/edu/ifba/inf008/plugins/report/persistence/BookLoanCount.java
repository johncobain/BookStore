package br.edu.ifba.inf008.plugins.report.persistence;

import br.edu.ifba.inf008.shell.model.Book;

public class BookLoanCount {
  private final Book book;
  private final Long count;

  public BookLoanCount(Book book, Long count) {
    this.book = book;
    this.count = count;
  }

  public Book getBook() { return book; }
  public Long getCount() { return count; }
}