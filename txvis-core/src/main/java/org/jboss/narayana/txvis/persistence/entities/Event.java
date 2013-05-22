package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.persistence.enums.EventType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 22:57
 */
@Entity
public class Event implements Serializable {

    private Long id;
    private Transaction transaction;
    private Timestamp timestamp;
    private EventType eventType;


    public Event(Transaction transaction, Timestamp timestamp,
                 EventType eventType) {
        this.transaction = transaction;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Timestamp getTimestamp() {
        return timestamp;
    }
    @Enumerated(EnumType.STRING)
    public EventType getEventType() {
        return eventType;
    }

    @ManyToOne
    public Transaction getTransaction() {
        return transaction;
    }


    /*
     * Methods for container
     */
    public Event() {}

    private void setId(Long id) {
        this.id = id;
    }

    private void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    private void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    private void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}