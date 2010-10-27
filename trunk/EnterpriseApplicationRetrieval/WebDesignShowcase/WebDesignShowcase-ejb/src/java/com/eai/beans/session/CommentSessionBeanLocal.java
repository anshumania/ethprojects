/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.beans.session;

import com.eai.beans.CommentBean;
import javax.ejb.Local;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface CommentSessionBeanLocal {

    public void findAllUsers();

    public void notifySubscribers(CommentBean comment);

    public void addComment(CommentBean comment);
}
