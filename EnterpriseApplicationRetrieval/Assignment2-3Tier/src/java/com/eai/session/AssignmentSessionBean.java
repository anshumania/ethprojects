package com.eai.session;

import com.eai.beans.AddressBean;
import com.eai.beans.CustomerBean;
import com.eai.entity.Address;
import com.eai.entity.Country;
import com.eai.entity.Customer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.sql.DataSource;

/**
 *
 * @author ANSHUMAN
 */
@Stateless
public class AssignmentSessionBean implements AssignmentSessionBeanLocal {

    @PersistenceUnit(unitName = "postgresZurich")
    EntityManagerFactory entityManagerFZurich;
    @PersistenceUnit(unitName = "postgresBerne")
    EntityManagerFactory entityManagerFBerne;

    @Override
    public Collection fetchAllCustomers(String cityName) {
        Collection<Customer> result = null;

        if (cityName.equalsIgnoreCase("Zurich")) {
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
    public void updateCustomer(String cityName, int customerId, String username, String password, String firstname, String lastname, String email) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Query q = em.createNamedQuery("Customer.findByCustomerId");
        q.setParameter("customerId", customerId);
        Customer c = (Customer) q.getResultList().get(0);

        c.setUsername(username);
        c.setPassword(password);
        c.setFirstname(firstname);
        c.setLastname(lastname);
        c.setEmail(email);

        em.persist(c);
    }

    @Override
    public void addCustomer(String cityName, String username, String password, String firstname, String lastname, String email) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Customer c = new Customer();

        c.setUsername(username);
        c.setPassword(password);
        c.setFirstname(firstname);
        c.setLastname(lastname);
        c.setEmail(email);
        c.setDateAdded(new Date());

        em.persist(c);
    }

    @Override
    public void deleteCustomer(String cityName, int customerId) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
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
    public Collection fetchAllAddressesForCustomer(String cityName, int customerId) {
        Collection<Address> addressEntityCollection = null;

        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", customerId).getSingleResult();
        addressEntityCollection = c.getAddressCollection();
/*
        Collection<AddressBean> addressBeanCollection = new ArrayList<AddressBean>();
        for (Address iterator : addressEntityCollection) {
            // populate the customer bean with the data
            AddressBean address = new AddressBean();
            address.setAddressId(iterator.getAddressId());
            address.setCustomer(c);
            address.setStreet(iterator.getStreet());
            address.setCity(iterator.getCity());
            address.setZipCode(iterator.getZipCode());
            address.setCountry(iterator.getCountry());

            addressBeanCollection.add(address);
        }
        return addressBeanCollection;
 *
 */
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

        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", 1).getSingleResult();
        Country co;
        try {
            co = (Country) em.createNamedQuery("Country.findByCountryName").setParameter("countryName", countryName).getSingleResult();
        } catch(NoResultException ex) {
            int maxCountryId = 0;
            try {
                Country lastCountry = (Country) em.createNamedQuery("Country.getLatestCountry").getSingleResult();
                maxCountryId = lastCountry.getCountryId();
            } catch(NoResultException ex2) {}

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

        //might need this
        //em.refresh(c);
    }

    @Override
    public void deleteAddress(String cityName, int addressId) {
        EntityManager em = null;
        if (cityName.equalsIgnoreCase("Zurich")) {
            em = entityManagerFZurich.createEntityManager();
        } else if (cityName.equalsIgnoreCase("Berne")) {
            em = entityManagerFBerne.createEntityManager();
        } else {
            /*throw unsupported city exception*/
        }

        Query q = em.createNamedQuery("Address.findByAddressId");
        q.setParameter("addressId", addressId);
        Address a = (Address) q.getSingleResult();

        em.remove(a);
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
        Customer c = (Customer) em.createNamedQuery("Customer.findByCustomerId").setParameter("customerId", 1).getSingleResult();
        Country co;
        try {
            co = (Country) em.createNamedQuery("Country.findByCountryName").setParameter("countryName", countryName).getSingleResult();
        } catch(NoResultException ex) {
            int maxCountryId = 0;
            try {
                Country lastCountry = (Country) em.createNamedQuery("Country.getLatestCountry").getSingleResult();
                maxCountryId = lastCountry.getCountryId();
            } catch(NoResultException ex2) {}

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
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
