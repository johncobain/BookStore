package br.edu.ifba.inf008.shell.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilitário centralizado para gerenciamento da persistência compartilhada.
 * Todos os plugins devem usar esta classe para obter EntityManager.
 */
public class JPAUtil {
    
  private static final EntityManagerFactory FACTORY = 
    Persistence.createEntityManagerFactory("bookstore-pu");

  static {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Closing database connections...");
      closeFactory();
    }));
  }
    
  public static EntityManager getEntityManager() {
    return FACTORY.createEntityManager();
  }

  public static Object getEntityManagerAsObject() {
    return FACTORY.createEntityManager();
  }
  public static void warmUp() {
    try (EntityManager em = getEntityManager()) {
      em.createNativeQuery("SELECT 1").getSingleResult();
    } catch (Exception e) {
      System.err.println("Warning: Database warm-up failed: " + e.getMessage());
    }
  }
    
  public static void closeFactory() {
    if (FACTORY != null && FACTORY.isOpen()) {
      try{
        FACTORY.close();
      } catch (Exception e){
        System.err.println("Error closing EntityManagerFactory: " + e.getMessage());
      }
    }
  }
    
  public static boolean isFactoryOpen() {
    return FACTORY != null && FACTORY.isOpen();
  }
}
