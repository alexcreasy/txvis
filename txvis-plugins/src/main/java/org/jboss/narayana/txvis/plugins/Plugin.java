package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.DataAccessObject;

import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 13:16
 */
public interface Plugin {

    long getPollInterval();

    void setup();

    void tearDown();

    Collection<Issue> getIssues();

    void findIssues();

    void injectDAO(DataAccessObject dao);
}
