package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 21:55
 */
@Named
@RequestScoped
public class TransactionInfoBean implements Serializable {

    @ManagedProperty(value="#{param.txuid}")
    private String txUID;

    @Inject
    private GenericDAO dao;

    private Transaction tx;

    public String getTxUID() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.txUID = (String) facesContext.getExternalContext().
                getRequestParameterMap().get("txuid");
        return this.txUID;
    }

    public void setTxUID(String txUID) {
        this.txUID = txUID;
    }

    public Transaction getTransaction() {
        return tx;
    }

    public void init() {
        tx = dao.retrieve(Transaction.class, getTxUID());
    }
}
