package br.edu.ifba.inf008.shell.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class BaseDAOTest {

  @Test
  void testH2DatabaseConnection() {
    var emf = Persistence.createEntityManagerFactory("bookstore-test-pu");
    try (EntityManager em = emf.createEntityManager()) {
      assertNotNull(em);
      
      String url = em.getEntityManagerFactory().getProperties().get("jakarta.persistence.jdbc.url").toString();
      assertTrue(url.contains("h2:mem"), "Should use H2 in-memory database");
      assertTrue(url.contains("bookstore_test"), "Should use unified test database");
    } finally {
      emf.close();
    }
  }
}
