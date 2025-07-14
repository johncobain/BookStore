package br.edu.ifba.inf008.plugins.user.persistence;

import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.shell.persistence.BaseDAO;
import jakarta.persistence.EntityManager;

public class UserDAO extends BaseDAO<User, Integer> {
  
  @Override
  protected Class<User> getEntityClass() {
    return User.class;
  }

  public User findByName(String nameSubstring){
    try (EntityManager em = getEntityManager()) {
      try {
        return em.createQuery(
          "SELECT u FROM User u WHERE u.name LIKE :name", User.class)
          .setParameter("name", "%" + nameSubstring + "%")
          .getSingleResult();
      } finally {
        em.close();
      }
    }
  }

  public User findByEmail(String emailSubstring){
    try (EntityManager em = getEntityManager()) {
      try {
        return em.createQuery(
          "SELECT u FROM User u WHERE u.email LIKE :email", User.class)
          .setParameter("email", "%" + emailSubstring + "%")
          .getSingleResult();
      } finally {
        em.close();
      }
    }
  }
}
