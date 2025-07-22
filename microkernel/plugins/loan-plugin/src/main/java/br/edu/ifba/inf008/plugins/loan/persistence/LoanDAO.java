package br.edu.ifba.inf008.plugins.loan.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import br.edu.ifba.inf008.shell.model.Book;
import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.model.User;
import br.edu.ifba.inf008.shell.persistence.BaseDAO;
import jakarta.persistence.EntityManager;

public class LoanDAO extends BaseDAO<Loan, Integer> {
  
  @Override
  protected Class<Loan> getEntityClass() {
    return Loan.class;
  }

  @Override  
  protected EntityManager getEntityManager() {
    if (isTestEnvironment()) {
      try {
        Class<?> testJPAUtilClass = Class.forName("br.edu.ifba.inf008.plugins.loan.persistence.TestJPAUtil");
        java.lang.reflect.Method getEntityManagerMethod = testJPAUtilClass.getMethod("getEntityManager");
        return (EntityManager) getEntityManagerMethod.invoke(null);
      } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        return super.getEntityManager();
      }
    }
    return super.getEntityManager();
  }

  private boolean isTestEnvironment() {
    String stackTrace = java.util.Arrays.toString(Thread.currentThread().getStackTrace());
    return stackTrace.contains("Test") || 
           System.getProperty("surefire.test.class.path") != null;
  }

  public List<Loan> findAll(String fieldSubString){
    try (EntityManager em = getEntityManager()){
      try{
        List<Loan> results = em.createQuery(
          "SELECT l FROM Loan l JOIN l.user u JOIN l.book b WHERE u.name LIKE :field OR u.email LIKE :field", Loan.class)
          .setParameter("field", "%" + fieldSubString + "%")
          .getResultList();
        return results.isEmpty() ? null : results;
      } finally {
        em.close();
      }
    }
  }

  public List<Loan> findActiveLoans() {
    try (EntityManager em = getEntityManager()) {
      return em.createQuery(
        "SELECT l FROM Loan l WHERE l.returnDate IS NULL", Loan.class
      ).getResultList();
    }
  }

  public List<Loan> findActiveLoans(String fieldSubString) {
    try (EntityManager em = getEntityManager()) {
      return em.createQuery(
        "SELECT l FROM Loan l JOIN l.user u WHERE l.returnDate IS NULL AND (u.name LIKE :field OR u.email LIKE :field)", Loan.class)
        .setParameter("field", "%" + fieldSubString + "%")
        .getResultList();
    }
  }

  public List<User> listUsers() {
    try (EntityManager em = getEntityManager()) {
      return em.createQuery(
        "SELECT u FROM User u", User.class
      ).getResultList();
    }
  }

  public List<Book> listBooks() {
    try (EntityManager em = getEntityManager()) {
      return em.createQuery(
        "SELECT b FROM Book b", Book.class
      ).getResultList();
    }
  }

  @Override
  public void save(Loan loan){
    try (EntityManager em = getEntityManager()) {
      try{
        em.getTransaction().begin();
        
        Book book = em.find(Book.class, loan.getBook().getBookId());
        if (book.getCopiesAvailable() > 0){
          book.setCopiesAvailable(book.getCopiesAvailable() - 1);
          em.persist(loan);
          em.merge(book);
          em.getTransaction().commit();
        }else{
          em.getTransaction().rollback();
          throw new IllegalStateException("No copies available for this book");
        }
      } finally {
        em.close();
      }
    }
  }

  public void returnLoan(Loan loan) {
    try (EntityManager em = getEntityManager()) {
      try{
        em.getTransaction().begin();
        
        Loan managedLoan = em.find(Loan.class, loan.getLoanId());
        managedLoan.returnBook();

        Book book = em.find(Book.class, managedLoan.getBook().getBookId());
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);

        em.merge(managedLoan);
        em.merge(book);
        em.getTransaction().commit();
      } finally {
        em.close();
      }
    }
  }

  @Override
  public void update(Loan loan){
    try (EntityManager em = getEntityManager()) {
      try{
        em.getTransaction().begin();
        
        Loan managedLoan = em.find(Loan.class, loan.getLoanId());
        Book oldBook = em.find(Book.class, managedLoan.getBook().getBookId());
        Book newBook = em.find(Book.class, loan.getBook().getBookId());

        boolean bookChanged = !oldBook.getBookId().equals(newBook.getBookId());
        boolean wasReturned = managedLoan.getReturnDate() != null;
        boolean isReturned = loan.getReturnDate() != null;

        if (bookChanged) {
          if (!wasReturned && isReturned) {
            oldBook.setCopiesAvailable(oldBook.getCopiesAvailable() + 1);
            em.merge(oldBook);
          } else if (wasReturned && !isReturned) {
            if (newBook.getCopiesAvailable() > 0) {
              newBook.setCopiesAvailable(newBook.getCopiesAvailable() - 1);
              em.merge(newBook);
            } else {
              em.getTransaction().rollback();
              throw new IllegalStateException("No copies available for this book");
            }
          } else if (!wasReturned && !isReturned) {
            oldBook.setCopiesAvailable(oldBook.getCopiesAvailable() + 1);
            if (newBook.getCopiesAvailable() > 0) {
              newBook.setCopiesAvailable(newBook.getCopiesAvailable() - 1);
            } else {
              em.getTransaction().rollback();
              throw new IllegalStateException("No copies available for this book");
            }
            em.merge(oldBook);
            em.merge(newBook);
          }
        } else {
          if (!wasReturned && isReturned) {
            oldBook.setCopiesAvailable(oldBook.getCopiesAvailable() + 1);
            em.merge(oldBook);
          } else if (wasReturned && !isReturned) {
            if (oldBook.getCopiesAvailable() > 0) {
              oldBook.setCopiesAvailable(oldBook.getCopiesAvailable() - 1);
              em.merge(oldBook);
            } else {
              em.getTransaction().rollback();
              throw new IllegalStateException("No copies available for this book");
            }
          }
        }

        em.merge(loan);

        em.getTransaction().commit();
      } finally {
        em.close();
      }
    }
  }

  @Override
  public void delete(Loan loan) {
    try (EntityManager em = getEntityManager()) {
      try{
        em.getTransaction().begin();
        Loan managedLoan = em.find(Loan.class, loan.getLoanId());

        if (loan.getReturnDate() == null){
          Book book = em.find(Book.class, managedLoan.getBook().getBookId());
          book.setCopiesAvailable(book.getCopiesAvailable() + 1);
          em.merge(book);
        }

        em.remove(managedLoan);
        
        em.getTransaction().commit();
      } finally {
        em.close();
      }
    }
  }
}
