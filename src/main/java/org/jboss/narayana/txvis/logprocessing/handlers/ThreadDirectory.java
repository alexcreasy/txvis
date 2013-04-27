package org.jboss.narayana.txvis.logprocessing.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 15:28
 */
enum ThreadDirectory {

    INSTANCE;

    private final Map<String, String> directory = new HashMap<String, String>();

    String registerTx(String threadID, String TxID) {
        return this.directory.put(threadID, TxID);
    }

    String lookupTxID(String threadID) {
        return this.directory.get(threadID);
    }
}
