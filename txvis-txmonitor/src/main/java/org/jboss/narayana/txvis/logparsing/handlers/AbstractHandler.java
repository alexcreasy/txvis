package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.persistence.DataAccessObject;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:56
 */
public abstract class AbstractHandler implements Handler {

    public static final String TX_ID = "(?:-?[0-9a-f^:]+:){4}-?[0-9a-f]+";
    public static final String XA_RESOURCE_ID = "(?:\\w+\\.)+\\w+@[0-9a-f]+";

    private Pattern pattern;

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

    @Override
    public void injectDAO(DataAccessObject dao) throws NullPointerException {
        if (dao == null)
            throw new NullPointerException("instance of DataAccessObject required");
        this.dao = dao;
    }
}
