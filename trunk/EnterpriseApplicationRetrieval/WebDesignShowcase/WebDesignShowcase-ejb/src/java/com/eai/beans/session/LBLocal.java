package com.eai.beans.session;

import javax.ejb.Local;
import javax.persistence.EntityManager;

/**
 *
 * @author Tim Church
 */
@Local
public interface LBLocal {

    public EntityManager get();

    public void persist(Object entity);

    public void remove(Object entity);

    public void merge(Object entity);
}
