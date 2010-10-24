package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jms.*;
import javax.naming.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tim Church
 */
public class AddComment extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

		// comment information
		int userID = 1; // TODO: retrieve user ID associated with comment
		int designID = 1; // TODO: retrieve design ID associated with comment
		String comment = request.getParameter("comment");

		// vars for using JMS
		final String TOPIC_NAME = "Comments";
        Context jndiContext = null;
        TopicConnectionFactory topicConnectionFactory = null;
        TopicConnection topicConnection = null;
        TopicSession topicSession = null;
        Topic topic = null;
        TopicPublisher topicPublisher = null;
        TextMessage message = null;

        try {

			// create JNDI InitialContext
			try {
				jndiContext = new InitialContext();
			} catch (NamingException ne) {
				ne.printStackTrace();
			}

			// look up connection factory & topic
			try {
				topicConnectionFactory = (TopicConnectionFactory)jndiContext.lookup("TopicConnectionFactory");
				topic = (Topic) jndiContext.lookup(TOPIC_NAME);
			} catch (NamingException ne) {
				ne.printStackTrace();
			}

			// create connection, session, publisher & text msg
			try {
				topicConnection = topicConnectionFactory.createTopicConnection();
				topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
				topicPublisher = topicSession.createPublisher(topic);
				message = topicSession.createTextMessage();

				message.setText(userID + " " + designID + " " + comment);
				topicPublisher.publish(message);
			} catch (JMSException jmse) {
				jmse.printStackTrace();
			} finally {
				if (topicConnection != null) {
					try {
						topicConnection.close();
					} catch (JMSException jmse) {
						jmse.printStackTrace();
					}
				}
			}

			out.println("THIS WORKS!");

        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
