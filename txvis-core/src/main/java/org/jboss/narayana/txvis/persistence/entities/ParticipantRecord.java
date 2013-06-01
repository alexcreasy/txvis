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

    @ManyToOne(cascade = CascadeType.MERGE)
    private Transaction transaction;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Participant participant;

    @Enumerated(EnumType.STRING)
    private Vote vote = Vote.UNKNOWN;


    protected ParticipantRecord() {}

    public ParticipantRecord(Transaction transaction, Participant participant) throws
            NullPointerException{

        if (transaction == null)
            throw new NullPointerException("Method called with null parameter: transaction");

        if (participant == null)
            throw new NullPointerException("Method called with null parameter: participant");

        this.transaction = transaction;
        this.participant = participant;

        transaction.addParticipantRecord(this);
        participant.addParticipantRecord(this);
    }

    public Long getId() {
        return this.id;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public Participant getParticipant() {
        return this.participant;
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
