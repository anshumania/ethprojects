package com.eai.beans.session;

import com.eai.beans.UserBean;
import com.eai.beans.entity.Users;
import javax.ejb.Local;

/**
 *
 * @author Tim Church
 */
@Local
public interface UserSessionLocal {

    Users authenticate(String username, String password);

    Users createUser(UserBean user);
}
