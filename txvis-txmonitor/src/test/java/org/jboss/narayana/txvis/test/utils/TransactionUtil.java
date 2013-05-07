package org.jboss.narayana.txvis.test.utils;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.narayana.txvis.dataaccess.Status;

import javax.transaction.RollbackException;
import java.util.UUID;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 04/05/2013
 * Time: 14:55
 */
public class TransactionUtil {


    public void createTx(int noOfTx, int noOfParticipantsPerTx, Status outcome) throws Exception {
        for (int i = 0; i < noOfTx; i++)
            createTx(noOfParticipantsPerTx, outcome);
    }

    public void createTx(int noOfParticipantsPerTx, Status outcome) throws Exception {
        TransactionManager.transactionManager().begin();

        if (outcome.equals(Status.ROLLBACK_RESOURCE)) {
            TransactionManager.transactionManager().getTransaction().enlistResource(
                    new DummyXAResource(UUID.randomUUID().toString(), false));
            noOfParticipantsPerTx--;
        }

        for (int i = 0; i < noOfParticipantsPerTx; i++)
            TransactionManager.transactionManager().getTransaction().enlistResource(
                    new DummyXAResource(UUID.randomUUID().toString()));

        if (outcome.equals(Status.ROLLBACK_CLIENT))
                TransactionManager.transactionManager().rollback();
        else
            try {
                TransactionManager.transactionManager().commit();
            }
            catch (RollbackException e) {}
    }
}
