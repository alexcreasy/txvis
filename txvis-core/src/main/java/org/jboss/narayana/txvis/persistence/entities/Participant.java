package org.jboss.narayana.txvis.persistence.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 22:29
 */
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "participant")
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    private String resourceName;

    protected Participant() {}

    public void addParticipantRecord(ParticipantRecord p) {
        if (p == null)
            throw new NullPointerException("Method called with null parameter: p");
        participantRecords.add(p);
    }

    public Collection<ParticipantRecord> getParticipantRecords() {
        return Collections.unmodifiableCollection(participantRecords);
    }


}
