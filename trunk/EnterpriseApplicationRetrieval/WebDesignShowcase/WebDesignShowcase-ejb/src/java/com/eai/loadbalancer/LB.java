package com.eai.loadbalancer;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Max
 */
public class LB {

	private static EntityManagerFactory entityManagerEai1 = Persistence.createEntityManagerFactory("eai");
	private static EntityManagerFactory entityManagerEai2 = Persistence.createEntityManagerFactory("eai2");

	private static int roundRobin = 0;

	public static synchronized EntityManager get() {
		EntityManager result = null;

		if (roundRobin == 0) {
			result = entityManagerEai1.createEntityManager();
		} else {
			result = entityManagerEai2.createEntityManager();
		}

		roundRobin = (roundRobin + 1) % 2;

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public static void persist(Object entity) {
		EntityManager em1, em2;

		em1 = entityManagerEai1.createEntityManager();
		em1.persist(entity);

		em2 = entityManagerEai2.createEntityManager();
		em2.persist(entity);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public static void remove(Object entity) {
		EntityManager em1, em2;

		em1 = entityManagerEai1.createEntityManager();
		em1.remove(entity);

		em2 = entityManagerEai2.createEntityManager();
		em2.remove(entity);
	}

}
