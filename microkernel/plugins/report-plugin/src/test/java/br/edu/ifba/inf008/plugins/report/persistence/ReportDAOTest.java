package br.edu.ifba.inf008.plugins.report.persistence;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifba.inf008.shell.model.Book;
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import jakarta.persistence.EntityManager;

public class ReportDAOTest {
  private ReportDAO reportDAO;
  private EntityManager em;

  @BeforeEach
  void setUp() {
    reportDAO = new ReportDAO();
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

  private Loan createLoan(User user, Book book, LocalDate loanDate, LocalDate returnDate) {
    Loan loan = new Loan(user, book, loanDate, returnDate);
    em.getTransaction().begin();
    em.persist(loan);
    em.getTransaction().commit();
    return loan;
  }

  @Test
  void testFindAll() {
    User user = createUser("User1", "user1@test.com");
    Book book = createBook("Book1", "Author1", "111", 2022, 1);
    Loan loan = createLoan(user, book, LocalDate.now(), null);

    List<Loan> loans = reportDAO.findAll();
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testFindByInitialDateBefore() {
    User user = createUser("User2", "user2@test.com");
    Book book = createBook("Book2", "Author2", "222", 2022, 1);
    LocalDate date = LocalDate.of(2022, 1, 1);
    Loan loan = createLoan(user, book, date, null);

    List<Loan> loans = reportDAO.findByInitialDate(LocalDate.of(2022, 2, 1), "before");
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testFindByInitialDateAfter() {
    User user = createUser("User3", "user3@test.com");
    Book book = createBook("Book3", "Author3", "333", 2022, 1);
    LocalDate date = LocalDate.of(2022, 3, 1);
    Loan loan = createLoan(user, book, date, null);

    List<Loan> loans = reportDAO.findByInitialDate(LocalDate.of(2022, 2, 1), "after");
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testFindByInitialDateOnDate() {
    User user = createUser("User4", "user4@test.com");
    Book book = createBook("Book4", "Author4", "444", 2022, 1);
    LocalDate date = LocalDate.of(2022, 2, 1);
    Loan loan = createLoan(user, book, date, null);

    List<Loan> loans = reportDAO.findByInitialDate(LocalDate.of(2022, 2, 1), "onDate");
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testFindByInitialAndFinalDateBetween() {
    User user = createUser("User5", "user5@test.com");
    Book book = createBook("Book5", "Author5", "555", 2022, 1);
    LocalDate date = LocalDate.of(2022, 2, 15);
    Loan loan = createLoan(user, book, date, null);

    List<Loan> loans = reportDAO.findByInitialAndFinalDate(LocalDate.of(2022, 2, 1), LocalDate.of(2022, 02, 28));
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testFindByStatusActive() {
    User user = createUser("User6", "user6@test.com");
    Book book = createBook("Book6", "Author6", "666", 2022, 1);
    Loan loan = createLoan(user, book, LocalDate.now(), null);

    List<Loan> loans = reportDAO.findByStatus(true);
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }

  @Test
  void testFindByStatusReturned() {
    User user = createUser("User7", "user7@test.com");
    Book book = createBook("Book7", "Author7", "777", 2022, 1);
    Loan loan = createLoan(user, book, LocalDate.now(), LocalDate.now());

    List<Loan> loans = reportDAO.findByStatus(false);
    assertTrue(loans != null && !loans.isEmpty());
    assertTrue(loans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())));
  }
}
