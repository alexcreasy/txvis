package org.jboss.narayana.txvis.dataaccess;

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
    public Transaction create(String txID) {
        if (this.txList.containsKey(txID))
            throw new IllegalStateException("Transaction already exists with id=" + txID);

        Transaction tx = new Transaction(txID);
        this.txList.put(txID, tx);
        return tx;
    }

    @Override
    public Transaction get(String txID) {
        return this.txList.get(txID);
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
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Transaction tx : this.txList.values())
            result.append(tx).append("\n");

        return result.toString();
    }
}
