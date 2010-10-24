package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
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
	@Resource(name = "Comments")
	private Topic comments;
	@Resource(name = "TopicConnectionFactory")
	private ConnectionFactory topicConnectionFactory;
   
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

        try {
			sendJMSMessageToComments(userID + " " + designID + " " + comment);
			out.println("THIS WORKS!");
        } catch (JMSException jmse) {
			jmse.printStackTrace();
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

	private Message createJMSMessageForcomments(Session session, String messageData) throws JMSException {
		// TODO create and populate message to send
		TextMessage tm = session.createTextMessage();
		tm.setText(messageData);
		return tm;
	}

	private void sendJMSMessageToComments(String messageData) throws JMSException {
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
