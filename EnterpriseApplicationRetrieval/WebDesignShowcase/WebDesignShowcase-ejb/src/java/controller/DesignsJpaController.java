package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
//import entity.Designss;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
//import entity.Userss;
import entity.CommentsE;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Tim Church
 */
public class DesignsJpaController {

//    public DesignsJpaController() {
//        emf = Persistence.createEntityManagerFactory("WebDesignShowcase-ejbPU");
//    }
//    private EntityManagerFactory emf = null;
//
//    public EntityManager getEntityManager() {
//        return emf.createEntityManager();
//    }
//
//    public void create(Designss designs) throws PreexistingEntityException, Exception {
//        if (designs.getCommentsCollection() == null) {
//            designs.setCommentsCollection(new ArrayList<CommentsE>());
//        }
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Userss users = designs.getUsers();
//            if (users != null) {
//                users = em.getReference(users.getClass(), users.getId());
//                designs.setUsers(users);
//            }
//            Collection<CommentsE> attachedCommentsCollection = new ArrayList<CommentsE>();
//            for (CommentsE commentsCollectionCommentsToAttach : designs.getCommentsCollection()) {
//                commentsCollectionCommentsToAttach = em.getReference(commentsCollectionCommentsToAttach.getClass(), commentsCollectionCommentsToAttach.getId());
//                attachedCommentsCollection.add(commentsCollectionCommentsToAttach);
//            }
//            designs.setCommentsCollection(attachedCommentsCollection);
//            em.persist(designs);
//            if (users != null) {
//                users.getDesignsCollection().add(designs);
//                users = em.merge(users);
//            }
//            for (CommentsE commentsCollectionComments : designs.getCommentsCollection()) {
//                Designss oldDesignsOfCommentsCollectionComments = commentsCollectionComments.getDesigns();
//                commentsCollectionComments.setDesigns(designs);
//                commentsCollectionComments = em.merge(commentsCollectionComments);
//                if (oldDesignsOfCommentsCollectionComments != null) {
//                    oldDesignsOfCommentsCollectionComments.getCommentsCollection().remove(commentsCollectionComments);
//                    oldDesignsOfCommentsCollectionComments = em.merge(oldDesignsOfCommentsCollectionComments);
//                }
//            }
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            if (findDesigns(designs.getId()) != null) {
//                throw new PreexistingEntityException("Designs " + designs + " already exists.", ex);
//            }
//            throw ex;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public void edit(Designss designs) throws IllegalOrphanException, NonexistentEntityException, Exception {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Designss persistentDesigns = em.find(Designss.class, designs.getId());
//            Userss usersOld = persistentDesigns.getUsers();
//            Userss usersNew = designs.getUsers();
//            Collection<CommentsE> commentsCollectionOld = persistentDesigns.getCommentsCollection();
//            Collection<CommentsE> commentsCollectionNew = designs.getCommentsCollection();
//            List<String> illegalOrphanMessages = null;
//            for (CommentsE commentsCollectionOldComments : commentsCollectionOld) {
//                if (!commentsCollectionNew.contains(commentsCollectionOldComments)) {
//                    if (illegalOrphanMessages == null) {
//                        illegalOrphanMessages = new ArrayList<String>();
//                    }
//                    illegalOrphanMessages.add("You must retain Comments " + commentsCollectionOldComments + " since its designs field is not nullable.");
//                }
//            }
//            if (illegalOrphanMessages != null) {
//                throw new IllegalOrphanException(illegalOrphanMessages);
//            }
//            if (usersNew != null) {
//                usersNew = em.getReference(usersNew.getClass(), usersNew.getId());
//                designs.setUsers(usersNew);
//            }
//            Collection<CommentsE> attachedCommentsCollectionNew = new ArrayList<CommentsE>();
//            for (CommentsE commentsCollectionNewCommentsToAttach : commentsCollectionNew) {
//                commentsCollectionNewCommentsToAttach = em.getReference(commentsCollectionNewCommentsToAttach.getClass(), commentsCollectionNewCommentsToAttach.getId());
//                attachedCommentsCollectionNew.add(commentsCollectionNewCommentsToAttach);
//            }
//            commentsCollectionNew = attachedCommentsCollectionNew;
//            designs.setCommentsCollection(commentsCollectionNew);
//            designs = em.merge(designs);
//            if (usersOld != null && !usersOld.equals(usersNew)) {
//                usersOld.getDesignsCollection().remove(designs);
//                usersOld = em.merge(usersOld);
//            }
//            if (usersNew != null && !usersNew.equals(usersOld)) {
//                usersNew.getDesignsCollection().add(designs);
//                usersNew = em.merge(usersNew);
//            }
//            for (CommentsE commentsCollectionNewComments : commentsCollectionNew) {
//                if (!commentsCollectionOld.contains(commentsCollectionNewComments)) {
//                    Designss oldDesignsOfCommentsCollectionNewComments = commentsCollectionNewComments.getDesigns();
//                    commentsCollectionNewComments.setDesigns(designs);
//                    commentsCollectionNewComments = em.merge(commentsCollectionNewComments);
//                    if (oldDesignsOfCommentsCollectionNewComments != null && !oldDesignsOfCommentsCollectionNewComments.equals(designs)) {
//                        oldDesignsOfCommentsCollectionNewComments.getCommentsCollection().remove(commentsCollectionNewComments);
//                        oldDesignsOfCommentsCollectionNewComments = em.merge(oldDesignsOfCommentsCollectionNewComments);
//                    }
//                }
//            }
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            String msg = ex.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                Integer id = designs.getId();
//                if (findDesigns(id) == null) {
//                    throw new NonexistentEntityException("The designs with id " + id + " no longer exists.");
//                }
//            }
//            throw ex;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Designss designs;
//            try {
//                designs = em.getReference(Designss.class, id);
//                designs.getId();
//            } catch (EntityNotFoundException enfe) {
//                throw new NonexistentEntityException("The designs with id " + id + " no longer exists.", enfe);
//            }
//            List<String> illegalOrphanMessages = null;
//            Collection<CommentsE> commentsCollectionOrphanCheck = designs.getCommentsCollection();
//            for (CommentsE commentsCollectionOrphanCheckComments : commentsCollectionOrphanCheck) {
//                if (illegalOrphanMessages == null) {
//                    illegalOrphanMessages = new ArrayList<String>();
//                }
//                illegalOrphanMessages.add("This Designs (" + designs + ") cannot be destroyed since the Comments " + commentsCollectionOrphanCheckComments + " in its commentsCollection field has a non-nullable designs field.");
//            }
//            if (illegalOrphanMessages != null) {
//                throw new IllegalOrphanException(illegalOrphanMessages);
//            }
//            Userss users = designs.getUsers();
//            if (users != null) {
//                users.getDesignsCollection().remove(designs);
//                users = em.merge(users);
//            }
//            em.remove(designs);
//            em.getTransaction().commit();
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public List<Designss> findDesignsEntities() {
//        return findDesignsEntities(true, -1, -1);
//    }
//
//    public List<Designss> findDesignsEntities(int maxResults, int firstResult) {
//        return findDesignsEntities(false, maxResults, firstResult);
//    }
//
//    private List<Designss> findDesignsEntities(boolean all, int maxResults, int firstResult) {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            cq.select(cq.from(Designss.class));
//            Query q = em.createQuery(cq);
//            if (!all) {
//                q.setMaxResults(maxResults);
//                q.setFirstResult(firstResult);
//            }
//            return q.getResultList();
//        } finally {
//            em.close();
//        }
//    }
//
//    public Designss findDesigns(Integer id) {
//        EntityManager em = getEntityManager();
//        try {
//            return em.find(Designss.class, id);
//        } finally {
//            em.close();
//        }
//    }
//
//    public int getDesignsCount() {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            Root<Designss> rt = cq.from(Designss.class);
//            cq.select(em.getCriteriaBuilder().count(rt));
//            Query q = em.createQuery(cq);
//            return ((Long) q.getSingleResult()).intValue();
//        } finally {
//            em.close();
//        }
//    }

}
