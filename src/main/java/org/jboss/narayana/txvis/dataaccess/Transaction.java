package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.logprocessing.handlers.AbstractHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
public final class Transaction {

    private final String txId;
    private Status status = Status.IN_FLIGHT;
    private final List<Resource> resources = new LinkedList<Resource>();

    Transaction(String txId) throws IllegalArgumentException, NullPointerException {
        if (!txId.matches(AbstractHandler.TX_ID))
            throw new IllegalArgumentException("Invalid transaction ID: " + txId);
        this.txId = txId;
    }

    public String getTxId() {
        return this.txId;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) throws NullPointerException {
        if (status == null)
            throw new NullPointerException();
        this.status = status;
    }

    public void addParticipant(Resource resource) throws NullPointerException {
        if (resource == null)
            throw new NullPointerException();
        this.resources.add(resource);
    }

    public Collection<Resource> getResources() {
        return Collections.unmodifiableCollection(resources);
    }

    public int totalParticipants() {
        return resources.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tx ID: ").append(txId);

        for (Resource p : resources) {
            result.append("\n\t").append(p);
        }
        return result.toString();
    }
}