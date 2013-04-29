package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.Configuration;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 10:51
 */
public final class DAOFactory {

    private static TransactionDAO transactionDAO;
    private static ResourceDAO resourceDAO;

    public static TransactionDAO transaction() {
        if (transactionDAO == null)
            throw new IllegalStateException("DAOFactory has not been initialized");
        return transactionDAO;
    }

    public static ResourceDAO participant() {
        if (resourceDAO == null)
            throw new IllegalStateException("DAOFactory has not been initialized");
        return resourceDAO;
    }

    public static void initialize() {
        try {
            transactionDAO = (TransactionDAO) Class.forName(Configuration.TRANSACTION_DAO_IMPLEMENTATION_CLASS).newInstance();
            resourceDAO = (ResourceDAO) Class.forName(Configuration.RESOURCE_DAO_IMPLEMENTATION_CLASS).newInstance();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Unable to initialize DAO layer", t);
        }
    }
}
