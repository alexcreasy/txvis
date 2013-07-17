package org.jboss.narayana.txvis.plugins;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 10:35
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PluginService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private Collection<Plugin> plugins = new LinkedList<>();


    @PostConstruct
    protected void setup() {

        for (Class<Plugin> c : PluginConfig.PLUGINS) {
            try {
                plugins.add(c.newInstance());
            }
            catch (InstantiationException | IllegalAccessException e) {
                logger.error("PluginService.setup - unable to load plugin: "+c.getSimpleName(), e);
            }
        }

    }

    @PreDestroy
    protected void tearDown() {}

}
