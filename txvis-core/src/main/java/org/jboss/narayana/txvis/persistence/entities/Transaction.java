package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
@Entity
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_FLIGHT;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKey(name="eventType")
    private Map<EventType, Event> events = new HashMap<>();


    protected Transaction() {}

    public Transaction(String transactionId) throws IllegalArgumentException {
        if (!transactionId.matches(AbstractHandler.TX_ID_PATTERN))
            throw new IllegalArgumentException(MessageFormat.format
                    ("Illegal transactionId: {0}", transactionId));

        this.transactionId = transactionId;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    //@OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //@Fetch(value = FetchMode.SUBSELECT)
    public Collection<ParticipantRecord> getParticipantRecords() {
        return this.participantRecords;
    }

    public void addParticipant(ParticipantRecord participantRecord) {
        this.participantRecords.add(participantRecord);
    }

//    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @MapKey(name="eventType")
    //@Fetch(value = FetchMode.SUBSELECT)
    public Map<EventType, Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        this.events.put(event.getEventType(), event);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Tx ID: ").append(transactionId);

        for (ParticipantRecord p : participantRecords) {
            result.append("\n\t").append(p);
        }
        return result.toString();
    }
}