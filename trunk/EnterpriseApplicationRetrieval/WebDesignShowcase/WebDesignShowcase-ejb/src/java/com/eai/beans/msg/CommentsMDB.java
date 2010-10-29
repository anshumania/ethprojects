package com.eai.beans.msg;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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
    
    public CommentsMDB() {
    }

	@Override
    public void onMessage(Message message) {
        try {
            System.out.println("MessageReceived :)" + ((TextMessage) message).getText());
        } catch (JMSException ex) {
            Logger.getLogger(CommentsMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
