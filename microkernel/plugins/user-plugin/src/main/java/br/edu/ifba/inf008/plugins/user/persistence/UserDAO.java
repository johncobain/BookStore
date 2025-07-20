package br.edu.ifba.inf008.plugins.user.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
}
