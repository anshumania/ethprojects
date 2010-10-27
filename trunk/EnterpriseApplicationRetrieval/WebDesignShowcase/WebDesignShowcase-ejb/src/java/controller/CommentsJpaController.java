package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import entity.CommentsE;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
//import entity.UserE ;
import entity.DesignsEE;

/**
 *
 * @author Tim Church
 */
public class CommentsJpaController {
//
//    public CommentsJpaController() {
//        emf = Persistence.createEntityManagerFactory("WebDesignShowcase-ejbPU");
//    }
//    private EntityManagerFactory emf = null;
//
//    public EntityManager getEntityManager() {
//        return emf.createEntityManager();
//    }
//
//    public void create(CommentsE comments) throws PreexistingEntityException, Exception {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            Userss users = comments.getUsers();
//            if (users != null) {
//                users = em.getReference(users.getClass(), users.getId());
//                comments.setUsers(users);
//            }
//            Designss designs = comments.getDesigns();
//            if (designs != null) {
//                designs = em.getReference(designs.getClass(), designs.getId());
//                comments.setDesigns(designs);
//            }
//            em.persist(comments);
//            if (users != null) {
//                users.getCommentsCollection().add(comments);
//                users = em.merge(users);
//            }
//            if (designs != null) {
//                designs.getCommentsCollection().add(comments);
//                designs = em.merge(designs);
//            }
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            if (findComments(comments.getId()) != null) {
//                throw new PreexistingEntityException("Comments " + comments + " already exists.", ex);
//            }
//            throw ex;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public void edit(CommentsE comments) throws NonexistentEntityException, Exception {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            CommentsE persistentComments = em.find(CommentsE.class, comments.getId());
//            Userss usersOld = persistentComments.getUsers();
//            Userss usersNew = comments.getUsers();
//            Designss designsOld = persistentComments.getDesigns();
//            Designss designsNew = comments.getDesigns();
//            if (usersNew != null) {
//                usersNew = em.getReference(usersNew.getClass(), usersNew.getId());
//                comments.setUsers(usersNew);
//            }
//            if (designsNew != null) {
//                designsNew = em.getReference(designsNew.getClass(), designsNew.getId());
//                comments.setDesigns(designsNew);
//            }
//            comments = em.merge(comments);
//            if (usersOld != null && !usersOld.equals(usersNew)) {
//                usersOld.getCommentsCollection().remove(comments);
//                usersOld = em.merge(usersOld);
//            }
//            if (usersNew != null && !usersNew.equals(usersOld)) {
//                usersNew.getCommentsCollection().add(comments);
//                usersNew = em.merge(usersNew);
//            }
//            if (designsOld != null && !designsOld.equals(designsNew)) {
//                designsOld.getCommentsCollection().remove(comments);
//                designsOld = em.merge(designsOld);
//            }
//            if (designsNew != null && !designsNew.equals(designsOld)) {
//                designsNew.getCommentsCollection().add(comments);
//                designsNew = em.merge(designsNew);
//            }
//            em.getTransaction().commit();
//        } catch (Exception ex) {
//            String msg = ex.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                Integer id = comments.getId();
//                if (findComments(id) == null) {
//                    throw new NonexistentEntityException("The comments with id " + id + " no longer exists.");
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
//    public void destroy(Integer id) throws NonexistentEntityException {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            CommentsE comments;
//            try {
//                comments = em.getReference(CommentsE.class, id);
//                comments.getId();
//            } catch (EntityNotFoundException enfe) {
//                throw new NonexistentEntityException("The comments with id " + id + " no longer exists.", enfe);
//            }
//            Userss users = comments.getUsers();
//            if (users != null) {
//                users.getCommentsCollection().remove(comments);
//                users = em.merge(users);
//            }
//            Designss designs = comments.getDesigns();
//            if (designs != null) {
//                designs.getCommentsCollection().remove(comments);
//                designs = em.merge(designs);
//            }
//            em.remove(comments);
//            em.getTransaction().commit();
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public List<CommentsE> findCommentsEntities() {
//        return findCommentsEntities(true, -1, -1);
//    }
//
//    public List<CommentsE> findCommentsEntities(int maxResults, int firstResult) {
//        return findCommentsEntities(false, maxResults, firstResult);
//    }
//
//    private List<CommentsE> findCommentsEntities(boolean all, int maxResults, int firstResult) {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            cq.select(cq.from(CommentsE.class));
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
//    public CommentsE findComments(Integer id) {
//        EntityManager em = getEntityManager();
//        try {
//            return em.find(CommentsE.class, id);
//        } finally {
//            em.close();
//        }
//    }
//
//    public int getCommentsCount() {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            Root<CommentsE> rt = cq.from(CommentsE.class);
//            cq.select(em.getCriteriaBuilder().count(rt));
//            Query q = em.createQuery(cq);
//            return ((Long) q.getSingleResult()).intValue();
//        } finally {
//            em.close();
//        }
//    }

}
