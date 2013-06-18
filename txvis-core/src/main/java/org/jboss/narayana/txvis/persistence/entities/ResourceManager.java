package org.jboss.narayana.txvis.persistence.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 22:29
 */
@Entity
public class ResourceManager implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "resourceManager", fetch = FetchType.EAGER)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    private String jndiName;
    private String productName;
    private String productVersion;

    protected ResourceManager() {}

    public ResourceManager(String jndiName, String productName, String productVersion)
            throws IllegalArgumentException, NullPointerException {
        if (jndiName.trim().isEmpty())
            throw new IllegalArgumentException("Method called with empty parameter: jndiName");
        this.jndiName = jndiName;

        this.productName = productName != null ? productName : "Unknown";
        this.productVersion = productVersion != null ? productVersion : "Unknown";
    }

    public Long getId() {
        return id;
    }

    public String getJndiName() {
        return jndiName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    void addParticipantRecord(ParticipantRecord rec) {
        if (rec == null)
            throw new NullPointerException("Method called with null parameter: rec");
        participantRecords.add(rec);
    }

    public Collection<ParticipantRecord> getParticipantRecords() {
        return Collections.unmodifiableCollection(participantRecords);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ResourceManager: JNDIName: ").append(jndiName)
                .append(" Product Name: ").append(productName)
                .append(" Product Version: ").append(productVersion);
        return sb.toString();
    }
}
