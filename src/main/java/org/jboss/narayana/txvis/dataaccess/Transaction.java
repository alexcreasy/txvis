package org.jboss.narayana.txvis.dataaccess;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
@Entity
public final class Transaction implements Serializable {

    private static final long serialVersionUID = -189443407589350068L;

    private Long id;

    private String transactionID;
    private Status status = Status.IN_FLIGHT;
    private List<ParticipantRecord> participants = new LinkedList<ParticipantRecord>();


    public Transaction() {}

    public Transaction(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionID() {
        return this.transactionID;
    }

    private void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
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

    public int totalParticipants() {
        return participants.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tx ID: ").append(transactionID);

        for (ParticipantRecord p : participants) {
            result.append("\n\t").append(p.getResource());
        }
        return result.toString();
    }



    /*
     * Private getters / setters for JPA
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @OneToMany//(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    public List<ParticipantRecord> getParticipants() {
        return this.participants;
    }

    private void setParticipants(List<ParticipantRecord> participants) {
        this.participants = participants;
    }
}