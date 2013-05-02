package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.logprocessing.handlers.AbstractHandler;

import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 10:03
 */
public final class TransactionDAOInMemoryImpl implements TransactionDAO {

    private final Map<String, Transaction> txList = new HashMap<String, Transaction>();

    TransactionDAOInMemoryImpl() {}

    @Override
    public void create(String transactionID) throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionID))
            throw new IllegalArgumentException("Illegal transactionId");
        if (this.txList.containsKey(transactionID))
            throw new IllegalStateException("Transaction already exists with id=" + transactionID);

        this.txList.put(transactionID, new Transaction(transactionID));
    }

    @Override
    public Transaction get(String transactionID) {
        return this.txList.get(transactionID);
    }

    @Override
    public Collection<Transaction> getAll() {
        return Collections.unmodifiableCollection(this.txList.values());
    }

    @Override
    public int totalTx() {
        return this.txList.size();
    }

    @Override
    public void enlistParticipantResource(String transactionID, String resourceID)
            throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionID))
            throw new IllegalArgumentException("Illegal transactionId");

        get(transactionID).addParticipant(new ParticipantRecord(get(transactionID),
                DAOFactory.resourceInstance().get(resourceID)));
    }

    @Override
    public ParticipantRecord getEnlistedParticipantResource(String transactionID, String resourceID)
            throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionID))
            throw new IllegalArgumentException("Illegal transactionId");

        for (ParticipantRecord p : get(transactionID).getParticipants()) {
            if (p.getResource().getResourceID().equals(resourceID))
                return p;
        }
        return null;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Transaction tx : this.txList.values())
            result.append(tx).append("\n");

        return result.toString();
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.TX_ID);
    }

    @Override
    public void deconstruct() {
        this.txList.clear();
    }
}
