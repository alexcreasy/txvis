package org.jboss.narayana.txvis.dataaccess;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 09:33
 */
public class ParticipantRecord {

    private final Resource resource;

    private Vote vote;


    ParticipantRecord(Resource resource) throws NullPointerException {
        if (resource == null)
            throw new NullPointerException();
        this.resource = resource;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setVote(Vote vote) throws NullPointerException {
        if (vote == null)
            throw new NullPointerException();
        this.vote = vote;
    }
}
