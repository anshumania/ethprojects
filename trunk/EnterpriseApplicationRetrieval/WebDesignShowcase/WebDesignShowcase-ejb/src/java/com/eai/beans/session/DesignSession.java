package com.eai.beans.session;

import com.eai.beans.entity.Comments;
import com.eai.beans.entity.Designs;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
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
	public Collection<Designs> findAllDesigns() {
		Collection<Designs> d = entityManagerEai.createEntityManager()
				.createNamedQuery("Designs.findAll")
				.getResultList();
		return d;
	}

	@Override
	public Collection<Designs> findDesignsByUserId(long userID) {
		Collection<Designs> d = entityManagerEai.createEntityManager()
				.createNamedQuery("Designs.findByUserId")
				.setParameter("userId", userID)
				.getResultList();
		return d;
	}

	@Override
	public void addDesign(long userID, String title, String url, String screenshot) {
		EntityManager em = entityManagerEai.createEntityManager();
		List<Designs> designs = em.createNamedQuery("Designs.findAll")
				.getResultList();

		long id = designs.get(designs.size()-1).getId() + 1;

		Designs d = new Designs();
		d.setId(id);
		d.setUserId(userID);
		d.setTitle(title);
		d.setUrl(url);
		d.setImageUrl(screenshot);

		em.persist(d);
	}

        @Override
        public Designs findDesignByDesignId(long designID)
        {
            	EntityManager em = entityManagerEai.createEntityManager();

		Designs d = (Designs)em.createNamedQuery("Designs.findById")
				.setParameter("id", designID)
				.getResultList()
				.get(0);
                return d;
        }


	@Override
        @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void deleteDesign(long designID) {
		EntityManager em = entityManagerEai.createEntityManager();

		Designs d = (Designs)em.createNamedQuery("Designs.findById")
				.setParameter("id", designID)
				.getResultList()
				.get(0);

		Collection<Comments> comments = em.createNamedQuery("Comments.findByDesignId")
				.setParameter("designId", designID)
				.getResultList();

		for (Comments c : comments) {
			em.remove(c);
		}

		if (d != null) {
			em.remove(d);
		}
	}
 
}
