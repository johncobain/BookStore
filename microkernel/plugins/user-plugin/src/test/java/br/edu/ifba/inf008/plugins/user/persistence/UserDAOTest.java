package br.edu.ifba.inf008.plugins.user.persistence;

import java.time.LocalDate;
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
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import jakarta.persistence.EntityManager;
 
public class UserDAOTest {
  private UserDAO userDAO;
  private User testUser;
  private List<User> createdUsers;
  private EntityManager em;

  private User saveAndTrack(User user){
    userDAO.save(user);
    if (user.getUserId() == null) {
      throw new RuntimeException("User was not properly saved - no ID assigned");
    }
    createdUsers.add(user);
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
    
    if (returnDate == null) {
        Book managedBook = em.find(Book.class, book.getBookId());
        managedBook.setCopiesAvailable(managedBook.getCopiesAvailable() - 1);
        em.merge(managedBook);
    }
    
    em.persist(loan);
    em.getTransaction().commit();
    return loan;
  }

  @BeforeEach
  void setUp(){
    userDAO = new UserDAO();
    createdUsers = new ArrayList<>();
    em = TestJPAUtil.getEntityManager();

    em.getTransaction().begin();
    try {
        em.createQuery("DELETE FROM Loan").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
    } catch (Exception e) {
    }
    em.getTransaction().commit();

    String uniqueEmail = "test_" + System.currentTimeMillis() + "@test.com";
    testUser = new User("Test User", uniqueEmail);
  }

  @AfterEach
  void cleanup(){
    if (createdUsers != null) {
      try (EntityManager cleanupEm = TestJPAUtil.getEntityManager()) {
        cleanupEm.getTransaction().begin();
        for (User user : createdUsers) {
          try {
            if (user.getUserId() != null) {
              User managed = cleanupEm.find(User.class, user.getUserId());
              if (managed != null) {
                cleanupEm.remove(managed);
              }
            }
          } catch (Exception e) {
            System.err.println("Error cleaning user: " + user.getEmail() + " - " + e.getMessage());
          }
        }
        cleanupEm.getTransaction().commit();
      } catch (Exception e) {
      }
      createdUsers.clear();
    }
    if (em != null && em.isOpen()) em.close();
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
      System.out.println("✅ User plugin using unified H2 test database: " + url);
    }
  }

  @Test
  void testSaveUser() {
    User createdUser = saveAndTrack(testUser);
    assertNotNull(createdUser.getUserId(), "User ID should not be null");
  }

  @Test
  void testFindUserById(){
    User createdUser = saveAndTrack(testUser);

    User foundUser = userDAO.findById(createdUser.getUserId());

    assertNotNull(foundUser, "Found user should not be null");
    assertEquals("Test User", foundUser.getName(), "User name should match");
  }

  @Test
  void testFindUserByName(){
    User createdUser = saveAndTrack(testUser);

    List<User> foundUsers = userDAO.findAll("name", createdUser.getName());

    assertNotNull(foundUsers, "Found users list should not be null");
    assertFalse(foundUsers.isEmpty(), "Found users list should not be empty");
    assertTrue(foundUsers.size() >= 1, "Should find at least 1 user");
    
    User foundUser = foundUsers.get(0);
    assertEquals(testUser.getName(), foundUser.getName(), "User name should match");
  }

  @Test
  void testFindUserByEmail(){
    User createdUser = saveAndTrack(testUser);

    List<User> foundUsers = userDAO.findAll("email", createdUser.getEmail());

    assertNotNull(foundUsers, "Found users list should not be null");
    assertFalse(foundUsers.isEmpty(), "Found users list should not be empty");
    assertTrue(foundUsers.size() >= 1, "Should find at least 1 user");
    
    User foundUser = foundUsers.get(0);
    assertEquals(testUser.getEmail(), foundUser.getEmail(), "User email should match");
  }

  @Test
  void testFindUserByNamePartial(){
    saveAndTrack(new User("João Silva", "joao_" + System.currentTimeMillis() + "@test.com"));
    saveAndTrack(new User("João Santos", "joao2_" + System.currentTimeMillis() + "@test.com"));

    List<User> foundByJoao = userDAO.findAll("name", "João");
    assertNotNull(foundByJoao, "Found users list should not be null");
    assertTrue(foundByJoao.size() >= 2, "Should find at least 2 users with 'João'");
  }

  @Test
  void testFindUserByEmailPartial(){
    long timestamp = System.currentTimeMillis();
    saveAndTrack(new User("User 1", "admin_" + timestamp + "@company.com"));
    saveAndTrack(new User("User 2", "admin2_" + timestamp + "@company.com"));

    List<User> foundByAdmin = userDAO.findAll("email", "admin");
    assertNotNull(foundByAdmin, "Found users list should not be null");
    assertTrue(foundByAdmin.size() >= 2, "Should find at least 2 users with 'admin' in email");
  }

  @Test
  void testFindAllUsers(){
    User user1 = saveAndTrack(new User("Test User 1", "test1_" + System.currentTimeMillis() + "@test.com"));
    User user2 = saveAndTrack(new User("Test User 2", "test2_" + System.currentTimeMillis() + "@test.com"));
    User user3 = saveAndTrack(new User("Test User 3", "test3_" + System.currentTimeMillis() + "@test.com"));

    List<User> users = userDAO.findAll();

    assertNotNull(users, "User list should not be null");
    assertTrue(users.size() >= 3, "There should be at least 3 users in the list");  
    assertTrue(users.contains(user1), "User list should contain user1");
    assertTrue(users.contains(user2), "User list should contain user2");
    assertTrue(users.contains(user3), "User list should contain user3");
  }

  @Test
  void testUpdateUser(){
    User createdUser = saveAndTrack(testUser);
    String updatedName = "Updated User Name";
    createdUser.setName(updatedName);

    userDAO.update(createdUser);

    User updatedUser = userDAO.findById(createdUser.getUserId());
    assertNotNull(updatedUser, "Updated user should not be null");
    assertEquals(updatedName, updatedUser.getName(), "User name should be updated");
  }

  @Test
  void testDeleteUser(){
    User createdUser = saveAndTrack(testUser);
    userDAO.delete(createdUser);
    User deletedUser = userDAO.findById(createdUser.getUserId());
    assertNull(deletedUser, "Deleted user should be null");
  }

   @Test
  void testDeleteUserWithActiveLoans(){
    User createdUser = saveAndTrack(testUser);
    Book book = createBook("Test Book", "Test Author", "123456789", 2022, 2);
    createLoan(createdUser, book, LocalDate.now(), null);

    Book managedBook = em.find(Book.class, book.getBookId());
    assertEquals(1, managedBook.getCopiesAvailable());

    em.clear();

    userDAO.delete(createdUser);

    try (EntityManager freshEm = TestJPAUtil.getEntityManager()) {
      Book afterDeleteBook = freshEm.find(Book.class, book.getBookId());
      assertEquals(2, afterDeleteBook.getCopiesAvailable());
    }

    User deletedUser = userDAO.findById(createdUser.getUserId());
    assertNull(deletedUser, "Deleted user should be null");
  }

  @Test
  void testHasLoans(){
    User createdUser = saveAndTrack(testUser);

    assertFalse(userDAO.hasLoans(createdUser));

    Book book = createBook("Test Book", "Test Author", "987654321", 2022, 1);
    createLoan(createdUser, book, LocalDate.now(), null);

    assertTrue(userDAO.hasLoans(createdUser));
  }
}