/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.beans.session;

import com.eai.beans.CommentBean;
import com.eai.beans.entity.Comments;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface CommentSessionLocal {

    public Collection<Comments> findAllComments();

	public Collection<Comments> findCommentsByUserIdAndDesignId(long userID, int designID);

    public void notifySubscribers(CommentBean comment);

    public void addComment(CommentBean comment);
}
