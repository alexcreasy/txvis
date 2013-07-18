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
                query = "FROM RequestRecord r WHERE r.requestid=:requestid"
        ),
        @NamedQuery(name = "RequestRecord.findByNodeIdAndTxUID",
                query = "FROM RequestRecord r WHERE r.nodeid=:nodeid AND r.txuid=:txuid"
        ),
        @NamedQuery(name = "RequestRecord.findByRequestIdAndTxUID",
                query = "FROM RequestRecord r WHERE r.requestid=:requestid AND r.txuid=:txuid"
        ),
        @NamedQuery(name = "RequestRecord.findByRequestIdAndIOR",
                query = "FROM RequestRecord r where r.requestid=:requestid AND r.ior=:ior"
        )
})
public class RequestRecord {

    @Id
    private Long requestid;

    @Column(nullable = false)
    private String nodeid;

    @Column(nullable = false, columnDefinition = "text")
    private String ior;

    @Column(nullable = true)
    private String txuid;


    public RequestRecord() {}

    public RequestRecord(Long requestid, String nodeid, String ior) {
        this.nodeid = nodeid;
        this.requestid = requestid;
        this.ior = ior;
    }

    public RequestRecord(Long requestid, String nodeid, String ior, String txuid) {
        this.requestid = requestid;
        this.nodeid = nodeid;
        this.txuid = txuid;
        this.ior = ior;
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

    public String getIor() {
        return ior;
    }

    public void setIor(String ior) {
        this.ior = ior;
    }

    @Override
    public String toString() {
        return "RequestRecord: nodeId="+nodeid+", requestId="+requestid+", ior="+ior+", txuid="+txuid;
    }
}
