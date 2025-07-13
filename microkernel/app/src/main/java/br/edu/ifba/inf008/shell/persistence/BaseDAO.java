package br.edu.ifba.inf008.shell.persistence;

import java.util.List;

import br.edu.ifba.inf008.interfaces.IDAO;
import jakarta.persistence.EntityManager;

public abstract class BaseDAO<T, ID> implements IDAO<T, ID> {
  protected EntityManager getEntityManager(){
    return JPAUtil.getEntityManager();
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
