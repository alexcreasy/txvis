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

    public void create(String txID) {
        this.txList.put(txID, new Transaction(txID));
    }

    public Transaction get(String txID) {
        return this.txList.get(txID);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Transaction tx : this.txList.values())
            result.append(tx + "\n");

        return result.toString();
    }

    public void printAll() {
        System.out.println(this);
    }
}
