package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.ConfigurationManager;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 10:51
 */
public final class DAOFactory {

    private static TransactionDAO transactionDAO;
    private static ResourceDAO resourceDAO;

    public static TransactionDAO transactionInstance() {
        if (transactionDAO == null)
            throw new IllegalStateException("DAOFactory has not been initialized");
        return transactionDAO;
    }

    public static ResourceDAO resourceInstance() {
        if (resourceDAO == null)
            throw new IllegalStateException("DAOFactory has not been initialized");
        return resourceDAO;
    }

    public static void initialize() {
        try {
            transactionDAO = (TransactionDAO) Class.forName(
                    ConfigurationManager.INSTANCE.getTransactionDaoImplementationClass()).newInstance();
            resourceDAO = (ResourceDAO) Class.forName(
                    ConfigurationManager.INSTANCE.getResourceDaoImplementationClass()).newInstance();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Unable to initialize DAOFactory", t);
        }
    }

    public static void shutdown() {
        transactionDAO.deconstruct();
        transactionDAO = null;
        resourceDAO.deconstruct();
        resourceDAO = null;
    }
}
