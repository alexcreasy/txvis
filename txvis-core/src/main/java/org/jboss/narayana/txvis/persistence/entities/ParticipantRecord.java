package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
    private ResourceManager resourceManager;

    @Enumerated(EnumType.STRING)
    private Vote vote = Vote.UNKNOWN;


    protected ParticipantRecord() {}

    public ParticipantRecord(Transaction transaction, ResourceManager resourceManager, Timestamp timestamp) throws
            NullPointerException{

        if (transaction == null)
            throw new NullPointerException("Method called with null parameter: transaction");

        if (resourceManager == null)
            throw new NullPointerException("Method called with null parameter: resourceManager");

        this.transaction = transaction;
        this.resourceManager = resourceManager;

        transaction.addParticipantRecord(this);
        resourceManager.addParticipantRecord(this);

        transaction.addEvent(new Event(EventType.ENLIST, resourceManager.getJndiName(), timestamp));
    }

    public Long getId() {
        return this.id;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
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
