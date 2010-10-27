package com.eai.servlet;

import com.eai.beans.CommentBean;
import com.eai.beans.session.CommentSessionBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
public class AddCommentServlet extends HttpServlet {
    
        @EJB
        private CommentSessionBeanLocal commentSessionBean;
        
	


   
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

        CommentBean commentBean = new CommentBean();
        commentBean.setUserId(1);// TODO: retrieve user ID associated with comment
        commentBean.setDesignId(1);// TODO: retrieve design ID associated with comment
        commentBean.setComment(comment);

//        commentSessionBean.findAllUsers();
        commentSessionBean.addComment(commentBean);
        commentSessionBean.notifySubscribers(commentBean);




//
//        try {
//			sendJMSMessageToComments(userID + " " + designID + " " + comment);
//			out.println("THIS WORKS!");
//        } catch (JMSException jmse) {
//			jmse.printStackTrace();
//		} finally {
//            out.close();
//        }
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
