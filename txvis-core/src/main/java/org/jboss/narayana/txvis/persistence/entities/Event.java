package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.persistence.enums.EventType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 22:57
 */
@Entity
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Transaction transaction;
    private Timestamp timestamp;
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    protected Event() {}

    public Event(Transaction transaction, EventType eventType, Timestamp timestamp) {
        this.transaction = transaction;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    public Event(EventType eventType, Timestamp timestamp) {
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    void setTransaction(Transaction t) {
        this.transaction = transaction;
    }
}
