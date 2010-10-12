/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.session;

import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ANSHUMAN
 */
@Stateless
public class AssignmentSessionBean implements AssignmentSessionBeanLocal {

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;
    
    public Collection fetchAllCustomers() {

        return entityManagerFactory.createEntityManager().createNamedQuery("Customer.findAll").getResultList();

        //return null;
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
