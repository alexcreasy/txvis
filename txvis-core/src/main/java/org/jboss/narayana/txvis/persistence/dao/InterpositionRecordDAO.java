package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.entities.InterpositionRecord;

import javax.ejb.*;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 05/07/2013
 * Time: 17:20
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class InterpositionRecordDAO implements Serializable {


    @EJB
    private GenericDAO dao;

    public void create(InterpositionRecord interpositionRecord) {
        dao.create(interpositionRecord);
    }

    public InterpositionRecord retrieve(Long requestId) {
        return dao.querySingle(InterpositionRecord.class, "FROM InterpositionRecord i WHERE i.requestid="+requestId);
    }

    public InterpositionRecord update(InterpositionRecord interpositionRecord) {
        return dao.update(interpositionRecord);
    }

    public void delete(InterpositionRecord interpositionRecord) {
        dao.delete(interpositionRecord);
    }

}
