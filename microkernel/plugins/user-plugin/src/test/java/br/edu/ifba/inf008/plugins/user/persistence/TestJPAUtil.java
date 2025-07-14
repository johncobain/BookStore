package br.edu.ifba.inf008.plugins.user.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestJPAUtil {
    
    private static EntityManagerFactory entityManagerFactory;
    
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory("bookstore-test-pu");
        }
        return entityManagerFactory;
    }
    
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
    
    public static void clearTable(String tableName) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM " + tableName).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {

        }
    }
    
    public static void resetSequence(String tableName, String sequenceName) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            // Ignora erros
        }
    }
}
