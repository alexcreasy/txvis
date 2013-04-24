package org.jboss.narayana.txvis.data;

import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 10:03
 */
public final class TransactionDAO {

    private final Map<String, Transaction> txList =
            Collections.synchronizedMap(new HashMap<String, Transaction>());

    public Transaction create(String txID) {
        if (this.txList.containsKey(txID))
            throw new IllegalStateException("Transaction with this ID already exists");

        Transaction tx = new Transaction(txID);
        this.txList.put(txID, tx);
        return tx;
    }

    public Transaction get(String txID) {
        return this.txList.get(txID);
    }

    public List<Transaction> getList() {
        return Collections.unmodifiableList(new LinkedList<Transaction>(this.txList.values()));
    }

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

    public void printAll() {
        System.out.println(this);
    }
}
