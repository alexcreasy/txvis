package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/05/2013
 * Time: 16:13
 */
@Model
public class TransactionModel implements Serializable {

    @EJB
    private DataAccessObject dao;

    private Collection<Transaction> allTransactions;

    @PostConstruct
    public void setup() {
        allTransactions = dao.retrieveAll();
    }

    public Collection<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public Transaction getTransaction(String transactionId) {
        return dao.retrieve(transactionId);
    }

    private void setTransactions(Collection<Transaction> allTransactions) {
        this.allTransactions = allTransactions;
    }



}
