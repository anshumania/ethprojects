package com.eai.servlet;

import com.eai.beans.CommentBean;
import com.eai.beans.UserBean;
import com.eai.beans.session.SessionFacadeLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Max
 */
public class ShowDesign extends HttpServlet {
    @EJB
    private SessionFacadeLocal sessionFacade;

//	@EJB
//	private CommentSessionLocal csb;

   
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

        try {
			HttpSession session = request.getSession(false);
			
			if (session != null) {
				UserBean user = (UserBean)session.getAttribute("user");
				long userID = user.getId();
				int designID = Integer.parseInt(request.getParameter("designID"));
                                //Collection<CommentBean> c = sessionFacade.findCommentsByUserIdAndDesignId(userID, designID);
                                // get all comments for the design
                                Collection<CommentBean> c = sessionFacade.findCommentsByDesignId(designID);
//				Collection<Comments> c = csb.findCommentsByUserIdAndDesignId(userID, designID);
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/showDesign.jsp");

				session.setAttribute("designID", designID);
				request.setAttribute("comments", c);
				dispatcher.forward(request, response);
			}
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
