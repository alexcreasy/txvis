package org.jboss.narayana.txvis.data;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 14:26
 */
public class Participant {

    private final String participantID;

    public Participant(String participantID)
            throws IllegalArgumentException, NullPointerException {

        if (participantID.trim().isEmpty())
            throw new IllegalArgumentException("Empty participant ID");

        this.participantID = participantID;
    }

    public String getParticipantID() {
        return this.participantID;
    }

    @Override
    public String toString() {
        return "Participant ID: " + this.participantID;
    }
}
