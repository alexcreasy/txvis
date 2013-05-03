package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.ConfigurationManager;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 10:51
 */
public final class DAOFactory {

    private static DataAccessObject DAO;

    public static DataAccessObject getInstance() {
        return DAO;
    }

    public static void initialize() {
        DAO = new DataAccessObject();
    }

    public static void shutdown() {

    }
}
