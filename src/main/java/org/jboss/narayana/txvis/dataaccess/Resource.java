package org.jboss.narayana.txvis.dataaccess;

import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 14:26
 */
public final class Resource implements Serializable {
    private static final long serialVersionUID = 773651601216334875L;
    private String resourceID;

    Resource(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getResourceID() {
        return this.resourceID;
    }

    private void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    @Override
    public String toString() {
        return "Resource ID: " + this.resourceID;
    }
}
