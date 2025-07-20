package br.edu.ifba.inf008.plugins.book.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.edu.ifba.inf008.shell.model.Book;
import br.edu.ifba.inf008.shell.persistence.BaseDAO;
import jakarta.persistence.EntityManager;

public class BookDAO extends BaseDAO<Book, Integer> {
  
  @Override
  protected Class<Book> getEntityClass() {
    return Book.class;
  }

  @Override
  protected EntityManager getEntityManager() {
    if (isTestEnvironment()) {
      try {
        Class<?> testJPAUtilClass = Class.forName("br.edu.ifba.inf008.plugins.book.persistence.TestJPAUtil");
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

  public List<Book> findAll(String searchField, String fieldSubString) {
    try (EntityManager em = getEntityManager()) {
      try {
        List<Book> results = em.createQuery(
          "SELECT b FROM Book b WHERE b." + searchField + " LIKE :field", Book.class)
          .setParameter("field", "%" + fieldSubString + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      }
    }
  }

  public List<Book> findAvailableBooks(){
    try (EntityManager em = getEntityManager()) {
      try{
        List<Book> results = em.createQuery(
          "SELECT b FROM Book b WHERE b.copiesAvailable > 0", Book.class
        )
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      } 
    }
  }

  public List<Book> findAvailableBooks(String searchField, String fieldSubString) {
    try (EntityManager em = getEntityManager()) {
      try {
        List<Book> results = em.createQuery(
          "SELECT b FROM Book b WHERE b.copiesAvailable > 0 AND b." + searchField + " LIKE :field", Book.class
        )
          .setParameter("field", "%" + fieldSubString + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      } 
    }
  }
}
