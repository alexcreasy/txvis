package org.jboss.narayana.txvis.logparsing;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.HandlerService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:56
 */
public abstract class AbstractHandler implements Handler {

    /**
     *
     */
    public static final String TXUID = "TXUID";
    /**
     *
     */
    public static final String PATTERN_TXUID = "(?<"+TXUID+">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";


    private final Pattern pattern;
    protected HandlerService service;

    /**
     *
     * @param regex
     * @throws PatternSyntaxException
     */
    public AbstractHandler(String regex) throws PatternSyntaxException {
        this.pattern = Pattern.compile(regex);
    }

    /**
     *
     * @return
     */
    @Override
    public final Pattern getPattern() {
        return this.pattern;
    }

    /**
     *
     * @param service
     * @throws NullPointerException
     */
    final void injectService(HandlerService service) throws NullPointerException {
        if (service == null)
            throw new NullPointerException("Method called with null parameter: service");
        this.service = service;
    }
}