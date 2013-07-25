package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/07/2013
 * Time: 22:06
 */
@Named
@RequestScoped
public class GraphPlotterBean {

    public static class Node implements Serializable {

        private String id;
        private String name;
        private String[] data = new String[0];
        private List<Node> children = new LinkedList<>();


        private Node() {}

        private Node(String id, String name) {
            this.id = id;
            this.name = name;
        }


        public static Node populateNewInstance(Transaction tx)  {
            Node parent = new Node();
            populate(parent, tx);
            return parent;
        }

        private static void populate(Node parent, Transaction tx) {
            Node current;

            if (tx.getParent() == null)
                current = parent;
            else {
                current = new Node();
                parent.children.add(current);
            }

            current.id = String.valueOf(tx.getId());
            current.name = tx.getNodeid();

            for (ParticipantRecord rec : tx.getParticipantRecords()) {
                Node participant = new Node(rec.getResourceManager().getJndiName(),
                        rec.getResourceManager().getProductName());
                current.children.add(participant);
            }

            for (Transaction subordinate : tx.getSubordinates())
                populate(current, subordinate);
        }
    }
}
