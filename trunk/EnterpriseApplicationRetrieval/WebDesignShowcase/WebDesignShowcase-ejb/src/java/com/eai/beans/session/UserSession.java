package com.eai.beans.session;

import com.eai.beans.UserBean;
import com.eai.beans.entity.Users;
import java.util.Collection;
import java.util.List;
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
    public Collection<Users> findAllUsers() {
        Collection<Users> users = entityManagerEai.createEntityManager()
				.createNamedQuery("Users.findAll")
				.getResultList();
        return users;
    }

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
        List<Users> users = em.createNamedQuery("Users.findAll")
				.getResultList();

		long id = users.get(users.size()-1).getId() + 1;

        Users u = new Users(id,
                            user.getUsername(),
                            user.getPassword(),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail());

        em.persist(u);
        return u;
    }
 
}
