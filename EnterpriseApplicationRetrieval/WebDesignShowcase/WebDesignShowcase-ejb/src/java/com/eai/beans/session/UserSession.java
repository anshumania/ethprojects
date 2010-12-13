package com.eai.beans.session;

import com.eai.beans.UserBean;
import com.eai.beans.entity.Users;
import com.eai.loadbalancer.LB;
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
        EntityManager em = LB.get();
        List<Users> users = em.createNamedQuery("Users.findAll")
				.getResultList();

		long id;

		if (!users.isEmpty()) {
			id = users.get(users.size()-1).getId() + 1;
		} else {
			id = 1;
		}

        Users u = new Users(id,
                            user.getUsername(),
                            user.getPassword(),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail());

        LB.persist(u);
        return u;
    }

     @Override
    public Users updateUser(UserBean updatedUser) {
        EntityManager em = entityManagerEai.createEntityManager();
        Users user = (Users)em.createNamedQuery("Users.findByUsername").setParameter("username", updatedUser.getUsername())
				.getSingleResult();

        // any of the possible values which can be update
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setPassword(updatedUser.getPassword());

        em.persist(user);

        return user;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public boolean deleteUser(long deleteUser) {
        EntityManager em = entityManagerEai.createEntityManager();
        Users user = (Users)em.createNamedQuery("Users.findById").setParameter("id", deleteUser).getSingleResult();
        em.remove(user);
        return true;
    }
 
}
