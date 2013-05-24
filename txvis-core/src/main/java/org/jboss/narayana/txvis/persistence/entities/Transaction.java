package org.jboss.narayana.txvis.persistence.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
@Entity
public class Transaction implements Serializable {

    private Long id;
    private String transactionId;
    private Status status = Status.IN_FLIGHT;
    private Collection<Participant> participants = new HashSet<>();
    private Collection<Event> events = new LinkedList<>();

    protected Transaction() {}

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    public String getTransactionId() {
        return this.transactionId;
    }

    protected void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    public Collection<Participant> getParticipants() {
        return this.participants;
    }

    protected void setParticipants(Collection<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    public Collection<Event> getEvents() {
        return events;
    }

    protected void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
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
}