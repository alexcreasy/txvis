package org.jboss.narayana.txvis.persistence.enums;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 23/04/2013
 * Time: 11:51
 */
public enum Status {
    IN_FLIGHT, PREPARE, COMMIT, ONE_PHASE_COMMIT, PHASE_ONE_ABORT, PHASE_TWO_ABORT
}
