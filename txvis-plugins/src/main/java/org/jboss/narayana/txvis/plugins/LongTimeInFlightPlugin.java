package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/07/2013
 * Time: 19:53
 */
public class LongTimeInFlightPlugin implements Plugin {

    private Collection<Issue> issues = new LinkedList<>();

    private DataAccessObject dao;

    public static final int THRESHOLD = 1000;

    @Override
    public long getPollInterval() {
        return 0;
    }

    @Override
    public void setup() {

    }

    @Override
    public void tearDown() {
    }

    @Override
    public Collection<Issue> getIssues() {
        return Collections.unmodifiableCollection(issues);
    }

    @Override
    public void findIssues() {
        for (Transaction tx : dao.findAllWithStatus(Status.IN_FLIGHT)) {
           if (tx.getDuration() > THRESHOLD) {
               Issue issue = new Issue();
               issue.setCause(tx);
               issue.setTitle("Transaction in flight " + tx.getDuration() + "ms");
               issue.setBody("Transaction: " + tx.getTxuid() + " has been in flight longer than the specified threshold, " +
                       "of "+THRESHOLD+"ms this may indicate it is stuck");
               issues.add(issue);
           }
       }
    }

    @Override
    public void injectDAO(DataAccessObject dao) {
        this.dao = dao;
    }
}
