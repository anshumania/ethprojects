package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.Designs;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Users;
import entity.Comments;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Tim Church
 */
public class DesignsJpaController {

    public DesignsJpaController() {
        emf = Persistence.createEntityManagerFactory("WebDesignShowcase-ejbPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Designs designs) throws PreexistingEntityException, Exception {
        if (designs.getCommentsCollection() == null) {
            designs.setCommentsCollection(new ArrayList<Comments>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users users = designs.getUsers();
            if (users != null) {
                users = em.getReference(users.getClass(), users.getId());
                designs.setUsers(users);
            }
            Collection<Comments> attachedCommentsCollection = new ArrayList<Comments>();
            for (Comments commentsCollectionCommentsToAttach : designs.getCommentsCollection()) {
                commentsCollectionCommentsToAttach = em.getReference(commentsCollectionCommentsToAttach.getClass(), commentsCollectionCommentsToAttach.getId());
                attachedCommentsCollection.add(commentsCollectionCommentsToAttach);
            }
            designs.setCommentsCollection(attachedCommentsCollection);
            em.persist(designs);
            if (users != null) {
                users.getDesignsCollection().add(designs);
                users = em.merge(users);
            }
            for (Comments commentsCollectionComments : designs.getCommentsCollection()) {
                Designs oldDesignsOfCommentsCollectionComments = commentsCollectionComments.getDesigns();
                commentsCollectionComments.setDesigns(designs);
                commentsCollectionComments = em.merge(commentsCollectionComments);
                if (oldDesignsOfCommentsCollectionComments != null) {
                    oldDesignsOfCommentsCollectionComments.getCommentsCollection().remove(commentsCollectionComments);
                    oldDesignsOfCommentsCollectionComments = em.merge(oldDesignsOfCommentsCollectionComments);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDesigns(designs.getId()) != null) {
                throw new PreexistingEntityException("Designs " + designs + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Designs designs) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Designs persistentDesigns = em.find(Designs.class, designs.getId());
            Users usersOld = persistentDesigns.getUsers();
            Users usersNew = designs.getUsers();
            Collection<Comments> commentsCollectionOld = persistentDesigns.getCommentsCollection();
            Collection<Comments> commentsCollectionNew = designs.getCommentsCollection();
            List<String> illegalOrphanMessages = null;
            for (Comments commentsCollectionOldComments : commentsCollectionOld) {
                if (!commentsCollectionNew.contains(commentsCollectionOldComments)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comments " + commentsCollectionOldComments + " since its designs field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usersNew != null) {
                usersNew = em.getReference(usersNew.getClass(), usersNew.getId());
                designs.setUsers(usersNew);
            }
            Collection<Comments> attachedCommentsCollectionNew = new ArrayList<Comments>();
            for (Comments commentsCollectionNewCommentsToAttach : commentsCollectionNew) {
                commentsCollectionNewCommentsToAttach = em.getReference(commentsCollectionNewCommentsToAttach.getClass(), commentsCollectionNewCommentsToAttach.getId());
                attachedCommentsCollectionNew.add(commentsCollectionNewCommentsToAttach);
            }
            commentsCollectionNew = attachedCommentsCollectionNew;
            designs.setCommentsCollection(commentsCollectionNew);
            designs = em.merge(designs);
            if (usersOld != null && !usersOld.equals(usersNew)) {
                usersOld.getDesignsCollection().remove(designs);
                usersOld = em.merge(usersOld);
            }
            if (usersNew != null && !usersNew.equals(usersOld)) {
                usersNew.getDesignsCollection().add(designs);
                usersNew = em.merge(usersNew);
            }
            for (Comments commentsCollectionNewComments : commentsCollectionNew) {
                if (!commentsCollectionOld.contains(commentsCollectionNewComments)) {
                    Designs oldDesignsOfCommentsCollectionNewComments = commentsCollectionNewComments.getDesigns();
                    commentsCollectionNewComments.setDesigns(designs);
                    commentsCollectionNewComments = em.merge(commentsCollectionNewComments);
                    if (oldDesignsOfCommentsCollectionNewComments != null && !oldDesignsOfCommentsCollectionNewComments.equals(designs)) {
                        oldDesignsOfCommentsCollectionNewComments.getCommentsCollection().remove(commentsCollectionNewComments);
                        oldDesignsOfCommentsCollectionNewComments = em.merge(oldDesignsOfCommentsCollectionNewComments);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = designs.getId();
                if (findDesigns(id) == null) {
                    throw new NonexistentEntityException("The designs with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Designs designs;
            try {
                designs = em.getReference(Designs.class, id);
                designs.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The designs with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Comments> commentsCollectionOrphanCheck = designs.getCommentsCollection();
            for (Comments commentsCollectionOrphanCheckComments : commentsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Designs (" + designs + ") cannot be destroyed since the Comments " + commentsCollectionOrphanCheckComments + " in its commentsCollection field has a non-nullable designs field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Users users = designs.getUsers();
            if (users != null) {
                users.getDesignsCollection().remove(designs);
                users = em.merge(users);
            }
            em.remove(designs);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Designs> findDesignsEntities() {
        return findDesignsEntities(true, -1, -1);
    }

    public List<Designs> findDesignsEntities(int maxResults, int firstResult) {
        return findDesignsEntities(false, maxResults, firstResult);
    }

    private List<Designs> findDesignsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Designs.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Designs findDesigns(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Designs.class, id);
        } finally {
            em.close();
        }
    }

    public int getDesignsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Designs> rt = cq.from(Designs.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
