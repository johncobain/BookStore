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
    
  public static EntityManager getEntityManager() {
    return FACTORY.createEntityManager();
  }

  public static Object getEntityManagerAsObject() {
    return FACTORY.createEntityManager();
  }
    
  public static void closeFactory() {
    if (FACTORY != null) {
      FACTORY.close();
    }
  }
    
  public static boolean isFactoryOpen() {
    return FACTORY != null && FACTORY.isOpen();
  }
}
