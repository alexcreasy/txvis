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


    String put(String key, String value) {
        return this.directory.put(key, value);
    }

    String get(String key) {
        return this.directory.get(key);
    }
}
