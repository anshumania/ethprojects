package beans;

import controller.*;
import entity.*;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;

/**
 *
 * @author Maximilian Speicher (speichem[at]student.ethz.ch)
 */
@MessageDriven(mappedName = "Comments", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "ProcessComments"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "ProcessComments")
    })
public class ProcessComments implements MessageListener {
    
    public ProcessComments() {
    }

	@Override
    public void onMessage(Message message) {
		TextMessage msg = null;

		try {
			if (message instanceof TextMessage) {
				msg = (TextMessage)message;
				String msgContent = msg.getText();

				int userID = Integer.parseInt(msgContent.split(" ")[0]);
				int designID = Integer.parseInt(msgContent.split(" ")[1]);
				String text = msgContent.substring((userID + " " + designID +" ").length());

				CommentsJpaController commentsJpaController = new CommentsJpaController();
				Users user = new UsersJpaController().findUsers(userID);
				Designs design = new DesignsJpaController().findDesigns(designID);
				int commentID = commentsJpaController.getCommentsCount() + 1;

				Comments comment = new Comments();
				comment.setComment(text);
				comment.setDesigns(design);
				comment.setId(commentID);
				comment.setUsers(user);

				try {
					commentsJpaController.create(comment);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
    }
    
}
