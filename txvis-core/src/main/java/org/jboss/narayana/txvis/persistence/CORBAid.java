package org.jboss.narayana.txvis.persistence;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 20:18
 */
public class CORBAid {

    private final Long requestId;
    private final String ior;
    private final String nodeid;

    public CORBAid(Long requestId, String ior, String nodeid) {
        this.requestId = requestId;
        this.ior = ior;
        this.nodeid = nodeid;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getIor() {
        return ior;
    }

    public String getNodeid() {
        return nodeid;
    }
}
