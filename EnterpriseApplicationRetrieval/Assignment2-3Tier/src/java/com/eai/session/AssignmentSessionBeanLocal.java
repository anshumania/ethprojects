/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.session;

import com.eai.entity.Address;
import com.eai.entity.Customer;
import java.util.*;
import javax.ejb.Local;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface AssignmentSessionBeanLocal {

    public List<Customer> fetchAllCustomers();
	public List<Address> fetchAddressesByCustomerId(Customer customer);
    
}
