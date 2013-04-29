package org.jboss.narayana.txvis.dataaccess;

import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 18:18
 */
public interface TransactionDAO {
    Transaction create(String txID);

    Transaction get(String txID);

    Collection<Transaction> getAll();

    int totalTx();
}
