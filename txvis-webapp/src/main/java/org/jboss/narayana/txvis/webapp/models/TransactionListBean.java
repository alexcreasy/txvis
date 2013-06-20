package org.jboss.narayana.txvis.webapp.models;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/05/2013
 * Time: 16:13
 */
@Named
@SessionScoped
public class TransactionListBean implements Serializable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    private GenericDAO dao;

    private Collection<Transaction> transactionsList;

    private Status filterByStatus;
    private long filterByDuration;


    public Collection<Transaction> getTransactionsList() {
        logger.info(MessageFormat.format(
                "TransactionListBean.getTransactionsList, filterByStatus={0}, filterByDuration={1}",
                filterByStatus, filterByDuration));

        filter();
        return transactionsList;
    }

    public void filter() {
        logger.trace("filter, filterByStatus=" + filterByStatus);
        transactionsList = filterByStatus == null
                ? dao.retrieveAll(Transaction.class)
                : dao.retrieveTransactionsWithStatus(filterByStatus);
    }

    public void setFilterByStatus(Status status) {
        logger.trace("setFilterByStatus, status=" + status);

        filterByStatus = status;
    }

    public Status getFilterByStatus() {
        logger.trace("getFilterByStatus, filterByStatus=" + filterByStatus);
        return filterByStatus;
    }

    public long getFilterByDuration() {
        return filterByDuration;
    }

    public void setFilterByDuration(long filterByDuration) {
        this.filterByDuration = filterByDuration;
    }

    public Status[] getStatuses() {
        return Status.values();
    }
}
