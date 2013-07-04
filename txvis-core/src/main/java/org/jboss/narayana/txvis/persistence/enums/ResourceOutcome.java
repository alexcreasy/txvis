package org.jboss.narayana.txvis.persistence.enums;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 23/04/2013
 * Time: 15:10
 */
public enum ResourceOutcome {
    UNKNOWN, PREPARE, COMMIT, ONE_PHASE_COMMIT, ABORT, HEUR_COMMIT, HEUR_ABORT, HEUR_MIXED, HEUR_HAZARD
}
