/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.servlet;

import com.eai.entity.Address;
import com.eai.entity.Customer;
import com.eai.session.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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
@WebServlet(name="AssignmentServlet", urlPatterns={"/AssignmentServlet"})
public class AssignmentServlet extends HttpServlet {

    @EJB
    private AssignmentSessionBeanLocal assignSBLocal;
   
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
            List<Customer> customers = assignSBLocal.fetchAllCustomers();
			ArrayList<List<Address>> addresses = new ArrayList<List<Address>>();

			for (Customer c : customers)
			{
				List<Address> a = assignSBLocal.fetchAddressesByCustomerId(c);
				addresses.add(a);
			}

			request.setAttribute("addresses", addresses);
			request.setAttribute("customers", customers);
			RequestDispatcher view = request.getRequestDispatcher("showCustomers.jsp");
			view.forward(request, response);
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
