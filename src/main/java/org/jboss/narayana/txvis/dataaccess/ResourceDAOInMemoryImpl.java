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

    private final Map<String, Resource> resources = new HashMap<String, Resource>();

    ResourceDAOInMemoryImpl() {}

    @Override
    public Resource get(String resourceId)
            throws IllegalArgumentException, NullPointerException {
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty parameter resourceId");

        Resource resource = this.resources.get(resourceId);

        if (resource == null) {
            resource = new Resource(resourceId);
            this.resources.put(resourceId, resource);
        }
        return resource;
    }
}
