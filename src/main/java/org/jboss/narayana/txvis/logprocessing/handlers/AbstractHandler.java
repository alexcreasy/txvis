package org.jboss.narayana.txvis.logprocessing.handlers;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:56
 */
public abstract class AbstractHandler implements Handler {

    public static final String TX_ID = "(?:-?[0-9a-fA-F^:]+:){4}-?[0-9a-fA-F]+";

    private Pattern pattern;

    public AbstractHandler(String regex) throws PatternSyntaxException {
        this.pattern = Pattern.compile(regex);
    }

    public AbstractHandler(String regex, int flags)
            throws PatternSyntaxException, IllegalArgumentException {
        this.pattern = Pattern.compile(regex, flags);
    }

    // Suppress default constructor
    private AbstractHandler(){}

    @Override
    public final Pattern getPattern() {
        return this.pattern;
    }
}
