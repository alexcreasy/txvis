package org.jboss.narayana.txvis.dataaccess;

import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 18:18
 */
public interface TransactionDAO {

    void create(String transactionID) throws IllegalArgumentException, NullPointerException;

    Transaction get(String transactionID);

    Collection<Transaction> getAll();

    void enlistParticipantResource(String transactionID, String resourceID)
            throws IllegalArgumentException, NullPointerException;

    ParticipantRecord getEnlistedParticipantResource(String transactionID, String resourceID)
            throws IllegalArgumentException, NullPointerException;

    int totalTx();

    void deconstruct();
}
