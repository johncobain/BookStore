package br.edu.ifba.inf008.plugins.user.persistence;

import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.shell.persistence.BaseDAO;
import jakarta.persistence.EntityManager;

public class UserDAO extends BaseDAO<User, Integer> {
  
  @Override
  protected Class<User> getEntityClass() {
    return User.class;
  }

  public User findByEmail(String email){
    try (EntityManager em = getEntityManager()) {
      try {
        return em.createQuery(
          "SELECT u FROM User u WHERE u.email = :email", User.class)
          .setParameter("email", email)
          .getSingleResult();
      } finally {
        em.close();
      }
    }
  }
}
