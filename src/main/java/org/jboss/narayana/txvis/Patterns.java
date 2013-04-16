package org.jboss.narayana.txvis;

import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 12:54
 */
public class Patterns {


    /*
     * Strings used for RegEx patterns
     */
    public static final String TX_ID = "[0-9a-fA-F]+:[0-9a-fA-F]+:-[0-9a-fA-F]+:[0-9a-fA-F]+:[0-9a-fA-F]+";

    public static final String THREAD_ID =  "\\(pool-\\d+-thread-\\d+\\)";

    /*
     * Pre-compiled patterns
     */
    public static final Pattern TX_BEGIN = Pattern.compile(
            "(" + THREAD_ID + ") BasicAction::Begin\\(\\) for action-id ("
            + TX_ID + ")"
            );

    public static final Pattern TX_ENLIST = null;





    // Suppress default constructor to prevent instantiation
    private Patterns() {}
}
