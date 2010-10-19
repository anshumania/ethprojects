/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.session;

import com.eai.beans.CustomerBean;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface AssignmentStatefulSessionBeanLocal {

    public Collection fetchAllCustomers();

    public String getCityName();

    public void setCityName(String cityName);

    public void updateCustomer(CustomerBean customerBean);

    public void addCustomer(String cityName, CustomerBean customerBean);

    public void deleteCustomer(int customerId);

    public Collection fetchAllAddressesForCustomer(int customerId);

    public void addAddress(String cityName, int addressId, int customerId, String street, String city, String zipCode, String countryName);

    public void deleteAddress(String cityName, int addressId, int customerId);

    public void updateAddress(String cityName, int addressId, int customerId, String street, String city, String zipCode, String countryName);
}
