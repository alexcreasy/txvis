package org.jboss.narayana.txvis;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 12:54
 */
public class Patterns {

    public static final String TX_ID = "\\d+:[0-9a-fA-F]+:-[0-9a-fA-F]+:[0-9a-fA-F]+:\\d+";

    public static final String CREATE_TX =
            "(\\(pool-\\d+-thread-\\d+\\)) BasicAction::Begin\\(\\) for action-id ("
            + TX_ID + ")";


    // Suppress default constructor to prevent instantiation
    private Patterns() {}
}
