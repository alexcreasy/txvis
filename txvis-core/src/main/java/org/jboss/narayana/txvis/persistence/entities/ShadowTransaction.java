package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.logparsing.AbstractHandler;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 02/07/2013
 * Time: 03:15
 */
@Entity
public class ShadowTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txuid;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_FLIGHT;

    private boolean onePhase = false;
    private boolean distributed;
    private String nodeId;
    private Long startTime;
    private Long endTime;

    @ElementCollection
    private Set<Long> participantRecords = new HashSet<>();

    @ElementCollection
    private Set<Long> events = new HashSet<>();


    // Restrict default constructor to EJB container
    protected ShadowTransaction() {}

    /**
     *
     * @param txuid
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public ShadowTransaction(String txuid) throws NullPointerException, IllegalArgumentException {

        if (!txuid.matches(AbstractHandler.PATTERN_TXUID))
            throw new IllegalArgumentException("Illegal transactionId: " + txuid);

        this.txuid = txuid;
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
        this.onePhase = isOnePhase;
    }

    /**
     *
     * @return
     */
    public boolean isDistributed() {
        return distributed;
    }

    /**
     *
     * @param distributed
     */
    public void setDistributed(boolean distributed) {
        this.distributed = distributed;
    }

    /**
     *
     * @return
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     *
     * @param nodeId
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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
    public Collection<Long> getParticipantRecords() {
        return participantRecords;
    }

    /**
     *
     * @param participantRecord
     */
    void addParticipantRecord(Long participantRecord) {
        this.participantRecords.add(participantRecord);
    }
}
