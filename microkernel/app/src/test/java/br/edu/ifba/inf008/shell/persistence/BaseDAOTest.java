package br.edu.ifba.inf008.shell.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import br.edu.ifba.inf008.shell.model.User;

public class BaseDAOTest {
  private static class TestUserDAO extends BaseDAO<User, Integer> {
    @Override
    protected Class<User> getEntityClass() {
      return User.class;
    }
  }

  @Test
  void testBaseDaoCreation() {
    TestUserDAO dao = new TestUserDAO();
    assertNotNull(dao);
    assertEquals(User.class, dao.getEntityClass());
  }
}
