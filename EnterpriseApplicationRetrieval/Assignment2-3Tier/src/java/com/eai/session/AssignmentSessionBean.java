/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.session;

import com.eai.beans.CustomerBean;
import com.eai.entity.Address;
import com.eai.entity.Customer;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.sql.DataSource;

/**
 *
 * @author ANSHUMAN
 */
@Stateless
public class AssignmentSessionBean implements AssignmentSessionBeanLocal {
    
    @PersistenceUnit(unitName="postgresZurich")
    EntityManagerFactory entityManagerFZurich;
    
    @PersistenceUnit(unitName="postgresBerne")
    EntityManagerFactory entityManagerFBerne;

	@Override
    public Collection fetchAllCustomers(String cityName) {
        Collection<Customer> result = null;

        if(cityName.equalsIgnoreCase("Zurich"))
               result = entityManagerFZurich.createEntityManager().createNamedQuery("Customer.findAll").getResultList();
        else if(cityName.equalsIgnoreCase("Berne"))
               result = entityManagerFBerne.createEntityManager().createNamedQuery("Customer.findAll").getResultList();
        else
        {
            /*throw unsupported city exception*/
        }

        //fetch all the customers for this city
        Collection<CustomerBean> customers = new ArrayList<CustomerBean>();
        for(Customer iterator : result)
        {
            // populate the customer bean with the data
            CustomerBean customer = new CustomerBean();
            customer.setCustomerId(iterator.getCustomerId());
            customer.setEmail(iterator.getEmail());
            customer.setFirstname(iterator.getFirstname());
            customer.setLastname(iterator.getLastname());
            customer.setPassword(iterator.getPassword());
            customer.setUsername(iterator.getUsername());
            customer.setDateAdded(iterator.getDateAdded());

            customers.add(customer);
        }

        return customers;
    }

	@Override
    public void updateCustomer(String cityName, int customerId, String username, String password, String firstname, String lastname, String email)
	{
		EntityManager em = null;
		if(cityName.equalsIgnoreCase("Zurich"))
           em = entityManagerFZurich.createEntityManager();
        else if(cityName.equalsIgnoreCase("Berne"))
           em = entityManagerFBerne.createEntityManager();
        else
        {
            /*throw unsupported city exception*/
        }

		Query q = em.createNamedQuery("Customer.findByCustomerId");
		q.setParameter("customerId", customerId);
		Customer c = (Customer)q.getResultList().get(0);

		c.setUsername(username);
		c.setPassword(password);
		c.setFirstname(firstname);
		c.setLastname(lastname);
		c.setEmail(email);

		em.persist(c);
	}

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

}
