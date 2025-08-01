package br.edu.ifba.inf008.plugins.user.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.edu.ifba.inf008.shell.model.Book;
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.shell.persistence.BaseDAO;
import jakarta.persistence.EntityManager;

public class UserDAO extends BaseDAO<User, Integer> {
  
  @Override
  protected Class<User> getEntityClass() {
    return User.class;
  }

  @Override
  protected EntityManager getEntityManager() {
    if (isTestEnvironment()) {
      try {
        Class<?> testJPAUtilClass = Class.forName("br.edu.ifba.inf008.plugins.user.persistence.TestJPAUtil");
        java.lang.reflect.Method getEntityManagerMethod = testJPAUtilClass.getMethod("getEntityManager");
        return (EntityManager) getEntityManagerMethod.invoke(null);
      } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        return super.getEntityManager();
      }
    }
    return super.getEntityManager();
  }

  private boolean isTestEnvironment() {
    String stackTrace = java.util.Arrays.toString(Thread.currentThread().getStackTrace());
    return stackTrace.contains("Test") || 
           System.getProperty("surefire.test.class.path") != null;
  }

  public List<User> findAll(String searchField, String fieldSubString) {
    try (EntityManager em = getEntityManager()) {
      try {
        List<User> results = em.createQuery(
          "SELECT u FROM User u WHERE u." + searchField + " LIKE :field", User.class)
          .setParameter("field", "%" + fieldSubString + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      }
    }
  }

  public boolean hasLoans(User user) {
    try (EntityManager em = getEntityManager()) {
      try {
          Long count = em.createQuery(
            "SELECT COUNT(l) FROM Loan l WHERE l.user.userId = :userId", Long.class)
            .setParameter("userId", user.getUserId())
            .getSingleResult();
          return count > 0;
      } finally {
        em.close();
      }
    }
  }

  @Override
  public void delete(User user) {
    try (EntityManager em = getEntityManager()) {
      try {
        em.getTransaction().begin();

        List<Loan> activeLoans = em.createQuery(
          "SELECT l FROM Loan l WHERE l.user.userId = :userId AND l.returnDate IS NULL", Loan.class)
          .setParameter("userId", user.getUserId())
          .getResultList();
        
        if (!activeLoans.isEmpty()) {
          for (Loan loan : activeLoans) {
            Book book = em.find(Book.class, loan.getBook().getBookId());
            if(book != null){
              book.setCopiesAvailable(book.getCopiesAvailable() + 1);
              em.merge(book);
            }
          }
        }

        em.createQuery("DELETE FROM Loan l WHERE l.user.userId = :userId")
          .setParameter("userId", user.getUserId())
          .executeUpdate();

        User managedUser = em.find(User.class, user.getUserId());
        if (managedUser != null) {
          em.remove(managedUser);
        }
        em.getTransaction().commit();
      } catch (Exception e) {
        if (em.getTransaction().isActive()) {
          em.getTransaction().rollback();
        }
        throw e;
      } finally {
        em.close();
      }
    }
  }
}
