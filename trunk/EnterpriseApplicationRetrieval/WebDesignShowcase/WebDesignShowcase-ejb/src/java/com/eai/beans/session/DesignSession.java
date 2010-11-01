package com.eai.beans.session;

import com.eai.beans.entity.Designs;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author Max
 */
@Stateless
public class DesignSession implements DesignSessionLocal {

	@PersistenceUnit(unitName = "eai")
    EntityManagerFactory entityManagerEai;

	@Override
	public Collection<Designs> findDesignsByUserId(long userID) {
		Collection<Designs> d = entityManagerEai.createEntityManager()
				.createNamedQuery("Designs.findByUserId")
				.setParameter("userId", userID)
				.getResultList();
		return d;
	}
 
}
