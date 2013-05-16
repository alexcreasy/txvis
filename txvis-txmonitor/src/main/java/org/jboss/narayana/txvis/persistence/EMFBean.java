package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/05/2013
 * Time: 14:50
 */
@Singleton
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EMFBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private EntityManagerFactory emf;

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    @PostConstruct
    @PostActivate
    private void setup() {
        emf = Persistence.createEntityManagerFactory("org.jboss.narayana.txvis");

        if (logger.isInfoEnabled())
            logger.info("Initialised EMF");
    }

    @PreDestroy
    @PrePassivate
    private void tearDown() {
        if(emf.isOpen())
            emf.close();
        if (logger.isInfoEnabled())
            logger.info("Shutdown EMF");
    }
}
