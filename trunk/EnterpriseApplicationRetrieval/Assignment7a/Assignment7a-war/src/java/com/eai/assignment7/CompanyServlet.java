/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.assignment7;

import com.eai.assignment7.beans.TaskBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ANSHUMAN
 */
@WebServlet(name="CompanyServlet", urlPatterns={"/CompanyServlet"})
public class CompanyServlet extends HttpServlet {
    @EJB
    private CompanySessionLocal companySession;
   
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
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CompanyServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CompanyServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
            */


            List<TaskBean> taskBeans = new ArrayList<TaskBean>();

            for(int i=1;i<=3;i++)
            {
                
                String task = "task" + i;
                String name = request.getParameter(task);
                String priority = request.getParameter("priority"+task);
                String runtime = request.getParameter("runtime"+task);

                TaskBean taskBean = new TaskBean();
                taskBean.setName(name);
                taskBean.setPriority(Integer.parseInt(priority));
                taskBean.setRunTime(Integer.parseInt(runtime));
                taskBean.setOffice("office"+i);

                taskBeans.add(taskBean);

            }




            System.out.println("postingTasks");
            companySession.postTasks(taskBeans);

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/tasksPosted.jsp");
            dispatcher.forward(request, response);


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
