package org.jboss.narayana.txvis;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
public class Transaction {

    private static final Logger logger = Logger.getLogger(Transaction.class.getName());
    private String txId;

    public Transaction(String txId) throws IllegalArgumentException, NullPointerException {
        if (!txId.matches(Patterns.TX_ID))
            throw new IllegalArgumentException("Invalid transaction ID: " + txId);

        this.txId = txId;
    }

    @Override
    public String toString() {
        return "Tx ID: " + txId;
    }

}
