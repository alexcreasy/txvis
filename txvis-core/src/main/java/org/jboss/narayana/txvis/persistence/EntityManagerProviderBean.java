package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.MessageFormat;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/05/2013
 * Time: 14:50
 */
@Singleton
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EntityManagerProviderBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private EntityManagerFactory emf;

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    @PostConstruct
    @PostActivate
    private void setup() {
        emf = Persistence.createEntityManagerFactory(Configuration.PERSISTENCE_CONTEXT);

        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format(
                    "Initialised EntityManagerProviderBean with persistence context: {0}",
                    Configuration.PERSISTENCE_CONTEXT));
    }

    @PreDestroy
    @PrePassivate
    private void tearDown() {
        if(emf.isOpen())
            emf.close();
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format(
                    "Shutdown EntityManagerProviderBean with persistence context: {0}",
                    Configuration.PERSISTENCE_CONTEXT));
    }
}
