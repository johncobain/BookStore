package br.edu.ifba.inf008.plugins.loan.persistence;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifba.inf008.shell.model.Book;
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import jakarta.persistence.EntityManager;

public class LoanDAOTest {
  private LoanDAO loanDAO;

  private EntityManager em;

  @BeforeEach
  void setUp() {
    loanDAO = new LoanDAO();
    em = TestJPAUtil.getEntityManager();
    em.getTransaction().begin();
    em.createQuery("DELETE FROM Loan").executeUpdate();
    em.createQuery("DELETE FROM Book").executeUpdate();
    em.createQuery("DELETE FROM User").executeUpdate();
    em.getTransaction().commit();
  }

  @AfterEach
  void tearDown() {
    if (em.isOpen()) em.close();
  }

  private User createUser(String name, String email) {
    User user = new User(name, email);
    em.getTransaction().begin();
    em.persist(user);
    em.getTransaction().commit();
    return user;
  }

  private Book createBook(String title, String author, String isbn, int year, int copies) {
    Book book = new Book(title, author, isbn, year, copies);
    em.getTransaction().begin();
    em.persist(book);
    em.getTransaction().commit();
    return book;
  }

  private Book getManagedBook(Book book) {
    Book managedBook;
    try (EntityManager emDao = loanDAO.getEntityManager()) {
      managedBook = emDao.find(Book.class, book.getBookId());
    }
    return managedBook;
  }

  @Test
  void testCreateLoanDecrementsCopies() {
    User user = createUser("User1", "user1@test.com");
    Book book = createBook("Book1", "Author1", "987654321", 2022, 2);

    Book managedBook = getManagedBook(book);
    Loan loan = new Loan(user, managedBook, LocalDate.now(), null);
    loanDAO.save(loan);

    Book updatedBook = getManagedBook(book);
    assertEquals(1, updatedBook.getCopiesAvailable());
  }

  @Test
  void testReturnLoanIncrementsCopies() {
    User user = createUser("User2", "user2@test.com");
    Book book = createBook("Book2", "Author2", "123456789", 2022, 1);

    Book managedBook = getManagedBook(book);
    Loan loan = new Loan(user, managedBook, LocalDate.now(), null);
    loanDAO.save(loan);

    loanDAO.returnLoan(loan);

    Book updatedBook = getManagedBook(book);
    assertEquals(1, updatedBook.getCopiesAvailable());
  }

  @Test
  void testFindActiveLoans() {
    User user = createUser("User3", "user3@test.com");
    Book book = createBook("Book3", "Author3", "123456789", 2022, 1);

    Book managedBook = getManagedBook(book);
    Loan loan = new Loan(user, managedBook, LocalDate.now(), null);
    loanDAO.save(loan);

    List<Loan> activeLoans = loanDAO.findActiveLoans();
    assertTrue(activeLoans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));

    loanDAO.returnLoan(loan);

    List<Loan> activeLoansAfterReturn = loanDAO.findActiveLoans();
    assertTrue(activeLoansAfterReturn.stream().noneMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testListUsersAndBooks() {
    User user = createUser("User4", "user4@test.com");
    Book book = createBook("Book4", "Author4", "123456789", 2022, 1);

    List<User> users = loanDAO.listUsers();
    List<Book> books = loanDAO.listBooks();

    assertTrue(users.stream().anyMatch(u -> u.getUserId().equals(user.getUserId())));
    assertTrue(books.stream().anyMatch(b -> b.getBookId().equals(book.getBookId())));
  }

  @Test
  void testCreateLoanWithNoCopiesThrows() {
    User user = createUser("User5", "user5@test.com");
    Book book = createBook("Book5", "Author5", "123456789", 2022, 0);

    Book managedBook = getManagedBook(book);
    Loan loan = new Loan(user, managedBook, LocalDate.now(), null);
    Exception ex = assertThrows(IllegalStateException.class, () -> loanDAO.save(loan));
    assertEquals("No copies available for this book", ex.getMessage());
  }

  @Test
  void testDeleteLoanIncrementsCopies() {
    User user = createUser("User6", "user6@test.com");
    Book book = createBook("Book6", "Author6", "123456780", 2022, 1);

    Book managedBook = getManagedBook(book);
    Loan loan = new Loan(user, managedBook, LocalDate.now(), null);
    loanDAO.save(loan);

    Book afterLoan = getManagedBook(book);
    assertEquals(0, afterLoan.getCopiesAvailable());

    loanDAO.delete(loan);

    Book afterDelete = getManagedBook(book);
    assertEquals(1, afterDelete.getCopiesAvailable());
  }

  @Test
  void testUpdateLoanChangesBookCopies() {
    User user = createUser("User7", "user7@test.com");
    Book book1 = createBook("Book7A", "Author7A", "123456781", 2022, 1);
    Book book2 = createBook("Book7B", "Author7B", "123456782", 2022, 1);

    Book managedBook1 = getManagedBook(book1);
    Book managedBook2 = getManagedBook(book2);

    Loan loan = new Loan(user, managedBook1, LocalDate.now(), null);
    loanDAO.save(loan);

    Book afterLoanBook1 = getManagedBook(book1);
    assertEquals(0, afterLoanBook1.getCopiesAvailable());
    Book afterLoanBook2 = getManagedBook(book2);
    assertEquals(1, afterLoanBook2.getCopiesAvailable());

    loan.setBook(managedBook2);
    loanDAO.update(loan);

    Book afterUpdateBook1 = getManagedBook(book1);
    Book afterUpdateBook2 = getManagedBook(book2);
    assertEquals(1, afterUpdateBook1.getCopiesAvailable());
    assertEquals(0, afterUpdateBook2.getCopiesAvailable());
  }
}
