package org.jboss.narayana.txvis.persistence;

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

    private EntityManagerFactory emf;


    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    @PostConstruct
    @PostActivate
    private void setup() {
        System.out.println("\n\n\n\n\n\nCreate EMF\n\n\n\n\n\n");
        emf = Persistence.createEntityManagerFactory("org.jboss.narayana.txvis");

    }

    @PreDestroy
    @PrePassivate
    private void tearDown() {
        System.out.println("\n\n\n\n\n\nDestroy EMF\n\n\n\n\n\n");
        emf.close();
    }



}
