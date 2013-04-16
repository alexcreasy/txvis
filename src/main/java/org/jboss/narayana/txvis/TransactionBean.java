package org.jboss.narayana.txvis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 10:03
 */
public class TransactionBean {

    private static final Map<String, Transaction> txList = new HashMap<>();

    public Transaction create(String txID) {
        Transaction tx = new Transaction(txID);
        this.txList.put(txID, tx);
        return tx;
    }

    public Transaction get(String txID) {
        return this.txList.get(txID);
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
