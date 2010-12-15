package com.eai.beans.session;

import com.eai.beans.entity.Comments;
import com.eai.beans.entity.Designs;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

/**
 *
 * @author Max
 */
@Stateless
public class DesignSession implements DesignSessionLocal {

    @EJB
    LBLocal lb;

    @Override
    public Collection<Designs> findAllDesigns() {
        Collection<Designs> d = lb.get().createNamedQuery("Designs.findAll").getResultList();
        return d;
    }

    @Override
    public Collection<Designs> findDesignsByUserId(long userID) {
        Collection<Designs> d = lb.get().createNamedQuery("Designs.findByUserId").setParameter("userId", userID).getResultList();
        return d;
    }

    @Override
    public void addDesign(long userID, String title, String url, String screenshot) {
        EntityManager em = lb.get();
        List<Designs> designs = em.createNamedQuery("Designs.findAll").getResultList();

        long id;

        if (!designs.isEmpty()) {
            id = designs.get(designs.size() - 1).getId() + 1;
        } else {
            id = 1;
        }

        Designs d = new Designs();
        d.setId(id);
        d.setUserId(userID);
        d.setTitle(title);
        d.setUrl(url);
        d.setImageUrl(screenshot);

        lb.persist(d);
    }

    @Override
    public Designs findDesignByDesignId(long designID) {
        EntityManager em = lb.get();

        Designs d = (Designs) em.createNamedQuery("Designs.findById").setParameter("id", designID).getResultList().get(0);
        return d;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void deleteDesign(long designID) {
        EntityManager em = lb.get();

        Designs d = (Designs) em.createNamedQuery("Designs.findById").setParameter("id", designID).getResultList().get(0);

        Collection<Comments> comments = em.createNamedQuery("Comments.findByDesignId").setParameter("designId", designID).getResultList();

        for (Comments c : comments) {
            lb.remove(c);
        }

        if (d != null) {
            lb.remove(d);
        }
    }
}
