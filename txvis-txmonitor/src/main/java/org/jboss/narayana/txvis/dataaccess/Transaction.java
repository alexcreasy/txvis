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

    private String transactionId;
    private Status status = Status.IN_FLIGHT;
    private List<Participant> participants = new LinkedList<Participant>();


    public Transaction() {}

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
    }

    @Column(unique=true)
    public String getTransactionId() {
        return this.transactionId;
    }

    private void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Participant> getParticipants() {
        return this.participants;
    }

    private void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tx ID: ").append(transactionId);

        for (Participant p : participants) {
            result.append("\n\t").append(p);
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
}