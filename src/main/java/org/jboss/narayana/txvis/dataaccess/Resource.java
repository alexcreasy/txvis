package org.jboss.narayana.txvis.dataaccess;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 14:26
 */
public final class Resource {

    private final String participantID;
    private Vote vote = null;

    Resource(String participantID)
            throws IllegalArgumentException, NullPointerException {
        if (participantID.trim().isEmpty())
            throw new IllegalArgumentException("Empty participant ID");

        this.participantID = participantID;
    }

    public String getParticipantID() {
        return this.participantID;
    }

    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) throws NullPointerException {
        if (vote == null)
            throw new NullPointerException("null param vote");
        this.vote = vote;
    }

    @Override
    public String toString() {
        return "Resource ID: " + this.participantID;
    }
}
