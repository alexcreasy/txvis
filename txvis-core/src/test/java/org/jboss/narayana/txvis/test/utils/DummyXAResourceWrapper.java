package org.jboss.narayana.txvis.test.utils;

import org.jboss.tm.XAResourceWrapper;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 31/05/2013
 * Time: 18:27
 */
public class DummyXAResourceWrapper implements XAResourceWrapper {

    private XAResource xaResource;
    private String jndiName;

    public DummyXAResourceWrapper(XAResource xaResource, String jndiName) {
        this.xaResource = xaResource;
        this.jndiName = jndiName;
    }

    @Override
    public XAResource getResource() {
        return xaResource;
    }

    @Override
    public String getProductName() {
        return "Dummy Product";
    }

    @Override
    public String getProductVersion() {
        return "1.0.0";
    }

    @Override
    public String getJndiName() {
        return jndiName;
    }

    @Override
    public void commit(Xid xid, boolean b) throws XAException {
        xaResource.commit(xid, b);
    }

    @Override
    public void end(Xid xid, int i) throws XAException {
        xaResource.end(xid, i);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        xaResource.forget(xid);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return xaResource.getTransactionTimeout();
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {
        return this.xaResource.isSameRM(xaResource);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return xaResource.prepare(xid);
    }

    @Override
    public Xid[] recover(int i) throws XAException {
        return xaResource.recover(i);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        xaResource.rollback(xid);
    }

    @Override
    public boolean setTransactionTimeout(int i) throws XAException {
        return xaResource.setTransactionTimeout(i);
    }

    @Override
    public void start(Xid xid, int i) throws XAException {
        xaResource.start(xid, i);
    }
}
