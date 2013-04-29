package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.logprocessing.handlers.AbstractHandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
public final class Transaction implements Serializable {

    private static final long serialVersionUID = -189443407589350068L;

    private String txId;
    private Status status = Status.IN_FLIGHT;
    private List<ParticipantRecord> participants = new LinkedList<ParticipantRecord>();

    Transaction(String txId) {
        this.txId = txId;
    }

    public String getTxId() {
        return this.txId;
    }

    private void setTxId(String txId) {
        this.txId = txId;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addParticipant(ParticipantRecord participant) {
        this.participants.add(participant);
    }

    public Collection<ParticipantRecord> getParticipants() {
        return Collections.unmodifiableCollection(participants);
    }

    private void setParticipants(List<ParticipantRecord> participants) {
        this.participants = participants;
    }

    public int totalParticipants() {
        return participants.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tx ID: ").append(txId);

        for (ParticipantRecord p : participants) {
            result.append("\n\t").append(p.getResource());
        }
        return result.toString();
    }
}