package org.jboss.narayana.txvis.persistence.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
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

    private boolean onePhase = false;

    private Long startTime;

    private Long endTime;

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Event> events = new LinkedList<>();

    protected Transaction() {}

    public Transaction(String transactionId) throws NullPointerException,
            IllegalArgumentException {

        if (!transactionId.matches(AbstractHandler.PATTERN_TXID))
            throw new IllegalArgumentException("Illegal transactionId: " + transactionId);

        this.transactionId = transactionId;
    }

    public Transaction(String transactionId, Timestamp timestamp) throws NullPointerException,
            IllegalArgumentException {

        if (!transactionId.matches(AbstractHandler.PATTERN_TXID))
            throw new IllegalArgumentException("Illegal transactionId: " + transactionId);

        this.transactionId = transactionId;
        this.startTime = timestamp.getTime();
        events.add(new Event(this, EventType.BEGIN, "N/A", timestamp));
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

    public void setStatus(Status status, Timestamp timestamp) {
        this.status = status;
        events.add(new Event(this, EventType.END, status.toString(), timestamp));
        this.endTime = timestamp.getTime();
    }

    public boolean isOnePhase() {
        return onePhase;
    }

    public void setOnePhase(boolean isOnePhase) {
        for (ParticipantRecord rec : participantRecords)
            rec.setVote(Vote.COMMIT);
        this.onePhase = isOnePhase;
    }

    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    public Timestamp getEndTime() {
        return endTime != null ? new Timestamp(endTime) :null;
    }

    public Collection<ParticipantRecord> getParticipantRecords() {
        return participantRecords;
    }

    void addParticipantRecord(ParticipantRecord participantRecord) {
        this.participantRecords.add(participantRecord);
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
        event.setTransaction(this);
    }

    public long getDuration() {
        return status.equals(Status.IN_FLIGHT)
                ? System.currentTimeMillis() - startTime
                : endTime - startTime;
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