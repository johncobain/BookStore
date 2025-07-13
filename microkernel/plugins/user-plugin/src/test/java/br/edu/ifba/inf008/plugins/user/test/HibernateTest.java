package br.edu.ifba.inf008.plugins.user.test;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateTest {
    
    private static EntityManagerFactory factory;
    
    @Test
    public void testHibernateConnection() {
        try {
            System.out.println("🔄 Testando conexão Hibernate com configuração centralizada...");
            
            factory = Persistence.createEntityManagerFactory("bookstore-pu");
            System.out.println("✅ EntityManagerFactory criado com sucesso!");
            assertNotNull(factory, "EntityManagerFactory should not be null");
            
            try (EntityManager em = factory.createEntityManager()) {
                System.out.println("✅ EntityManager criado com sucesso!");
                assertNotNull(em, "EntityManager should not be null");
                
                em.getTransaction().begin();
                System.out.println("✅ Transação iniciada!");
                assertTrue(em.getTransaction().isActive(), "Transaction should be active");
                
                em.getTransaction().commit();
                System.out.println("✅ Transação commitada!");
                assertFalse(em.getTransaction().isActive(), "Transaction should be committed");
            }
            System.out.println("✅ EntityManager fechado!");
            
            System.out.println("🎉 HIBERNATE FUNCIONANDO PERFEITAMENTE!");
            
        } catch (Exception e) {
            System.out.println("❌ ERRO: " + e.getMessage());
            fail("Hibernate test failed: " + e.getMessage());
        }
    }
    
    @AfterAll
    public static void cleanup() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}