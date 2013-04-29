package org.jboss.narayana.txvis.dataaccess;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 18:16
 */
public interface ResourceDAO {
    Resource get(String resourceID)
            throws IllegalArgumentException, NullPointerException;
}
