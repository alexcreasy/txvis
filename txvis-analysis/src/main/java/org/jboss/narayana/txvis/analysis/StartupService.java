package org.jboss.narayana.txvis.analysis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 10:35
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StartupService {

    @PostConstruct
    protected void setup() {}

    @PreDestroy
    protected void tearDown() {}

}
