package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DataAccessObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 16:54
 */
public interface Handler {

    void handle(Matcher matcher, String line);
    void injectDAO(DataAccessObject dao) throws NullPointerException;
    Pattern getPattern();
}
