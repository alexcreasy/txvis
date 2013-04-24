package org.jboss.narayana.txvis.parser;

import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 12:54
 */
public class Patterns {


    /*
     * Strings used for RegEx patterns -?[0-9a-fA-F]+:{4}-?[0-9a-fA-F]+
     *
     * (?:-?[0-9a-fA-F^:]+:?){4}
     *
     * (:?-?[0-9a-fA-F^:]+:){4}-?[0-9a-fA-F]+
     *
     */
    public static final String TX_ID = "-?[0-9a-fA-F]+:-?[0-9a-fA-F]+:-?[0-9a-fA-F]+:-?[0-9a-fA-F]+:-?[0-9a-fA-F]+";

    public static final String THREAD_ID =  "pool-\\d+-thread-\\d+";

    /**
     * RegEx pattern for detecting creation of a transaction
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Transaction ID
     */
    public static final String BEGIN = "\\((" + THREAD_ID + ")\\)\\sBasicAction::Begin\\(\\)\\sfor\\saction-id\\s(" + TX_ID + ")";

    /**
     *
     * RegEx pattern for detecting a participant enlist
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Participant ID
     */
    public static final String ENLIST = "\\((" + THREAD_ID + ")\\)\\sTransactionImple.enlistResource\\s\\(\\s([^\\s\\)]+)\\s\\)";

    /**
     * RegEx pattern for detecting a successful commit.
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Transaction ID
     * (pool-1-thread-1) FileSystemStore.remove_committed(0:ffffac118223:61d0e901:515016c7:13,
     */
    public static final String COMMIT = "\\((" + THREAD_ID + ")\\)\\sFileSystemStore.remove_committed\\((" + TX_ID + "),";



    /*
     * Pre-compiled patterns
     */
    public static final Pattern TX_BEGIN = Pattern.compile(BEGIN);
    public static final Pattern TX_ENLIST = Pattern.compile(ENLIST);
    public static final Pattern TX_COMMIT = Pattern.compile(COMMIT);



    // Suppress default constructor to prevent instantiation
    private Patterns() {}
}
