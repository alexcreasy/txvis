package org.jboss.narayana.txvis.dataaccess;

import javax.persistence.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:26
 */
@Entity
public class Participant {

    private static final long serialVersionUID = -3441505744449729394L;
    private Long id;
    private Transaction transaction;
    private String resourceId;
    private Vote vote;

    public Participant(Transaction transaction, String resourceId) {
        this.transaction = transaction;
        this.resourceId = resourceId;
    }

    public Participant() {}

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

    public String getResourceId() {
        return this.resourceId;
    }

    private void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Enumerated(EnumType.STRING)
    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
