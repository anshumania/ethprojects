package com.eai.beans.msg;

import com.eai.beans.CommentBean;
import com.eai.beans.entity.Designs;
import com.eai.beans.entity.Users;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author ANSHUMAN
 */
@MessageDriven(mappedName = "Comments", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "CommentsMDB"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "CommentsMDB")
    })
public class CommentsMDB implements MessageListener {

	@Resource(name = "mail/NotificationMail")
	private Session mailNotificationMail;

	@PersistenceUnit(unitName = "eai")
    EntityManagerFactory emf;
    
    public CommentsMDB() {
    }

	@Override
    public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage om = (ObjectMessage)message;

			try {
				CommentBean c = (CommentBean)om.getObject();
				String comment = c.getComment();
				long userID = c.getUserId();
				int designID = c.getDesignId();
				EntityManager em = emf.createEntityManager();

				Users u = (Users)em.createNamedQuery("Users.findById").setParameter("id", userID).getResultList().get(0);
				Designs d = (Designs)em.createNamedQuery("Designs.findById").setParameter("id", designID).getResultList().get(0);

				try {
					sendMail(u.getEmail(), "Comment on Design \"" + d.getTitle() + "\"", comment);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (JMSException ex) {
				Logger.getLogger(CommentsMDB.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
    }

	private void sendMail(String email, String subject, String body) throws NamingException, MessagingException {
		MimeMessage message = new MimeMessage(mailNotificationMail);
		message.setSubject(subject);
		message.setRecipients(RecipientType.TO, InternetAddress.parse(email, false));
		message.setText(body);
		Transport.send(message);
	}
    
}
