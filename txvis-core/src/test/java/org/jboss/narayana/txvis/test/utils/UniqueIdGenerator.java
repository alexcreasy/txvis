package org.jboss.narayana.txvis.test.utils;

import java.util.Random;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 02/05/2013
 * Time: 13:13
 */
public class UniqueIdGenerator {

    private static final String HEX_CHARS = "0123456789abcdef";

    private final String txIdBase;
    private int txCounter = 0;

    private static final String RESOURCE_ID_BASE = "org.jboss.narayana.txvis.test@8aff";
    private int resourceCounter = 0;


    public UniqueIdGenerator() {
        final StringBuilder sb = new StringBuilder("0:ffff05974e0a:");

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++)
                sb.append(HEX_CHARS.charAt(new Random().nextInt(16)));
            sb.append(":");
        }
        this.txIdBase = sb.toString();

    }

    public String getUniqueTxId() {
        return txIdBase + txCounter++;
    }

    public String getUniqueResourceId() {
        return RESOURCE_ID_BASE + resourceCounter++;
    }
}
