package org.jboss.narayana.txvis.dataaccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 14:49
 */
public final class ResourceDAOInMemoryImpl implements ResourceDAO {

    private final Map<String, Resource> resources =
            Collections.synchronizedMap(new HashMap<String, Resource>());

    ResourceDAOInMemoryImpl() {}

    @Override
    public Resource get(String resourceID)
            throws IllegalArgumentException, NullPointerException {
        Resource resource = this.resources.get(resourceID);

        if (resource == null) {
            resource = new Resource(resourceID);
            this.resources.put(resourceID, resource);
        }

        return resource;
    }
}
