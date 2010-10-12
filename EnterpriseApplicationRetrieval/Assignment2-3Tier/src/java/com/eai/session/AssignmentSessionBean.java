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


    
    public Collection fetchAllCustomers(String cityName) {
        
        // just a trial for updating data
        //editCustomerInformation();
       
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

    /**
     * a dummy test function to check for updates
     */
    public void editCustomerInformation()
    {
        Customer result = (Customer)entityManagerFBerne.createEntityManager().createNamedQuery("Customer.findByCustomerId").setParameter("customerId", 1).getResultList().get(0);
        System.out.println("customer="+ result.getFirstname());
        Collection<Address> addresses = result.getAddressCollection();
        for(Address address : addresses)
        {
            System.out.println("address.getAddressId=" + address.getAddressId());
            if(address.getAddressId() == 1)
            {
                int numb = entityManagerFBerne.createEntityManager().createNamedQuery("Address.updateStreet").setParameter("street", "result").setParameter("addressId", 1).executeUpdate();
                System.out.println("data changed = " + numb);
            }
        }


    }


    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")


 
}
