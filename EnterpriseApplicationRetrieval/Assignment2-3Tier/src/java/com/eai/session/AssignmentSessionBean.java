/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.session;

import com.eai.entity.Address;
import com.eai.entity.Customer;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

/**
 *
 * @author ANSHUMAN
 */
@Stateless
public class AssignmentSessionBean implements AssignmentSessionBeanLocal {

    @PersistenceUnit(unitName="ctx_eai_zurich")
    EntityManagerFactory entityManagerFactory;

	@Override
    public List<Customer> fetchAllCustomers() {
        return entityManagerFactory.createEntityManager().createNamedQuery("Customer.findAll").getResultList();
    }

	@Override
    public List<Address> fetchAddressesByCustomerId(Customer customer) {
        Query q = entityManagerFactory.createEntityManager().createNamedQuery("Address.findByCustomerId");
		q.setParameter("customer", customer);
		return q.getResultList();
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
