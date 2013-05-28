package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/05/2013
 * Time: 16:13
 */
@Named
public class TransactionModel implements Serializable {

    @Inject
    private DataAccessObject dao;

    private Collection<Transaction> allTransactions;

    @PostConstruct
    public void setup() {
        allTransactions = dao.retrieveAll();
    }

    public Collection<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public Status[] getStatuses() {
        return Status.values();
    }

}
