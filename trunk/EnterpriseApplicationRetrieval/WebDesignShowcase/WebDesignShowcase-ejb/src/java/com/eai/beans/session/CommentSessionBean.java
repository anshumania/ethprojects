/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.beans.session;

import com.eai.beans.CommentBean;
import com.eai.beans.entity.Comments;
import com.eai.beans.entity.Users;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ANSHUMAN
 */
@Stateless
public class CommentSessionBean implements CommentSessionBeanLocal {
    @Resource(name = "Comments")
    private Topic comments;
    @Resource(name = "TopicConnectionFactory")
    private ConnectionFactory topicConnectionFactory;
    



    @PersistenceUnit(unitName = "eai")
    EntityManagerFactory entityManagerEai;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")



    @Override
    public void findAllUsers() {

        Collection<Users> users = entityManagerEai.createEntityManager().createNamedQuery("Users.findAll").getResultList();
        System.out.println("result.size" + users.size());
        for(Users user : users)
        {
            System.out.println("user"+user.getFirstname());
        }
    }

    @Override
    public void addComment(CommentBean comment){

        EntityManager em = entityManagerEai.createEntityManager();
        Comments lastComment = (Comments) em.createNamedQuery("Comments.getLatestComment").getResultList().get(0);;

        Comments c = new Comments();
        c.setId(lastComment.getId()+1);
        c.setComment(comment.getComment());
        c.setDesignId(comment.getDesignId());
        c.setUserId(comment.getUserId());

        em.persist(c);

    }



    @Override
    public void notifySubscribers(CommentBean comment) {
        try {
            sendJMSMessageToComments(comment);
        } catch (JMSException ex) {
            Logger.getLogger(CommentSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Message createJMSMessageForcomments(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }

    private void sendJMSMessageToComments(Object messageData) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            connection = topicConnectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(comments);
            messageProducer.send(createJMSMessageForcomments(session, messageData));
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    
}
