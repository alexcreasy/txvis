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
import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
@Entity
public class Transaction implements Serializable {

    @Id
    private String txuid;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_FLIGHT;

    private boolean onePhase = false;
    private Long startTime;
    private Long endTime;

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
               fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Event> events = new LinkedList<>();


    // Restrict default constructor to EJB container
    protected Transaction() {}

    /**
     *
     * @param txuid
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Transaction(String txuid) throws NullPointerException, IllegalArgumentException {

        if (!txuid.matches(AbstractHandler.PATTERN_TXID))
            throw new IllegalArgumentException("Illegal transactionId: " + txuid);

        this.txuid = txuid;
    }

    /**
     *
     * @param txuid
     * @param timestamp
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Transaction(String txuid, Timestamp timestamp) throws NullPointerException, IllegalArgumentException {

        if (!txuid.matches(AbstractHandler.PATTERN_TXID))
            throw new IllegalArgumentException("Illegal transactionId: " + txuid);

        this.txuid = txuid;
        setStartTime(timestamp);
        events.add(new Event(this, EventType.BEGIN, "N/A", timestamp));
    }

    public String getId() {
        return getTxuid();
    }

    /**
     *
     * @return
     */
    public String getTxuid() {
        return this.txuid;
    }

    /**
     *
     * @return
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     *
     * @param status
     * @param timestamp
     */
    public void setStatus(Status status, Timestamp timestamp) {
        this.status = status;
        events.add(new Event(this, EventType.END, status.toString(), timestamp));
        setEndTime(timestamp);
    }

    /**
     *
     * @return
     */
    public boolean isOnePhase() {
        return onePhase;
    }

    /**
     *
     * @param isOnePhase
     */
    public void setOnePhase(boolean isOnePhase) {
        for (ParticipantRecord rec : participantRecords)
            rec.setVote(Vote.COMMIT);
        this.onePhase = isOnePhase;
    }

    /**
     *
     * @return
     */
    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    private void setStartTime(Timestamp timestamp) {
        this.startTime = timestamp.getTime();
    }

    /**
     *
     * @return
     */
    public Timestamp getEndTime() {
        return (endTime != null) ? new Timestamp(endTime) : null;
    }

    private void setEndTime(Timestamp timestamp) {
        this.endTime = timestamp.getTime();
    }

    /**
     *
     * @return
     */
    public Collection<ParticipantRecord> getParticipantRecords() {
        return participantRecords;
    }

    /**
     *
     * @param participantRecord
     */
    void addParticipantRecord(ParticipantRecord participantRecord) {
        this.participantRecords.add(participantRecord);
    }

    /**
     *
     * @return
     */
    public Collection<Event> getEvents() {
        return events;
    }

    /**
     *
     * @param event
     */
    public void addEvent(Event event) {
        this.events.add(event);
        event.setTransaction(this);
    }

    /**
     *
     * @param timestamp
     */
    public void prepare(Timestamp timestamp) {
        events.add(new Event(this, EventType.PREPARE, "N/A", timestamp));
    }

    /**
     *
     * @return
     */
    public long getDuration() {
        return status.equals(Status.IN_FLIGHT) ? System.currentTimeMillis() - startTime : endTime - startTime;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("Transaction: uid: ").append(txuid)
                .append(", Status: ").append(status);
        return sb.toString();
    }
}