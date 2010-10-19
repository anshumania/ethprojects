/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.session;

import com.eai.beans.CustomerBean;
import com.eai.entity.Address;
import com.eai.entity.Country;
import com.eai.entity.Customer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.ejb.Stateful;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ANSHUMAN
 */
@Stateful
public class AssignmentStatefulSessionBean implements AssignmentStatefulSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceUnit(unitName = "postgresZurich")
    EntityManagerFactory entityManagerFZurich;
    @PersistenceUnit(unitName = "postgresBerne")
    EntityManagerFactory entityManagerFBerne;
    String cityName;

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public Collection fetchAllCustomers() {
        Collection<Customer> result = null;

        if (getCityName().equalsIgnoreCase("Zurich")) {
            result = entityManagerFZurich.createEntityManager().createNamedQuery("Customer.findAll").getResultList();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            result = entityManagerFBerne.createEntityManager().createNamedQuery("Customer.findAll").getResultList();
        } else {
            /*throw unsupported city exception*/
        }

        //fetch all the customers for this city
        Collection<CustomerBean> customers = new ArrayList<CustomerBean>();
        for (Customer iterator : result) {
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
    public void updateCustomer(CustomerBean customerBean) {
        EntityManager em = null;
        if (getCityName().equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (getCityName().equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Query q = em.createNamedQuery("Customer.findByCustomerId");
        q.setParameter("customerId", customerBean.getCustomerId());
        Customer c = (Customer) q.getResultList().get(0);

        c.setUsername(customerBean.getUsername());
        c.setPassword(customerBean.getPassword());
        c.setFirstname(customerBean.getFirstname());
        c.setLastname(customerBean.getLastname());
        c.setEmail(customerBean.getEmail());

        em.persist(c);
    }

    @Override
    public void addCustomer(String cityName,CustomerBean customerBean) {
        EntityManager em = null;
        if (getCityName().equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (getCityName().equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Customer c = new Customer();
        c.setCustomerId(customerBean.getCustomerId());
        c.setUsername(customerBean.getUsername());
        c.setPassword(customerBean.getPassword());
        c.setFirstname(customerBean.getFirstname());
        c.setLastname(customerBean.getLastname());
        c.setEmail(customerBean.getEmail());
        c.setDateAdded(new Date());

        em.persist(c);
    }

    @Override
    public void deleteCustomer(int customerId) {
        EntityManager em = null;
        if (getCityName().equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (getCityName().equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Query q = em.createNamedQuery("Customer.findByCustomerId");
        q.setParameter("customerId", customerId);
        Customer c = (Customer) q.getResultList().get(0);

        em.remove(c);
    }

    @Override
    public Collection fetchAllAddressesForCustomer(int customerId) {
        Collection<Address> addressEntityCollection = null;

        EntityManager em = null;
        if (getCityName().equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (getCityName().equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", customerId).getSingleResult();
        addressEntityCollection = c.getAddressCollection();

        return addressEntityCollection;
    }

    @Override
    public void addAddress(String cityName, int addressId, int customerId, String street, String city, String zipCode, String countryName) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", customerId).getSingleResult();
        Country co;
        try {
            co = (Country) em.createNamedQuery("Country.findByCountryName").setParameter("countryName", countryName).getResultList().get(0);
        } catch (Exception ex) {
            int maxCountryId = 0;
            try {
                Country lastCountry = (Country) em.createNamedQuery("Country.getLatestCountry").getResultList().get(0);
                ;
                maxCountryId = lastCountry.getCountryId();
            } catch (NoResultException ex2) {
            }

            co = new Country();
            co.setCountryId(maxCountryId + 1);
            co.setCountryName(countryName);
            em.persist(co);
        }

        Address a = new Address();
        a.setAddressId(addressId);
        a.setCustomer(c);
        a.setStreet(street);
        a.setCity(city);
        a.setZipCode(zipCode);
        a.setCountry(co);
        em.persist(a);

        //reload customer with new address included
        em.refresh(c);
    }

    @Override
    public void deleteAddress(String cityName, int addressId, int customerId) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Address a = (Address) em.createNamedQuery("Address.findByAddressId").setParameter("addressId", addressId).getSingleResult();
        em.remove(a);

        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", customerId).getSingleResult();
        c.removeAddress(a);
    }

    @Override
    public void updateAddress(String cityName, int addressId, int customerId, String street, String city, String zipCode, String countryName) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Address a = (Address) em.createNamedQuery("Address.findByAddressId").setParameter("addressId", addressId).getSingleResult();
        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", customerId).getSingleResult();
        Country co;
        try {
            co = (Country) em.createNamedQuery("Country.findByCountryName").setParameter("countryName", countryName).getResultList().get(0);
        } catch (Exception ex) {
            int maxCountryId = 0;
            try {
                Country lastCountry = (Country) em.createNamedQuery("Country.getLatestCountry").getResultList().get(0);
                maxCountryId = lastCountry.getCountryId();
            } catch (NoResultException ex2) {
            }

            co = new Country();
            co.setCountryId(maxCountryId + 1);
            co.setCountryName(countryName);
            em.persist(co);
        }

        a.setAddressId(addressId);
        a.setCustomer(c);
        a.setStreet(street);
        a.setCity(city);
        a.setZipCode(zipCode);
        a.setCountry(co);
        em.persist(a);
        em.refresh(c);
    }
}
