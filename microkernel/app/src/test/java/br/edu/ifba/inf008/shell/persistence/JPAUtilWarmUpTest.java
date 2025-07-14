package br.edu.ifba.inf008.shell.persistence;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class JPAUtilWarmUpTest {

  @Test
  public void testWarmUpInitializesConnectionSuccessfully() {
    assertDoesNotThrow(() -> {
      JPAUtil.warmUp();
    }, "JPAUtil.warmUp() should not throw any exceptions");
    
    assertTrue(JPAUtil.isFactoryOpen(), "EntityManagerFactory should be open after warmUp()");
    
    assertDoesNotThrow(() -> {
      try (var em = JPAUtil.getEntityManager()) {
        assertNotNull(em, "EntityManager should not be null after warmUp()");
        assertTrue(em.isOpen(), "EntityManager should be open");
      }
    }, "Getting EntityManager after warmUp() should work without exceptions");
  }
  
  @Test
  public void testWarmUpCanBeCalledMultipleTimes() {
    assertDoesNotThrow(() -> JPAUtil.warmUp());
    
    assertDoesNotThrow(() -> {
      JPAUtil.warmUp();
      JPAUtil.warmUp();
    }, "Multiple calls to warmUp() should be safe");
    
    assertTrue(JPAUtil.isFactoryOpen(), "EntityManagerFactory should remain open");
  }
}
