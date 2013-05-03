package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.ConfigurationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 10:51
 */
public final class DAOFactory {

    private static DataAccessObject DAO;
    private static EntityManagerFactory emf;



    public static DataAccessObject getInstance() {
        return DAO;
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void initialize() {
        emf = Persistence.createEntityManagerFactory("org.jboss.narayana.txvis");
        DAO = new DataAccessObject();
    }

    public static void shutdown() {
        emf.close();
    }
}
