package org.jboss.narayana.txvis.dataaccess;

import javax.ejb.Local;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 07/05/2013
 * Time: 22:35
 */
@Local
public interface DataAccessObject {
    Transaction create(String transactionId);

    Transaction retrieve(String transactionId);

    void delete(String transactionId);

    void deleteAll();

    Collection<Transaction> retrieveAll();

    void enlistParticipant(String transactionId, String resourceId);

    Participant getEnlistedParticipant(String transactionId, String resourceId);

    void setOutcome(String transactionId, Status outcome);

    void setParticipantVote(String transactionId, String resourceId, Vote vote);
}
