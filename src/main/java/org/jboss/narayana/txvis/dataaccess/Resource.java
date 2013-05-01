package org.jboss.narayana.txvis.dataaccess;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 14:26
 */
@Entity
public final class Resource implements Serializable {
    private static final long serialVersionUID = 773651601216334875L;
    private Long id;
    private String resourceID;

    private List<ParticipantRecord> enlistedIn;

    Resource(String resourceID) {
        this.resourceID = resourceID;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @OneToMany
    private List<ParticipantRecord> getEnlistedIn() {
        return this.enlistedIn;
    }

    private void setEnlistedIn(List<ParticipantRecord> enlistedIn) {
        this.enlistedIn = enlistedIn;
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