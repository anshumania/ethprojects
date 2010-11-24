package com.eai.beans.session;

import com.eai.beans.CommentBean;
import com.eai.beans.entity.Comments;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ANSHUMAN
 */
@Stateless
@TransactionManagement(value=TransactionManagementType.CONTAINER)
public class CommentSession implements CommentSessionLocal {

    @Resource(mappedName = "Comments")
    private Topic comments;
    @Resource(name = "TopicConnectionFactory")
    private ConnectionFactory topicConnectionFactory;
    @PersistenceUnit(unitName = "eai")
    EntityManagerFactory entityManagerEai;

    @Override
    public Collection<Comments> findAllComments() {
        EntityManager em = entityManagerEai.createEntityManager();
        Collection<Comments> c = em
				.createNamedQuery("Comments.findAll")
				.getResultList();
        
        return c;
    }

	@Override
        public Collection<Comments> findCommentsByUserIdAndDesignId(long userID, long designID) {

            EntityManager em = entityManagerEai.createEntityManager();
		Collection<Comments> c = em
				.createNamedQuery("Comments.findByUserIdAndDesignId")
				.setParameter("userId", userID)
				.setParameter("designId", designID)
				.getResultList();
		return c;
	}

        @Override
        public Collection<Comments> findCommentsByDesignId(long designID) {

            EntityManager em = entityManagerEai.createEntityManager();
		Collection<Comments> c = em
				.createNamedQuery("Comments.findByDesignId")
				.setParameter("designId", designID)
				.getResultList();
		return c;
	}

    @Override
    public void addComment(CommentBean comment) {
        EntityManager em = entityManagerEai.createEntityManager();
        List<Comments> commentList = em.createNamedQuery("Comments.findAll")
				.getResultList();

		long id = commentList.get(commentList.size()-1).getId() + 1;

        Comments c = new Comments();
        c.setId(id);
        c.setComment(comment.getComment());
        c.setDesignId(comment.getDesignId());
        c.setUserId(comment.getUserId());

        em.persist(c);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void deleteCommentsForADesign(long designID) {
        
        EntityManager em = entityManagerEai.createEntityManager();
	Collection<Comments> comments = em
				.createNamedQuery("Comments.findByDesignId")
				.setParameter("designId", designID)
				.getResultList();

              for (Comments c : comments) {
			em.remove(c);
		}
      }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void deleteCommentsOfAUser(long userID)
    {
        EntityManager em = entityManagerEai.createEntityManager();
	Collection<Comments> comments = em
				.createNamedQuery("Comments.findByUserId")
				.setParameter("userId", userID)
				.getResultList();

              for (Comments c : comments) {
			em.remove(c);
		}
    }

    @Override
    public void notifySubscribers(CommentBean comment) {
        try {
            sendJMSMessageToComments(comment);
        } catch (JMSException ex) {
            Logger.getLogger(CommentSession.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Message createJMSMessageForcomments(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        ObjectMessage om = session.createObjectMessage();
        om.setObject((CommentBean) messageData);
        return om;
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
