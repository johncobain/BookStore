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

  public List<Book> findByTitle(String titleSubstring){
    try (EntityManager em = getEntityManager()) {
      try {
        List<Book> results = em.createQuery(
          "SELECT b FROM Book b WHERE b.title LIKE :title", Book.class)
          .setParameter("title", "%" + titleSubstring + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      }
    }
  }

  public List<Book> findByAuthor(String authorSubstring){
    try (EntityManager em = getEntityManager()) {
      try {
        List<Book> results = em.createQuery(
          "SELECT b FROM Book b WHERE b.author LIKE :author", Book.class)
          .setParameter("author", "%" + authorSubstring + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      }
    }
  }

  public List<Book> findByIsbn(String isbnSubstring){
    try (EntityManager em = getEntityManager()) {
      try {
        List<Book> results = em.createQuery(
          "SELECT b FROM Book b WHERE b.isbn LIKE :isbn", Book.class)
          .setParameter("isbn", "%" + isbnSubstring + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      }
    }
  }
}
