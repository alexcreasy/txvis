package org.jboss.narayana.txvis.logparsing.handlers;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.DataAccessObject;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:56
 */
public abstract class AbstractHandler implements Handler {

    protected static final Logger logger = Logger.getLogger(Handler.class.getName());

    /*
     * Constants can be used as building blocks
     * for regular expressions in implementation
     * classes.
     */

    /**
     * The back reference group name used to retrieve the log4j
     * timestamp TIMESTAMP_PATTERN - retrieve the timestamp by
     * calling <code>matcher.group(TIMESTAMP_GROUPNAME)</code>
     */
    public static final String TIMESTAMP_GROUPNAME = "TIMESTAMP";

    /**
     *
     */
    public static final String TIMESTAMP_PATTEN =
            "^(?<" + TIMESTAMP_GROUPNAME + ">\\d{2}:\\d{2}:\\d{2},\\d{3})";

    /**
     *
     */
    public static final String TX_ID_GROUPNAME = "TXID";

    /**
     *
     */
    public static final String TX_ID_PATTERN =
            "(?<" + TX_ID_GROUPNAME + ">(?:-?[0-9a-f^:]+:){4}-?[0-9a-f]+)";
    /**
     *
     */
    public static final String XA_RESOURCE_ID_GROUPNAME = "XARESOURCEID";

    /**
     *
     */
    public static final String XA_RESOURCE_ID_PATTERN =
            "(?<" + XA_RESOURCE_ID_GROUPNAME + ">(?:\\w+\\.)+\\w+@[0-9a-f]+)";






    private final Pattern pattern;

    protected DataAccessObject dao;

    public AbstractHandler(String regex) throws PatternSyntaxException {
        this.pattern = Pattern.compile(regex);
    }

    public AbstractHandler(String regex, int flags)
            throws PatternSyntaxException, IllegalArgumentException {
        this.pattern = Pattern.compile(regex, flags);
    }

    @Override
    public final Pattern getPattern() {
        return this.pattern;
    }

    public void injectDAO(DataAccessObject dao) throws NullPointerException {
        if (dao == null)
            throw new NullPointerException("instance of DataAccessObject required");
        this.dao = dao;
    }


}
