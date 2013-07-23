package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 17:53
 */
public class Issue implements Serializable {

    private String title;
    private String body;
    private boolean read;
    private Transaction cause;

    public Issue() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Transaction getCause() {
        return cause;
    }

    public void setCause(Transaction cause) {
        this.cause = cause;
    }
}
