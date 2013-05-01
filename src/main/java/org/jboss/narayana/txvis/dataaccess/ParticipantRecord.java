package org.jboss.narayana.txvis.dataaccess;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 09:33
 */
@Entity
public final class ParticipantRecord implements Serializable {

    private static final long serialVersionUID = -3441505744449729394L;
    private Long id;
    private Transaction transaction;
    private Resource resource;
    private Vote vote;

    ParticipantRecord(Transaction transaction, Resource resource) {
        this.transaction = transaction;
        this.resource = resource;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public Transaction getTransaction() {
        return this.transaction;
    }

    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @ManyToOne
    public Resource getResource() {
        return this.resource;
    }

    private void setResource(Resource resource) {
        this.resource = resource;
    }

    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}