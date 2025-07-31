package br.edu.ifba.inf008.plugins.report.persistence;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;

import br.edu.ifba.inf008.shell.model.Loan;
import br.edu.ifba.inf008.shell.persistence.BaseDAO;
import jakarta.persistence.EntityManager;

public class ReportDAO extends BaseDAO<Loan, Integer>{
  private String getQuery(String searchType){
    String baseQuery = "SELECT l FROM Loan l JOIN l.user u JOIN l.book b";

    switch (searchType) {
      case "allTime" -> {
        return baseQuery;
      }
      case "before" -> {
        return baseQuery + " WHERE l.loanDate <= :initialDate";
      }
      case "after" -> {
        return baseQuery + " WHERE l.loanDate >= :initialDate";
      }
      case "onDate" -> {
        return baseQuery + " WHERE l.loanDate = :initialDate";
      }
      case "between" -> {
        return baseQuery + " WHERE l.loanDate BETWEEN :initialDate AND :finalDate";
      }
      default -> throw new IllegalArgumentException("Invalid search type: " + searchType);
    }
  }

  private String addStatusFilter(String baseQuery, boolean isActive) {
    if (baseQuery.contains("WHERE")) {
        return baseQuery + (isActive ? " AND l.returnDate IS NULL" : " AND l.returnDate IS NOT NULL");
    } else {
        return baseQuery + (isActive ? " WHERE l.returnDate IS NULL" : " WHERE l.returnDate IS NOT NULL");
    }
}

  @Override
  protected Class<Loan> getEntityClass() {
    return Loan.class;
  }

  @Override  
  protected EntityManager getEntityManager() {
    if (isTestEnvironment()) {
      try {
        Class<?> testJPAUtilClass = Class.forName("br.edu.ifba.inf008.plugins.report.persistence.TestJPAUtil");
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

  @Override
  public List<Loan> findAll(){
    try (EntityManager em = getEntityManager()){
      try{
        return em.createQuery(
          getQuery("allTime"), Loan.class)
          .getResultList();
      } finally {
        em.close();
      }
    }
  }

  public List<Loan> findByInitialDate(LocalDate initialDate, String searchType){
    try (EntityManager em = getEntityManager()){
      try{
        return em.createQuery(
          getQuery(searchType), Loan.class)
          .setParameter("initialDate", initialDate)
          .getResultList();
      } finally {
        em.close();
      }
    }
  }

  public List<Loan> findByInitialAndFinalDate(LocalDate initialDate, LocalDate finalDate){
    try (EntityManager em = getEntityManager()){
      try{
        return em.createQuery(
          getQuery("between"), Loan.class)
          .setParameter("initialDate", initialDate)
          .setParameter("finalDate", finalDate)
          .getResultList();
      } finally {
        em.close();
      }
    }
  }

  public List<Loan> findByStatus(boolean isActive) {
    try (EntityManager em = getEntityManager()) {
      try{
        return em.createQuery(
          addStatusFilter(getQuery("allTime"), isActive), Loan.class
        ).getResultList();
      } finally {
        em.close();
      }
    }
  }
  
  public List<Loan> findByStatusAndInitialDate(boolean isActive, LocalDate initialDate, String searchType) {
    try (EntityManager em = getEntityManager()) {
      try{
        return em.createQuery(
          addStatusFilter(getQuery("allTime"), isActive), Loan.class
          ).setParameter("initialDate", initialDate)
          .getResultList();
      } finally {
        em.close();
      }
    }
  }
  
  public List<Loan> findByStatusAndInitialAndFinalDate(boolean isActive, LocalDate initialDate, LocalDate finalDate) {
    try (EntityManager em = getEntityManager()) {
      try{
        return em.createQuery(
          addStatusFilter(getQuery("between"), isActive), Loan.class
          ).setParameter("initialDate", initialDate)
          .setParameter("finalDate", finalDate)
          .getResultList();
      } finally {
        em.close();
      }
    }
  }
}
