package com.eai.beans.session;

import com.eai.beans.UserBean;
import com.eai.beans.entity.Users;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author Tim Church
 */
@Local
public interface UserSessionLocal {

    Collection<Users> findAllUsers();

    Users authenticate(String username, String password);

    Users createUser(UserBean user);

    Users updateUser(UserBean updatedUser);

    boolean deleteUser(long deleteUser);
}
