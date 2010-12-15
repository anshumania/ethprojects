package com.eai.servlet;

import com.eai.beans.CommentBean;
import com.eai.beans.UserBean;
import com.eai.beans.session.SessionFacadeLocal;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Tim Church
 */
public class AddComment extends HttpServlet {
    @EJB
    private SessionFacadeLocal sessionFacade;

//    @EJB
//    private CommentSessionLocal csb;

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

		HttpSession session = request.getSession(false);

		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");

			// comment information
			long userID = user.getId();
			//int designID = (Integer)session.getAttribute("designID");
                        int designID = Integer.parseInt(request.getParameter("designID"));
			String comment = request.getParameter("comment");

			CommentBean commentBean = new CommentBean();
			commentBean.setUserId(userID);
			commentBean.setDesignId(designID);
			commentBean.setComment(comment);

                        sessionFacade.addComment(commentBean);
                        sessionFacade.notifySubscribers(commentBean);

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ShowDesign?designID=" + designID);
			dispatcher.forward(request, response);
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
