package org.jboss.narayana.txvis.logparsing.handlers;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.DataAccessObject;

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
     * calling <code>matcher.group(TIMESTAMP)</code>
     */
    public static final String TIMESTAMP = "TIMESTAMP";

    /**
     *
     */
    public static final String PATTERN_TIMESTAMP =
            "^(?<" + TIMESTAMP + ">\\d{2}:\\d{2}:\\d{2},\\d{3})";

    /**
     *
     */
    public static final String TXID = "TXID";

    /**
     *
     */
    public static final String PATTERN_TXID =
            "(?<" + TXID + ">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";
    /**
     *
     */
    public static final String RESOURCEID = "XARESOURCEID";

    /**
     *
     */
    public static final String PATTERN_RESOURCEID =
            "(?<" + RESOURCEID + ">(?:\\w+\\.)+\\w+@[0-9a-f]+)";


    public static final String BASE_REGEX =
            "(?<TIMESTAMP>\\d{1,2}:\\d{1,2}:\\d{1,2},\\d{1,3})\\s(?<LEVEL>TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\s\\[(?<LOGCLASS>[^\\]]+)\\]\\s\\((?<THREAD>[^\\)]+)\\)\\s";
     /*
      ***********************************************************
      */



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
            throw new NullPointerException("Method called with null parameter: dao");
        this.dao = dao;
    }
}