package org.jboss.narayana.txvis.persistence.entities;

import javax.persistence.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 05/07/2013
 * Time: 17:11
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "InterpositionRecord.findByRequestId",
                query = "FROM InterpositionRecord r WHERE r.requestid=:requestid"),
})
public class InterpositionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nodeid;
    private Long requestid;
    private String txuid;


    public InterpositionRecord() {}

    public InterpositionRecord(String nodeid, Long requestid) {
        this.nodeid = nodeid;
        this.requestid = requestid;
    }

    public Long getId() {
        return id;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public Long getRequestid() {
        return requestid;
    }

    public void setRequestid(Long requestid) {
        this.requestid = requestid;
    }

    public String getTxuid() {
        return txuid;
    }

    public void setTxuid(String txuid) {
        this.txuid = txuid;
    }

    @Override
    public String toString() {
        return "InterPosition Record: "+id+" nodeId="+nodeid+", requestId="+requestid+", txuid="+txuid;
    }
}
