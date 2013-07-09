package org.jboss.narayana.txvis.persistence.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jboss.narayana.txvis.logparsing.common.AbstractHandler;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

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
@NamedQueries({
    @NamedQuery(name = "Transaction.findByNodeidAndTxuid",
            query = "SELECT t FROM Transaction t WHERE t.jbossNodeid=:nodeid AND t.txuid=:txuid"),
    @NamedQuery(name = "Transaction.findAllTopLevel", query = "SELECT u FROM Transaction u WHERE u.parent IS EMPTY"),
    @NamedQuery(name = "Transaction.findAllTopLevelWithStatus",
            query = "SELECT u FROM Transaction u WHERE u.parent IS EMPTY AND u.status=:status"),
})
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txuid;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_FLIGHT;

    private String jbossNodeid;
    private Long startTime;
    private Long endTime;


    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
               fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Event> events = new LinkedList<>();

    @ManyToOne
    private Transaction parent = null;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Transaction> subordinates = new HashSet<>();

    // Restrict default constructor to EJB container
    protected Transaction() {}

    /**
     *
     * @param txuid
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Transaction(String txuid) throws NullPointerException, IllegalArgumentException {

        if (!txuid.matches(AbstractHandler.PATTERN_TXUID))
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

        if (!txuid.matches(AbstractHandler.PATTERN_TXUID))
            throw new IllegalArgumentException("Illegal transactionId: " + txuid);

        this.txuid = txuid;
        setStartTime(timestamp);
        events.add(new Event(this, EventType.BEGIN, jbossNodeid, timestamp));
    }

    public Transaction(String txuid, String jbossNodeid, Timestamp timestamp) {
        if (!txuid.matches(AbstractHandler.PATTERN_TXUID))
            throw new IllegalArgumentException("Illegal transactionId: " + txuid);

        this.txuid = txuid;
        this.jbossNodeid = jbossNodeid;
        setStartTime(timestamp);
        events.add(new Event(this, EventType.BEGIN, jbossNodeid, timestamp));
    }

    public Long getId() {
        return id;
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
    public boolean isTopLevel() {
        return parent == null;
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

        Event e = null;
        switch (status) {
            case PREPARE:
                e = new Event(this, EventType.PREPARE, jbossNodeid, timestamp);
                break;
            case COMMIT: case ONE_PHASE_COMMIT:
                e = new Event(this, EventType.COMMIT, jbossNodeid, timestamp);
                setEndTime(timestamp);
                break;
            case PHASE_ONE_ABORT: case PHASE_TWO_ABORT:
                e = new Event(this, EventType.ABORT, jbossNodeid, timestamp);
                setEndTime(timestamp);
                break;
        }
        events.add(e);
    }

    /**
     *
     * @return
     */
    public boolean isDistributed() {
        return parent != null || !subordinates.isEmpty();
    }

    /**
     *
     * @return
     */
    public String getJbossNodeid() {
        return jbossNodeid;
    }

    /**
     *
     * @param nodeId
     */
    public void setJbossNodeid(String nodeId) {
        this.jbossNodeid = nodeId;
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

    /**
     *
     * @param timestamp
     */
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
    public Collection<Transaction> getSubordinates() {
        return subordinates;
    }

    /**
     *
     * @param tx
     */
    public void addSubordinate(Transaction tx) {
        subordinates.add(tx);
    }

    /**
     *
     * @return
     */
    public Transaction getParent() {
        return parent;
    }

    /**
     *
     * @param parent
     */
    public void setParent(Transaction parent) {
        this.parent = parent;
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
     * @return
     */
    public Collection<Event> getEventsInTemporalOrder() {
        //FIXME - Problem with hibernate compatibilty with JPA2.0 OrderBy annotation, this hack will probably
        //FIXME - incur a performance penalty
        Collections.sort((List<Event>) events);
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
     * @return
     */
    public long getDuration() {
        return endTime == null ? System.currentTimeMillis() - startTime : endTime - startTime;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
            .append("Transaction: < tx_uid=`").append(txuid)
            .append("`, nodeid=`").append(jbossNodeid)
            .append("`, parent=`").append(parent)
            .append("`, status=`").append(status)
            .append("`, Subordinate Nodes=`");

        for (Transaction tx : subordinates)
            sb.append(tx.getJbossNodeid()).append(", ");

        sb.append("` >");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + txuid.hashCode();
        result = 31 * result + jbossNodeid.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction))
            return false;
        Transaction tx = (Transaction) obj;

        return txuid.equals(tx.txuid) && jbossNodeid.equals(tx.jbossNodeid);
    }
}