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
    public static final String TX_ID = "[0-9a-fA-F\\-]+:[0-9a-fA-F\\-]+:[0-9a-fA-F\\-]+:[0-9a-fA-F\\-]+:[0-9a-fA-F\\-]+";

    public static final String THREAD_ID =  "\\(pool-\\d+-thread-\\d+\\)";

    /**
     * RegEx pattern for detecting creation of a transaction
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Transaction ID
     */
    public static final String BEGIN = "(" + THREAD_ID + ")\\sBasicAction::Begin\\(\\)\\sfor\\saction-id\\s(" + TX_ID + ")";

    /**
     *
     * RegEx pattern for detecting a participant enlist
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Participant ID
     */
    public static final String ENLIST = "(" + THREAD_ID + ")\\sTransactionImple.enlistResource\\s\\(\\s([^\\s\\)]+)\\s\\)";



    /*
     * Pre-compiled patterns
     */
    public static final Pattern TX_BEGIN = Pattern.compile(BEGIN);
    public static final Pattern TX_ENLIST = Pattern.compile(ENLIST);





    // Suppress default constructor to prevent instantiation
    private Patterns() {}
}
