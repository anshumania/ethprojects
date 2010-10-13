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

}
