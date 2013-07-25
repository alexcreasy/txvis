package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.persistence.*;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String getShortenedTxUID() {
        String txuid = cause.getTxuid();
        return MessageFormat.format("...{0}", txuid.substring(txuid.length() - 5, txuid.length()));
    }


    public void parse() {
        Pattern p = Pattern.compile(cause.getTxuid());

        Matcher m = p.matcher(body);

        if (m.find()) {
            body = m.replaceAll(getReplacementString());
        }
    }

    private String getReplacementString() {
        return MessageFormat.format("<a href=\"/txvis/txinfo.jsf?includeViewParams=true&amp;txid={0}\">{1}</a>",
                cause.getId(), getShortenedTxUID());
    }


    @Override
    public int hashCode() {
        int result = 17;
        result = result + 37 * title.hashCode();
        result = result + 37 * body.hashCode();
        result = result + 37 * cause.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if(!(obj instanceof Issue))
            return false;

        final Issue i = (Issue) obj;
        return title.equals(i.title) && body.equals(i.body) && cause.equals(i.cause);
    }
}
