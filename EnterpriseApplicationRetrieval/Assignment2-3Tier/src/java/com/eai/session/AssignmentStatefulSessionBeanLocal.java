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

	public Collection[] fetchAllCustomersFromBothCities();

    public String getCityName();

    public void setCityName(String cityName);

	public String getChangeCityName();

    public void setChangeCityName(String changeCityName);

    public void updateCustomer(CustomerBean customerBean);

    public void addCustomer(CustomerBean customerBean);

    public void deleteCustomer(int customerId);

    public Collection fetchAllAddressesForCustomer(int customerId);

    public void addAddress(int addressId, int customerId, String street, String city, String zipCode, String countryName);

    public void deleteAddress(int addressId, int customerId);

    public void updateAddress(int addressId, int customerId, String street, String city, String zipCode, String countryName);
}
