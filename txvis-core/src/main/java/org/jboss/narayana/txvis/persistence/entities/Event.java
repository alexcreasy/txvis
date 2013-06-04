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
    private String eventValue;

    protected Event() {}

    public Event(Transaction transaction, EventType eventType, String eventValue, Timestamp timestamp) {
        this.transaction = transaction;
        this.eventType = eventType;
        this.eventValue = eventValue;
        this.timestamp = timestamp;
    }

    public Event(EventType eventType, String eventValue, Timestamp timestamp) {
        this.eventType = eventType;
        this.eventValue = eventValue;
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

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getEventValue() {
        return eventValue;
    }

}
