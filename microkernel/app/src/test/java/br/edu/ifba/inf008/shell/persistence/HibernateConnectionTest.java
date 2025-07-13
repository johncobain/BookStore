package br.edu.ifba.inf008.shell.persistence;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateConnectionTest {
    
  private EntityManagerFactory emf;
  private EntityManager em;
  
  @BeforeEach
  void setUp(){
    try{
      emf = Persistence.createEntityManagerFactory("bookstore-pu");
      em = emf.createEntityManager();
    } catch (Exception e) {
      fail("❌ Failed to create EntityManager: " + e.getMessage());
    }
  }

  @AfterEach
  void cleanup() {
    if (em != null && em.isOpen()) {
      em.close();
    }
    if (emf != null && emf.isOpen()) {
      emf.close();
    }
  }
  
  @Test
  void testDatabaseConnection(){
    assertNotNull(em, "EntityManager must be initialized");
    assertTrue(em.isOpen(), "EntityManager must be open");

    try {
      em.createNativeQuery("SELECT 1").getSingleResult();
      System.out.println("✅ Test query executed successfully!");
    } catch (Exception e) {
      fail("❌ Failed to execute test query: " + e.getMessage());
    }
  }

  @Test
  void testEntityMapping() {
    try {
      em.getMetamodel().getEntities().forEach(entityType -> {
        System.out.println("📋 Entity Mapped: " + entityType.getName());
      });

      assertTrue(!em.getMetamodel().getEntities().isEmpty(),
                "There must be mapped entities");
    } catch (Exception e) {
      fail("❌ Failed to map entities: " + e.getMessage());
    }
  }
}