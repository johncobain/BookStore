package br.edu.ifba.inf008.plugins.report.persistence;


public class JPAUtil {

    public static Object getEntityManager() {
        return br.edu.ifba.inf008.shell.persistence.JPAUtil.getEntityManagerAsObject();
    }
    
    public static void closeFactory() {
        br.edu.ifba.inf008.shell.persistence.JPAUtil.closeFactory();
    }
}
