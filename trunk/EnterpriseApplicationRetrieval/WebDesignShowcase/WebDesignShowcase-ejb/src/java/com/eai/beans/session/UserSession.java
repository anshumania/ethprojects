package com.eai.beans.session;

import com.eai.beans.UserBean;
import com.eai.beans.entity.Users;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author Tim Church
 */
@Stateless
public class UserSession implements UserSessionLocal {

    @PersistenceUnit(unitName = "eai")
    EntityManagerFactory entityManagerEai;

    @Override
    public Users authenticate(String username, String password) {
        Collection<Users> users = entityManagerEai.createEntityManager().createNamedQuery("Users.findByUsernameAndPassword").setParameter("username", username).setParameter("password", password).getResultList();
        if(users.size() == 1) {
             return users.iterator().next();
        }
        return null;
    }

    @Override
    public Users createUser(UserBean user) {
        EntityManager em = entityManagerEai.createEntityManager();
        long userCount = em.createNamedQuery("Users.findAll").getResultList().size();

        Users u = new Users(userCount+1, 
                            user.getUsername(),
                            user.getPassword(),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail());

        em.persist(u);
        return u;
    }
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
