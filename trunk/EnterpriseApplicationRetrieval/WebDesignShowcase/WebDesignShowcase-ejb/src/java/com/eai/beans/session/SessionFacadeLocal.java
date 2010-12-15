/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.beans.session;

import com.eai.beans.CommentBean;
import com.eai.beans.DesignBean;
import com.eai.beans.UserBean;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface SessionFacadeLocal {

    //CommentSession
    public Collection<CommentBean> findAllComments();

    public Collection<CommentBean> findCommentsByUserIdAndDesignId(long userID, long designID);

    public Collection<CommentBean> findCommentsByDesignId(long designID);

    public void notifySubscribers(CommentBean comment);

    public void addComment(CommentBean comment);

    // DesignSession
    public Collection<DesignBean> findDesignsByUserId(long userID);

    public void addDesign(long userId, String title, String url, String screenshot);

    public Collection<DesignBean> findAllDesigns();

    public DesignBean findDesignByDesignId(long designID);

    public void deleteDesign(long designID);

    //UserSession
    public Collection<UserBean> findAllUsers();
    
    public UserBean authenticate(String username, String password);

    public UserBean createUser(UserBean user);

    public UserBean updateUser(UserBean updatedUser);

    public boolean deleteUser(UserBean deleteUser);

    public UserBean findUserById(long userID);
}
