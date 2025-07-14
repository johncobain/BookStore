package br.edu.ifba.inf008.shell.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.edu.ifba.inf008.interfaces.IDAO;
import jakarta.persistence.EntityManager;

public abstract class BaseDAO<T, ID> implements IDAO<T, ID> {
  protected EntityManager getEntityManager(){
    if (isTestEnvironment()) {
      try {
        Class<?> testJPAUtilClass = Class.forName("br.edu.ifba.inf008.plugins.user.persistence.TestJPAUtil");

        java.lang.reflect.Method getEntityManagerMethod = testJPAUtilClass.getMethod("getEntityManager");

        return (EntityManager) getEntityManagerMethod.invoke(null);
      } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        return JPAUtil.getEntityManager();
      }
    }
    return JPAUtil.getEntityManager();
  }
  
  private boolean isTestEnvironment() {
    String stackTrace = java.util.Arrays.toString(Thread.currentThread().getStackTrace());
    boolean isTest = stackTrace.contains("Test") || 
                     System.getProperty("maven.test.skip") != null ||
                     System.getProperty("surefire.test.class.path") != null;
    
    try {
      String resourcePath = getClass().getClassLoader().getResource("META-INF/persistence.xml").toString();
      isTest = isTest || resourcePath.contains("test-classes");
    } catch (Exception e) {
      // Se não conseguir acessar o resource, assume que não é teste
    }
    
    return isTest;
  }

  @Override
  public void save(T entity) {
    try (EntityManager em = getEntityManager()) {
      try{
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
      } catch (Exception e) {
        em.getTransaction().rollback();
        throw e;
      } finally {
        em.close();
      }
    }
  }

  @Override
  public T findById(ID id) {
    try (EntityManager em = getEntityManager()) {
      try {
        Class<T> entityClass = getEntityClass();
        return em.find(entityClass, id);
      } finally {
        em.close();
      }
    }
  }

  @Override
  public List<T> findAll() {
    try (EntityManager em = getEntityManager()) {
      try {
        Class<T> entityClass = getEntityClass();
        String lpql = "SELECT e FROM " + entityClass.getSimpleName() + " e"; 
        return em.createQuery(lpql, entityClass).getResultList();
      } finally {
        em.close();
      }
    }
  }

  @Override
  public void update(T entity) {
    try (EntityManager em = getEntityManager()) {
      try {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
      } catch (Exception e) {
        em.getTransaction().rollback();
        throw e;
      } finally {
        em.close();
      }
    }
  }

  @Override
  public void delete(T entity) {
    try (EntityManager em = getEntityManager()) {
      try {
        em.getTransaction().begin();
        T managedEntity = em.merge(entity);
        em.remove(managedEntity);
        em.getTransaction().commit();
      } catch (Exception e) {
        em.getTransaction().rollback();
        throw e;
      } finally {
        em.close();
      }
    }
  }

  protected abstract Class<T> getEntityClass();
}
