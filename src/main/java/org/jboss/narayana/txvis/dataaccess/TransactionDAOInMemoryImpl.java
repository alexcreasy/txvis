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
    public Transaction create(String transactionId) throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionId");
        if (this.txList.containsKey(transactionId))
            throw new IllegalStateException("Transaction already exists with id=" + transactionId);

        Transaction tx = new Transaction(transactionId);
        this.txList.put(transactionId, tx);
        return tx;
    }

    @Override
    public Transaction get(String txId) {
        return this.txList.get(txId);
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
    public void enlistParticipantResource(String transactionId, String resourceId)
            throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionId");

        get(transactionId).addParticipant(new ParticipantRecord(get(transactionId),
                DAOFactory.resourceInstance().get(resourceId)));
    }

    @Override
    public ParticipantRecord getEnlistedParticipantResource(String transactionId, String resourceId)
            throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionId");

        for (ParticipantRecord p : get(transactionId).getEnlistedParticipants()) {
            if (p.getResource().getResourceID().equals(resourceId))
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
}
