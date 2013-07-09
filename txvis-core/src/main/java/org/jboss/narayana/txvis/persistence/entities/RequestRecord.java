package org.jboss.narayana.txvis.persistence.entities;

import javax.persistence.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 05/07/2013
 * Time: 17:11
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "RequestRecord.findByRequestId",
                query = "FROM RequestRecord r WHERE r.requestid=:requestid"),
        @NamedQuery(name = "RequestRecord.findByNodeIdAndTxUID",
                query = "FROM RequestRecord s WHERE s.nodeid=:nodeid AND s.txuid=:txuid"),
        @NamedQuery(name = "RequestRecord.findByRequestIdAndTxUID",
                query = "FROM RequestRecord t WHERE t.requestid=:requestid AND t.txuid=:txuid"),
})
public class RequestRecord {

    @Id
    private Long requestid;

    @Column(nullable = false)
    private String nodeid;

    @Column(nullable = true)
    private String txuid;


    public RequestRecord() {}

    public RequestRecord(Long requestid, String nodeid) {
        this.nodeid = nodeid;
        this.requestid = requestid;
    }

    public RequestRecord(Long requestid, String nodeid, String txuid) {
        this.requestid = requestid;
        this.nodeid = nodeid;
        this.txuid = txuid;
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
        return "InterPosition Record: nodeId="+nodeid+", requestId="+requestid+", txuid="+txuid;
    }
}
