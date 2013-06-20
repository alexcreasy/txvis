package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.EntityManagerServiceBean;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;

import javax.ejb.*;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 20/06/2013
 * Time: 15:31
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ParticipantRecordDAO {

    @EJB
    private GenericDAO dao;

    @EJB
    private EntityManagerServiceBean emf;

    public void create(ParticipantRecord rec) throws NullPointerException {
        dao.create(rec);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @return
     */
    public ParticipantRecord retrieve(String txuid, String rmJndiName) throws NullPointerException {
        if (txuid == null)
            throw new NullPointerException("Method called with null parameter: txuid");
        if (rmJndiName == null)
            throw new NullPointerException("Method called with null parameter: rmJndiName");

        final EntityManager em = emf.createEntityManager();
        try {

            return (ParticipantRecord) em.createQuery("FROM "+ParticipantRecord.class.getSimpleName()+" e " +
                    "WHERE e.transaction.txuid=:txuid AND e.resourceManager.jndiName=:jndiName")
                    .setParameter("txuid", txuid).setParameter("jndiName", rmJndiName).getSingleResult();

        } finally {
            em.close();
        }
    }

    public List<ParticipantRecord> retrieveAll() {
        return dao.retrieveAll(ParticipantRecord.class);
    }

    public void update(ParticipantRecord rec) throws NullPointerException {
        dao.update(rec);
    }

    public void delete(ParticipantRecord rec) throws NullPointerException {
        dao.delete(rec);
    }

    public void deleteAll() {
        dao.deleteAll(ParticipantRecord.class);
    }
}
