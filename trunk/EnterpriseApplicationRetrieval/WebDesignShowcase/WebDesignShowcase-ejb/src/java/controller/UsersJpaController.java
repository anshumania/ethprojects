package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
//import entity.Userss;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
//import entity.Designss;
import java.util.ArrayList;
import java.util.Collection;
import entity.CommentsE;

/**
 *
 * @author Tim Church
 */
public class UsersJpaController {

//    public UsersJpaController() {
//        emf = Persistence.createEntityManagerFactory("WebDesignShowcase-ejbPU");
//    }
//    private EntityManagerFactory emf = null;
//
//    public EntityManager getEntityManager() {
//        return emf.createEntityManager();
//    }
//
//    public void create(Userss users) throws PreexistingEntityException, Exception {
//        if (users.getDesignsCollection() == null) {
//            users.setDesignsCollection(new ArrayList<Designss>());
//        }
//        if (users.getCommentsCollection() == null) {
//            users.setCommentsCollection(new ArrayList<CommentsE>());
//        }
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Collection<Designss> attachedDesignsCollection = new ArrayList<Designss>();
//            for (Designss designsCollectionDesignsToAttach : users.getDesignsCollection()) {
//                designsCollectionDesignsToAttach = em.getReference(designsCollectionDesignsToAttach.getClass(), designsCollectionDesignsToAttach.getId());
//                attachedDesignsCollection.add(designsCollectionDesignsToAttach);
//            }
//            users.setDesignsCollection(attachedDesignsCollection);
//            Collection<CommentsE> attachedCommentsCollection = new ArrayList<CommentsE>();
//            for (CommentsE commentsCollectionCommentsToAttach : users.getCommentsCollection()) {
//                commentsCollectionCommentsToAttach = em.getReference(commentsCollectionCommentsToAttach.getClass(), commentsCollectionCommentsToAttach.getId());
//                attachedCommentsCollection.add(commentsCollectionCommentsToAttach);
//            }
//            users.setCommentsCollection(attachedCommentsCollection);
//            em.persist(users);
//            for (Designss designsCollectionDesigns : users.getDesignsCollection()) {
//                Userss oldUsersOfDesignsCollectionDesigns = designsCollectionDesigns.getUsers();
//                designsCollectionDesigns.setUsers(users);
//                designsCollectionDesigns = em.merge(designsCollectionDesigns);
//                if (oldUsersOfDesignsCollectionDesigns != null) {
//                    oldUsersOfDesignsCollectionDesigns.getDesignsCollection().remove(designsCollectionDesigns);
//                    oldUsersOfDesignsCollectionDesigns = em.merge(oldUsersOfDesignsCollectionDesigns);
//                }
//            }
//            for (CommentsE commentsCollectionComments : users.getCommentsCollection()) {
//                Userss oldUsersOfCommentsCollectionComments = commentsCollectionComments.getUsers();
//                commentsCollectionComments.setUsers(users);
//                commentsCollectionComments = em.merge(commentsCollectionComments);
//                if (oldUsersOfCommentsCollectionComments != null) {
//                    oldUsersOfCommentsCollectionComments.getCommentsCollection().remove(commentsCollectionComments);
//                    oldUsersOfCommentsCollectionComments = em.merge(oldUsersOfCommentsCollectionComments);
//                }
//            }
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            if (findUsers(users.getId()) != null) {
//                throw new PreexistingEntityException("Users " + users + " already exists.", ex);
//            }
//            throw ex;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public void edit(Userss users) throws IllegalOrphanException, NonexistentEntityException, Exception {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Userss persistentUsers = em.find(Userss.class, users.getId());
//            Collection<Designss> designsCollectionOld = persistentUsers.getDesignsCollection();
//            Collection<Designss> designsCollectionNew = users.getDesignsCollection();
//            Collection<CommentsE> commentsCollectionOld = persistentUsers.getCommentsCollection();
//            Collection<CommentsE> commentsCollectionNew = users.getCommentsCollection();
//            List<String> illegalOrphanMessages = null;
//            for (Designss designsCollectionOldDesigns : designsCollectionOld) {
//                if (!designsCollectionNew.contains(designsCollectionOldDesigns)) {
//                    if (illegalOrphanMessages == null) {
//                        illegalOrphanMessages = new ArrayList<String>();
//                    }
//                    illegalOrphanMessages.add("You must retain Designs " + designsCollectionOldDesigns + " since its users field is not nullable.");
//                }
//            }
//            for (CommentsE commentsCollectionOldComments : commentsCollectionOld) {
//                if (!commentsCollectionNew.contains(commentsCollectionOldComments)) {
//                    if (illegalOrphanMessages == null) {
//                        illegalOrphanMessages = new ArrayList<String>();
//                    }
//                    illegalOrphanMessages.add("You must retain Comments " + commentsCollectionOldComments + " since its users field is not nullable.");
//                }
//            }
//            if (illegalOrphanMessages != null) {
//                throw new IllegalOrphanException(illegalOrphanMessages);
//            }
//            Collection<Designss> attachedDesignsCollectionNew = new ArrayList<Designss>();
//            for (Designss designsCollectionNewDesignsToAttach : designsCollectionNew) {
//                designsCollectionNewDesignsToAttach = em.getReference(designsCollectionNewDesignsToAttach.getClass(), designsCollectionNewDesignsToAttach.getId());
//                attachedDesignsCollectionNew.add(designsCollectionNewDesignsToAttach);
//            }
//            designsCollectionNew = attachedDesignsCollectionNew;
//            users.setDesignsCollection(designsCollectionNew);
//            Collection<CommentsE> attachedCommentsCollectionNew = new ArrayList<CommentsE>();
//            for (CommentsE commentsCollectionNewCommentsToAttach : commentsCollectionNew) {
//                commentsCollectionNewCommentsToAttach = em.getReference(commentsCollectionNewCommentsToAttach.getClass(), commentsCollectionNewCommentsToAttach.getId());
//                attachedCommentsCollectionNew.add(commentsCollectionNewCommentsToAttach);
//            }
//            commentsCollectionNew = attachedCommentsCollectionNew;
//            users.setCommentsCollection(commentsCollectionNew);
//            users = em.merge(users);
//            for (Designss designsCollectionNewDesigns : designsCollectionNew) {
//                if (!designsCollectionOld.contains(designsCollectionNewDesigns)) {
//                    Userss oldUsersOfDesignsCollectionNewDesigns = designsCollectionNewDesigns.getUsers();
//                    designsCollectionNewDesigns.setUsers(users);
//                    designsCollectionNewDesigns = em.merge(designsCollectionNewDesigns);
//                    if (oldUsersOfDesignsCollectionNewDesigns != null && !oldUsersOfDesignsCollectionNewDesigns.equals(users)) {
//                        oldUsersOfDesignsCollectionNewDesigns.getDesignsCollection().remove(designsCollectionNewDesigns);
//                        oldUsersOfDesignsCollectionNewDesigns = em.merge(oldUsersOfDesignsCollectionNewDesigns);
//                    }
//                }
//            }
//            for (CommentsE commentsCollectionNewComments : commentsCollectionNew) {
//                if (!commentsCollectionOld.contains(commentsCollectionNewComments)) {
//                    Userss oldUsersOfCommentsCollectionNewComments = commentsCollectionNewComments.getUsers();
//                    commentsCollectionNewComments.setUsers(users);
//                    commentsCollectionNewComments = em.merge(commentsCollectionNewComments);
//                    if (oldUsersOfCommentsCollectionNewComments != null && !oldUsersOfCommentsCollectionNewComments.equals(users)) {
//                        oldUsersOfCommentsCollectionNewComments.getCommentsCollection().remove(commentsCollectionNewComments);
//                        oldUsersOfCommentsCollectionNewComments = em.merge(oldUsersOfCommentsCollectionNewComments);
//                    }
//                }
//            }
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            String msg = ex.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                Integer id = users.getId();
//                if (findUsers(id) == null) {
//                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
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
//            Userss users;
//            try {
//                users = em.getReference(Userss.class, id);
//                users.getId();
//            } catch (EntityNotFoundException enfe) {
//                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
//            }
//            List<String> illegalOrphanMessages = null;
//            Collection<Designss> designsCollectionOrphanCheck = users.getDesignsCollection();
//            for (Designss designsCollectionOrphanCheckDesigns : designsCollectionOrphanCheck) {
//                if (illegalOrphanMessages == null) {
//                    illegalOrphanMessages = new ArrayList<String>();
//                }
//                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Designs " + designsCollectionOrphanCheckDesigns + " in its designsCollection field has a non-nullable users field.");
//            }
//            Collection<CommentsE> commentsCollectionOrphanCheck = users.getCommentsCollection();
//            for (CommentsE commentsCollectionOrphanCheckComments : commentsCollectionOrphanCheck) {
//                if (illegalOrphanMessages == null) {
//                    illegalOrphanMessages = new ArrayList<String>();
//                }
//                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Comments " + commentsCollectionOrphanCheckComments + " in its commentsCollection field has a non-nullable users field.");
//            }
//            if (illegalOrphanMessages != null) {
//                throw new IllegalOrphanException(illegalOrphanMessages);
//            }
//            em.remove(users);
//            em.getTransaction().commit();
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public List<Userss> findUsersEntities() {
//        return findUsersEntities(true, -1, -1);
//    }
//
//    public List<Userss> findUsersEntities(int maxResults, int firstResult) {
//        return findUsersEntities(false, maxResults, firstResult);
//    }
//
//    private List<Userss> findUsersEntities(boolean all, int maxResults, int firstResult) {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            cq.select(cq.from(Userss.class));
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
//    public Userss findUsers(Integer id) {
//        EntityManager em = getEntityManager();
//        try {
//            return em.find(Userss.class, id);
//        } finally {
//            em.close();
//        }
//    }
//
//    public int getUsersCount() {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            Root<Userss> rt = cq.from(Userss.class);
//            cq.select(em.getCriteriaBuilder().count(rt));
//            Query q = em.createQuery(cq);
//            return ((Long) q.getSingleResult()).intValue();
//        } finally {
//            em.close();
//        }
//    }

}
