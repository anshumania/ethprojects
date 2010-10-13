/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.session;

import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface AssignmentSessionBeanLocal {

    public Collection fetchAllCustomers(String cityName);

    public void updateCustomer(String cityName, int customerId, String username, String password, String firstname, String lastname, String email);

    public void addCustomer(String cityName, String username, String password, String firstname, String lastname, String email);

    public void deleteCustomer(String cityName, int customerId);

    public Collection fetchAllAddressesForCustomer(String cityName, int customerId);

    public void addAddress(String cityName, int addressId, int customerId, String street, String city, String zipCode, String countryName);

    public void deleteAddress(String cityName, int addressId);

    public void updateAddress(String cityName, int addressId, int customerId, String street, String city, String zipCode, String countryName);
}
