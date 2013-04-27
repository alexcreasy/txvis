package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.Configuration;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 10:51
 */
public final class DAOFactory {

    private static TransactionDAO transactionDAO;
    private static ParticipantDAO participantDAO;

    public static TransactionDAO transaction() {
        if (transactionDAO == null)
            throw new IllegalStateException("DAOFactory has not been initialized");
        return transactionDAO;
    }

    public static ParticipantDAO participant() {
        if (participantDAO == null)
            throw new IllegalStateException("DAOFactory has not been initialized");
        return participantDAO;
    }

    public static void initialize() {
        try {
            transactionDAO = (TransactionDAO) Class.forName(Configuration.TRANSACTION_DAO_IMPLEMENTATION_CLASS).newInstance();
            participantDAO = (ParticipantDAO) Class.forName(Configuration.PARTICIPANT_DAO_IMPLEMENTATION_CLASS).newInstance();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Unable to initialize DAO layer", t);
        }
    }
}
