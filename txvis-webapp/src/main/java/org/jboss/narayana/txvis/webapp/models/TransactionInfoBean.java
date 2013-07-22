package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
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

    @ManagedProperty(value="#{param.txid}")
    private String txID;

    @Inject
    private DataAccessObject dao;

    private Transaction tx;

    public String getTxID() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.txID = facesContext.getExternalContext().
                getRequestParameterMap().get("txid");
        return this.txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public Transaction getTransaction() {
        return tx;
    }

    public void init() {
        tx = dao.findTransaction(Long.parseLong(getTxID()));
    }
}
