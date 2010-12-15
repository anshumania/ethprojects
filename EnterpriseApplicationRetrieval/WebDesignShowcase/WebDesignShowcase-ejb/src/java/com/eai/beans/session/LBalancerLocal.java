/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.beans.session;

import javax.ejb.Local;
import javax.persistence.EntityManager;

/**
 *
 * @author Tim Church
 */
@Local
public interface LBalancerLocal {

    public  EntityManager get() ;

     public  void persist(Object entity);

}
