package com.eai.servlet;

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
public class Login extends HttpServlet {
    @EJB
    private SessionFacadeLocal sessionFacade;

//    @EJB
//    private UserSessionLocal userSession;


    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //load request parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //String nextPage = request.getParameter("nextPage"); //TODO - save page to return to after login

        //now try to login
        UserBean user = sessionFacade.authenticate(username, password);
//        Users user = userSession.authenticate(username, password);
        if(user != null) {
            //authentication successful.  save user in http session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            response.sendRedirect("UserDesigns");
        } else {
            //login failed, redisplay login page with error message
            request.setAttribute("loginFailed", true);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
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