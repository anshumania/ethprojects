package com.eai.servlet;

import com.eai.beans.UserBean;
import com.eai.beans.session.SessionFacadeLocal;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Tim Church
 */
@WebServlet(name = "EditUser", urlPatterns = {"/EditUser"})
public class EditUser extends HttpServlet {
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

        //TODO - validate input data (length, type, required)

        System.out.println("paramMap " + request.getParameterMap());

        if(request.getParameter("Operation") != null )
        {
            String operation = (String)request.getParameter("Operation");
            if(operation.equals("Edit Account"))
            {
        UserBean userBean = new UserBean();
        // the primary key
        userBean.setUsername(request.getParameter("username"));
        // any of the following can be updated
        userBean.setPassword(request.getParameter("password"));
        userBean.setFirstname(request.getParameter("firstName"));
        userBean.setLastname(request.getParameter("lastName"));
        UserBean user = sessionFacade.updateUser(userBean);
//            Users user = userSession.updateUser(userBean);

        //Automatically login user by saving user to session
        HttpSession session = request.getSession(true);
                System.out.println("now the name is=" + user.getFirstname());
//        session.removeAttribute("user");
        session.setAttribute("user", user);


        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/addDesign.jsp");
        dispatcher.forward(request, response);
            }
        else
            if (operation.equals("Delete Account"))
            {
                UserBean userBean = (UserBean)request.getSession().getAttribute("user");
                sessionFacade.deleteUser(userBean);
                request.getSession().removeAttribute("user");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
            }


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
