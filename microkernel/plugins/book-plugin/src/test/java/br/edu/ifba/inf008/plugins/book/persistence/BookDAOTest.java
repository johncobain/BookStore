package br.edu.ifba.inf008.plugins.book.persistence;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifba.inf008.shell.model.Book;
import jakarta.persistence.EntityManager;
 
public class BookDAOTest {
  private BookDAO bookDAO;
  private Book testBook;
  private List<Book> createdBooks;

  private Book saveAndTrack(Book book){
    bookDAO.save(book);
    if (book.getBookId() == null) {
      throw new RuntimeException("Book was not properly saved - no ID assigned");
    }
    createdBooks.add(book);
    System.out.println("✅ Saved book: " + book.getTitle() + " with ID: " + book.getBookId());
    return book;
  }

  @BeforeEach
  void setUp(){
    bookDAO = new BookDAO();
    createdBooks = new ArrayList<>();

    String uniqueIsbn = String.valueOf((long)(Math.random() * 1_000_000_0000L));

    testBook = new Book("Test Book", "Test Author", uniqueIsbn, 2022, 10);
  }

  @AfterEach
  void cleanup(){
    if (createdBooks != null) {
      try (EntityManager em = TestJPAUtil.getEntityManager()) {
        em.getTransaction().begin();
        for (Book book : createdBooks) {
          try {
            if (book.getBookId() != null) {
              Book managed = em.find(Book.class, book.getBookId());
              if (managed != null) {
                em.remove(managed);
              }
            }
          } catch (Exception e) {
            System.err.println("Error cleaning book: " + book.getTitle() + " - " + e.getMessage());
          }
        }
        em.getTransaction().commit();
      } catch (Exception e) {
      }
      createdBooks.clear();
    }
  }
  
  @AfterAll
  static void tearDown() {
    TestJPAUtil.closeEntityManagerFactory();
  }

  @Test
  void testDatabaseConnection() {
    try (EntityManager em = TestJPAUtil.getEntityManager()) {
      String url = em.getEntityManagerFactory().getProperties().get("jakarta.persistence.jdbc.url").toString();
      assertTrue(url.contains("bookstore_test"), "Should use unified test database");
      System.out.println("✅ Book plugin using unified H2 test database: " + url);
    }
  }

  @Test
  void testSaveBook() {
    Book createdBook = saveAndTrack(testBook);
    assertNotNull(createdBook.getBookId(), "Book ID should not be null");
  }

  @Test
  void testFindBookById(){
    Book createdBook = saveAndTrack(testBook);

    Book foundBook = bookDAO.findById(createdBook.getBookId());

    assertNotNull(foundBook, "Found book should not be null");
    assertEquals("Test Book", foundBook.getTitle(), "Book title should match");
  }

  @Test
  void testFindBookByTitle(){
    Book createdBook = saveAndTrack(testBook);

    List<Book> foundBooks = bookDAO.findAll("title", createdBook.getTitle());

    assertNotNull(foundBooks, "Found books list should not be null");
    assertFalse(foundBooks.isEmpty(), "Found books list should not be empty");
    assertTrue(foundBooks.size() >= 1, "Should find at least 1 book");

    Book foundBook = foundBooks.get(0);
    assertEquals(testBook.getTitle(), foundBook.getTitle(), "Book title should match");
  }
  
  @Test
  void testFindBookByAuthor(){
    Book createdBook = saveAndTrack(testBook);

    List<Book> foundBooks = bookDAO.findAll("author", createdBook.getAuthor());

    assertNotNull(foundBooks, "Found books list should not be null");
    assertFalse(foundBooks.isEmpty(), "Found books list should not be empty");
    assertTrue(foundBooks.size() >= 1, "Should find at least 1 book");
    
    Book foundBook = foundBooks.get(0);
    assertEquals(testBook.getAuthor(), foundBook.getAuthor(), "Book author should match");
  }
  
    @Test
    void testFindBookByIsbn(){
      Book createdBook = saveAndTrack(testBook);
  
      List<Book> foundBooks = bookDAO.findAll("isbn", createdBook.getIsbn());
  
      assertNotNull(foundBooks, "Found books list should not be null");
      assertFalse(foundBooks.isEmpty(), "Found books list should not be empty");
      assertTrue(foundBooks.size() >= 1, "Should find at least 1 book");
  
      Book foundBook = foundBooks.get(0);
      assertEquals(testBook.getIsbn(), foundBook.getIsbn(), "Book ISBN should match");
    }

  @Test
  void testFindBookByTitlePartial(){
    saveAndTrack(new Book("Python Programming", "Author 1", "1234567890", 2022, 10));
    saveAndTrack(new Book("Java Programming", "Author 2", "0987654321", 2021, 5));

    List<Book> foundByProgramming = bookDAO.findAll("title", "Programming");
    assertNotNull(foundByProgramming, "Found books list should not be null");
    assertTrue(foundByProgramming.size() >= 2, "Should find at least 2 books with 'Programming'");
  }

  @Test
  void testFindBookByAuthorPartial(){
    saveAndTrack(new Book("Book 1", "Author 1", "1234567890", 2022, 10));
    saveAndTrack(new Book("Book 2", "Author 2", "0987654321", 2021, 5));

    List<Book> foundByAuthor = bookDAO.findAll("author", "Author");
    assertNotNull(foundByAuthor, "Found books list should not be null");
    assertTrue(foundByAuthor.size() >= 2, "Should find at least 2 books with 'Author' in author name");
  }

  @Test
  void testFindBookByIsbnPartial(){
    saveAndTrack(new Book("Book 1", "Author 1", "1234567890", 2022, 10));
    saveAndTrack(new Book("Book 2", "Author 2", "0987654123", 2021, 5));

    List<Book> foundByIsbn = bookDAO.findAll("isbn", "123");
    assertNotNull(foundByIsbn, "Found books list should not be null");
    assertTrue(foundByIsbn.size() >= 2, "Should find at least 2 books with '123' in ISBN");
  }

  @Test
  void testFindAllBooks(){
    Book book1 = saveAndTrack(new Book("Book 1", "Author 1", "1234567890", 2022, 10));
    Book book2 = saveAndTrack(new Book("Book 2", "Author 2", "0987654321", 2021, 5));
    Book book3 = saveAndTrack(new Book("Book 3", "Author 3", "1122334455", 2020, 8));

    List<Book> books = bookDAO.findAll();

    assertNotNull(books, "Book list should not be null");
    assertTrue(books.size() >= 3, "There should be at least 3 books in the list");
    assertTrue(books.contains(book1), "Book list should contain book1");
    assertTrue(books.contains(book2), "Book list should contain book2");
    assertTrue(books.contains(book3), "Book list should contain book3");
  }

  @Test
  void testUpdateBook(){
    Book createdBook = saveAndTrack(testBook);
    String updatedTitle = "Updated Book Name";
    createdBook.setTitle(updatedTitle);

    bookDAO.update(createdBook);

    Book updatedBook = bookDAO.findById(createdBook.getBookId());
    assertNotNull(updatedBook, "Updated book should not be null");
    assertEquals(updatedTitle, updatedBook.getTitle(), "Book title should be updated");
  }

  @Test
  void testDeleteBook(){
    Book createdBook = saveAndTrack(testBook);
    bookDAO.delete(createdBook);
    Book deletedBook = bookDAO.findById(createdBook.getBookId());
    assertNull(deletedBook, "Deleted book should be null");
  }

  @Test
  void testFindAvailableBooks(){
    saveAndTrack(new Book("Available Book 1", "Author 1", "1234567890", 2022, 10));
    saveAndTrack(new Book("Available Book 2", "Author 2", "0987654321", 2021, 5));
    saveAndTrack(new Book("Unavailable Book", "Author 3", "1122334455", 2020, 0));

    List<Book> availableBooks = bookDAO.findAvailableBooks();
    assertNotNull(availableBooks, "Available books list should not be null");
    assertTrue(availableBooks.size() >= 2, "Should find at least 2 available books");
    for (Book book : availableBooks) {
      assertTrue(book.getCopiesAvailable() > 0, "All found books should have copies available");
    }
  }

  @Test
  void testFindAvailableBooksByTitle(){
    saveAndTrack(new Book("Available Book 1", "Author 1", "1234567890", 2022, 10));
    saveAndTrack(new Book("Available Book 2", "Author 2", "0987654321", 2021, 5));
    saveAndTrack(new Book("Available 3", "Author 2", "0987654320", 2021, 5));
    saveAndTrack(new Book("Unavailable Book", "Author 3", "1122334455", 2020, 0));

    List<Book> availableBooks = bookDAO.findAvailableBooks("title", "Book");
    assertNotNull(availableBooks, "Available books list should not be null");
    assertTrue(availableBooks.size() >= 2, "Should find at least 2 available books with 'Book' in title");
    for (Book book : availableBooks) {
      assertTrue(book.getCopiesAvailable() > 0, "All found books should have copies available");
    }
    assertTrue(availableBooks.stream().noneMatch(b -> "Available 3".equals(b.getTitle())), "'Available 3' should not be in the available books list");
  }
}