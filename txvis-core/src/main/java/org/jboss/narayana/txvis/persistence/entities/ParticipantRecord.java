package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:26
 */
@Entity
public class ParticipantRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Transaction transaction;

    @ManyToOne
    private Participant participant;

    private String resourceRecordId;

    @Enumerated(EnumType.STRING)
    private Vote vote = Vote.UNKNOWN;


    protected ParticipantRecord() {}

    public ParticipantRecord(Transaction transaction, String resourceRecordId) throws
            NullPointerException, IllegalArgumentException {
        if (transaction == null)
            throw new NullPointerException("Method called with null parameter: transaction");
        if (resourceRecordId.trim().isEmpty())
            throw new IllegalArgumentException("Method called with empty String parameter: resourceRecordId");

        this.transaction = transaction;
        this.resourceRecordId = resourceRecordId;
    }


    public Long getId() {
        return this.id;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public String getResourceRecordId() {
        return this.resourceRecordId;
    }

    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) throws NullPointerException {
        if (vote == null)
            throw new NullPointerException("Method called with null parameter: vote");
        this.vote = vote;
    }
}
