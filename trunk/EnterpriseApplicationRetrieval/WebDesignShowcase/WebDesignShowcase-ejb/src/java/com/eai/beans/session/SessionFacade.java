/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.beans.session;

import com.eai.beans.CommentBean;
import com.eai.beans.DesignBean;
import com.eai.beans.UserBean;
import com.eai.beans.entity.Comments;
import com.eai.beans.entity.Designs;
import com.eai.beans.entity.Users;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author ANSHUMAN
 */
@Singleton
@TransactionManagement(value=TransactionManagementType.BEAN)
public class SessionFacade implements SessionFacadeLocal {
    @EJB
    private CommentSessionLocal commentSession;
    @EJB
    private DesignSessionLocal designSession;
    @EJB
    private UserSessionLocal userSession;
    @Resource
    public UserTransaction utx;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    // CommentSession
    @Override
    public Collection<CommentBean> findAllComments()
    {
        Collection<Comments> comments = commentSession.findAllComments();
        Collection<CommentBean> commentBeans = new ArrayList<CommentBean>();
        for(Comments comment : comments)
        {
            CommentBean commentBean = new CommentBean();
            commentBean.setComment(comment.getComment());
            commentBean.setDesignId(comment.getDesignId());
            commentBean.setUserId(comment.getUserId());
            commentBeans.add(commentBean);
        }
        return commentBeans;
    }
    @Override
    public Collection<CommentBean> findCommentsByUserIdAndDesignId(long userID, long designID)
    {

        Collection<Comments> comments = commentSession.findCommentsByUserIdAndDesignId(userID, designID);
        Collection<CommentBean> commentBeans = new ArrayList<CommentBean>();
        for(Comments comment : comments)
        {
            CommentBean commentBean = new CommentBean();
            commentBean.setComment(comment.getComment());
            commentBean.setDesignId(comment.getDesignId());
            commentBean.setUserId(comment.getUserId());
            commentBeans.add(commentBean);
        }
        return commentBeans;
        
    }

    @Override
    public Collection<CommentBean> findCommentsByDesignId(long designID)
    {

        Collection<Comments> comments = commentSession.findCommentsByDesignId(designID);
        Collection<CommentBean> commentBeans = new ArrayList<CommentBean>();
        for(Comments comment : comments)
        {
            CommentBean commentBean = new CommentBean();
            commentBean.setComment(comment.getComment());
            commentBean.setDesignId(comment.getDesignId());
            commentBean.setUserId(comment.getUserId());
            commentBeans.add(commentBean);
        }
        return commentBeans;

    }

    @Override
    public void notifySubscribers(CommentBean comment)
    {
        commentSession.notifySubscribers(comment);
    }
    @Override
    public void addComment(CommentBean commentBean)
    {
        commentSession.addComment(commentBean);
    }

    // DesignSession
    @Override
    public Collection<DesignBean> findDesignsByUserId(long userID)
    {
        Collection<Designs> designs = designSession.findDesignsByUserId(userID);
        Collection<DesignBean> designBeans = new ArrayList<DesignBean>();
        for(Designs design : designs)
        {
            DesignBean designBean = new DesignBean();
            designBean.setId(design.getId());
            designBean.setImageUrl(design.getImageUrl());
            designBean.setTitle(design.getTitle());
            designBean.setUrl(design.getUrl());
            designBean.setUserId(design.getUserId());
            designBeans.add(designBean);
        }
        return designBeans;
    }
    @Override
    public void addDesign(long userId, String title, String url, String screenshot)
    {
        designSession.addDesign(userId, title, url, screenshot);
    }
    @Override
    public Collection<DesignBean> findAllDesigns()
    {
        Collection<Designs> designs =  designSession.findAllDesigns();
        Collection<DesignBean> designBeans = new ArrayList<DesignBean>();
        for(Designs design : designs)
        {
            DesignBean designBean = new DesignBean();
            designBean.setId(design.getId());
            designBean.setImageUrl(design.getImageUrl());
            designBean.setTitle(design.getTitle());
            designBean.setUrl(design.getUrl());
            designBean.setUserId(design.getUserId());
            designBeans.add(designBean);
        }
        return designBeans;
    }
    @Override
    public void deleteDesign(long designID)
    {
        try {
            //To delete a design first fetch all comments for that design
            utx.begin();
            commentSession.deleteCommentsForADesign(designID);
            designSession.deleteDesign(designID);
            utx.commit();
        } catch (RollbackException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicMixedException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicRollbackException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotSupportedException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


    //UserSession
    @Override
    public Collection<UserBean> findAllUsers()
    {
        Collection<Users> users =  userSession.findAllUsers();
        Collection<UserBean> userBeans = new ArrayList<UserBean>();
        for(Users user : users)
        {
            UserBean userBean = new UserBean();
            userBean.setEmail(user.getEmail());
            userBean.setFirstname(user.getFirstname());
            userBean.setLastname(user.getLastname());
            userBean.setPassword(user.getPassword());
            userBean.setUsername(user.getUsername());
            userBean.setId(user.getId());
            userBeans.add(userBean);
        }

        return userBeans;
    }
    @Override
    public UserBean authenticate(String username, String password)
    {
        Users user = userSession.authenticate(username, password);
        if(user==null) return null;
        UserBean userBean = new UserBean();
        userBean.setEmail(user.getEmail());
        userBean.setFirstname(user.getFirstname());
        userBean.setLastname(user.getLastname());
        userBean.setPassword(user.getPassword());
        userBean.setUsername(user.getUsername());
        userBean.setId(user.getId());
        return userBean;
    }
    @Override
    public UserBean createUser(UserBean userBean)
    {
        Users user = userSession.createUser(userBean);
        userBean.setEmail(user.getEmail());
        userBean.setFirstname(user.getFirstname());
        userBean.setLastname(user.getLastname());
        userBean.setPassword(user.getPassword());
        userBean.setUsername(user.getUsername());
        userBean.setId(user.getId());
        return userBean;
    }
    @Override
    public UserBean updateUser(UserBean updatedUser)
    {
        Users user = userSession.updateUser(updatedUser);
        updatedUser.setEmail(user.getEmail());
        updatedUser.setFirstname(user.getFirstname());
        updatedUser.setLastname(user.getLastname());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setUsername(user.getUsername());
        updatedUser.setId(user.getId());
        return updatedUser;
    }

    @Override
    
    public boolean deleteUser(UserBean deleteUser)
    {
        System.out.println("Deleting user");
        try {
            //To delete a user
            // delete all his comments
            // delete all comments of designs
            // delete all designs
            // delete user
            utx.begin();
            commentSession.deleteCommentsOfAUser(deleteUser.getId());
            Collection<Designs> designs = designSession.findDesignsByUserId(deleteUser.getId());
            if(designs != null || !designs.isEmpty())
            {
                for(Designs design : designs)
                {
                commentSession.deleteCommentsForADesign(design.getId());
                designSession.deleteDesign(design.getId());
                }
            }
            userSession.deleteUser(deleteUser.getId());
            utx.commit();
            
            } catch (NotSupportedException ex) {
            Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RollbackException ex) {
                Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (HeuristicMixedException ex) {
                Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (HeuristicRollbackException ex) {
                Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SystemException ex) {
                Logger.getLogger(SessionFacade.class.getName()).log(Level.SEVERE, null, ex);
            }

    System.out.println("User deleted");

        //commentSession.d
        return true;
    }



 
}
