package org.jboss.narayana.txvis.webapp;

import org.jboss.narayana.txvis.LogProcessor;
import org.jboss.narayana.txvis.persistence.DataAccessObject;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/05/2013
 * Time: 16:13
 */
@Named
@SessionScoped
public class Test implements Serializable {

    @EJB
    private DataAccessObject dao;

    @EJB
    private LogProcessor txmon;

//    @PostConstruct
//    public void postConstruct() {
//        txmon.start();
//
//    }


    public String someData() {
        return "This is a string";
    }
}
