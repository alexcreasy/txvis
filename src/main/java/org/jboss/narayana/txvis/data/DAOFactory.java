package org.jboss.narayana.txvis.data;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 24/04/2013
 * Time: 19:21
 */
public final class DAOFactory {

    private static ParticipantDAO participantDAO;
    private static TransactionDAO transactionDAO;

    public static ParticipantDAO participant() {
        if (participantDAO == null)
            participantDAO = new ParticipantDAO();
        return participantDAO;
    }

    public static TransactionDAO transaction() {
        if (transactionDAO == null)
            transactionDAO = new TransactionDAO();
        return transactionDAO;
    }

    private DAOFactory() {}
}
