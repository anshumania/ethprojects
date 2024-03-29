package com.eai.beans.session;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author Tim Church
 */
@Singleton
@TransactionManagement(value = TransactionManagementType.BEAN)
public class LB implements LBLocal {

    @PersistenceUnit(unitName = "eai")
    EntityManagerFactory entityManagerEai1;
    @PersistenceUnit(unitName = "eai2")
    EntityManagerFactory entityManagerEai2;
    @Resource
    UserTransaction utx;
    static int roundRobin = 0;

    @Override
    public synchronized EntityManager get() {
        EntityManager result = null;

        if (roundRobin == 0) {
            result = entityManagerEai1.createEntityManager();
        } else {
            result = entityManagerEai2.createEntityManager();
        }

        roundRobin = (roundRobin + 1) % 2;

        return result;
    }

    @Override
    public void persist(Object entity) {
        try {
            EntityManager em1;
            EntityManager em2;
            utx.begin();
            em1 = entityManagerEai1.createEntityManager();
            em1.persist(entity);
            em2 = entityManagerEai2.createEntityManager();
            em2.persist(entity);
            utx.commit();
        } catch (javax.transaction.RollbackException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicMixedException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicRollbackException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.transaction.NotSupportedException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void remove(Object entity) {
        try {
            EntityManager em1;
            EntityManager em2;
            utx.begin();
            em1 = entityManagerEai1.createEntityManager();
            em1.remove(em1.merge(entity));
            em2 = entityManagerEai2.createEntityManager();
            em2.remove(em2.merge(entity));
            utx.commit();
        } catch (javax.transaction.RollbackException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicMixedException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicRollbackException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.transaction.NotSupportedException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void merge(Object entity) {
        try {
            EntityManager em1;
            EntityManager em2;
            utx.begin();
            em1 = entityManagerEai1.createEntityManager();
            em1.merge(entity);
            em2 = entityManagerEai2.createEntityManager();
            em2.merge(entity);
            utx.commit();
        } catch (javax.transaction.RollbackException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicMixedException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicRollbackException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.transaction.NotSupportedException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(LB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
