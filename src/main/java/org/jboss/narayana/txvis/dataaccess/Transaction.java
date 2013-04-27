package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.logprocessing.handlers.AbstractHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
public final class Transaction {

    private final String txId;
    private Status status;
    private final List<Participant> participants = new LinkedList<Participant>();

    public Transaction(String txId) throws IllegalArgumentException, NullPointerException {
        if (!txId.matches(AbstractHandler.TX_ID))
            throw new IllegalArgumentException("Invalid transaction ID: " + txId);

        this.txId = txId;
        this.status = Status.IN_FLIGHT;
    }

    public String getTxId() {
        return this.txId;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) throws NullPointerException {
        if (status == null)
            throw new NullPointerException("null param status");
        this.status = status;
    }

    public void addParticipant(Participant participant) throws NullPointerException {
        if (participant == null)
            throw new NullPointerException("null param participant");
        this.participants.add(participant);
    }

    public int totalParticipants() {
        return participants.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tx ID: ").append(txId);

        for (Participant p : participants) {
            result.append("\n\t").append(p);
        }
        return result.toString();
    }
}