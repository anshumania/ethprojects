/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.servlet;

import com.eai.entity.Address;
import com.eai.entity.Customer;
import com.eai.session.AssignmentSessionBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
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
@WebServlet(name = "AssignmentServlet", urlPatterns = {"/AssignmentServlet"})
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
            // first time get it form the dropdown
            String cityName = request.getParameter("cityName");
            //for updates/deletes/adds the session is the only place
            // to fetch the values

            if (null == cityName) {
                cityName = (String) request.getSession().getAttribute("cityName");
            }

            System.out.println("requestProcess = " + request.getParameterMap());
            Map<String, String[]> paramMap = request.getParameterMap();
            for (Map.Entry<String, String[]> iterator : paramMap.entrySet()) {

                System.out.println("Key=" + iterator.getKey());
                String[] vals = iterator.getValue();
                for (String val : vals) {
                    System.out.println("value=" + val);
                }
            }

            String tierAction = request.getParameter("tierAction");

            if (tierAction != null && tierAction.equals("viewCustomers")) {
                Collection customers = assignSBLocal.fetchAllCustomers(cityName);

                request.setAttribute("customers", customers);
                request.getSession().setAttribute("cityName", cityName);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("updateCustomers")) {
                String newUsername = request.getParameter("username");
                String newPassword = request.getParameter("password");
                String newFirstname = request.getParameter("firstname");
                String newLastname = request.getParameter("lastname");
                String newEmail = request.getParameter("email");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.updateCustomer(cityName, customerId, newUsername, newPassword, newFirstname, newLastname, newEmail);
                Collection customers = assignSBLocal.fetchAllCustomers(cityName);

                request.setAttribute("customers", customers);
                request.getSession().setAttribute("cityName", cityName);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("deleteCustomer")) {
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.deleteCustomer(cityName, customerId);
                Collection customers = assignSBLocal.fetchAllCustomers(cityName);

                request.setAttribute("customers", customers);
                request.getSession().setAttribute("cityName", cityName);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("addCustomer")) {
                String newUsername = request.getParameter("username");
                String newPassword = request.getParameter("password");
                String newFirstname = request.getParameter("firstname");
                String newLastname = request.getParameter("lastname");
                String newEmail = request.getParameter("email");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.addCustomer(cityName, customerId, newUsername, newPassword, newFirstname, newLastname, newEmail);
                Collection customers = assignSBLocal.fetchAllCustomers(cityName);

                request.setAttribute("customers", customers);
                request.getSession().setAttribute("cityName", cityName);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("displayAddresses")) {

                int customerId = Integer.parseInt(request.getParameter("customerid"));
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(cityName, customerId);

                request.setAttribute("addresses", addresses);
                request.getSession().setAttribute("cityName", cityName);
                request.getSession().setAttribute("customerId", customerId);
                

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("addAddress")) {
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                String newStreet = request.getParameter("street");
                String newCity = request.getParameter("city");
                String newZipCode = request.getParameter("zipcode");
                String newCountry = request.getParameter("country");
                assignSBLocal.addAddress(cityName, addressId, customerId, newStreet, newCity, newZipCode, newCountry);

                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(cityName, customerId);
                request.getSession().setAttribute("cityName", cityName);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("updateAddress")) {
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                String newStreet = request.getParameter("street");
                String newCity = request.getParameter("city");
                String newZipCode = request.getParameter("zipcode");
                String newCountry = request.getParameter("country");
                assignSBLocal.updateAddress(cityName, addressId, customerId, newStreet, newCity, newZipCode, newCountry);

                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(cityName, customerId);
                request.getSession().setAttribute("cityName", cityName);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("deleteAddress")) {
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.deleteAddress(cityName, addressId, customerId);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(cityName, customerId);
                request.getSession().setAttribute("cityName", cityName);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
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
