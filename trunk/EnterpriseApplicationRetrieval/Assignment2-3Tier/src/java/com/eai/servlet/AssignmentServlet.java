/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.servlet;

import com.eai.beans.CustomerBean;
import com.eai.session.AssignmentSessionBeanLocal;
import com.eai.session.AssignmentStatefulSessionBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.*;
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
    AssignmentStatefulSessionBeanLocal assignStateSBLocal;

    
    @EJB
    private AssignmentSessionBeanLocal assignSBLocal;
    private String previousMode;

    public String getPreviousMode() {
        return previousMode;
    }

    public void setPreviousMode(String previousMode) {
        this.previousMode = previousMode;
    }

    

    protected void displayCustomers(String cityName, HttpServletRequest request) {
        Collection customers = null, customers2 = null;

        if (cityName.equals("All")) {
            customers = assignSBLocal.fetchAllCustomers("Zurich");
            customers2 = assignSBLocal.fetchAllCustomers("Berne");
        } else {
            customers = assignSBLocal.fetchAllCustomers(cityName);
        }

        request.setAttribute("customers", customers);
        request.setAttribute("customers2", customers2);
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response.setContentType("text/html;charset=UTF-8");

        // first get the mode of operation

        String sessionBeanMode = request.getParameter("sessionBeanMode");

        // in cases when its a new request
        if (null != sessionBeanMode) {
            setPreviousMode(sessionBeanMode);
            if (sessionBeanMode.equalsIgnoreCase("Stateful")) {
                 assignStateSBLocal= lookupAssignmentStatefulSessionBeanLocal();
                processStatefulRequest(request, response);
            } else {
                if (sessionBeanMode.equalsIgnoreCase("Stateless")) {
                    processStatelessRequest(request, response);
                }

            }
        } // fetch the previous mode as continuation of a previous request
        else {
            if (null != getPreviousMode()) {
                if (getPreviousMode().equalsIgnoreCase("Stateful")) {
                    assignStateSBLocal= lookupAssignmentStatefulSessionBeanLocal();
                    processStatefulRequest(request, response);
                } else {
                    if (getPreviousMode().equalsIgnoreCase("Stateless")) {
                        processStatelessRequest(request, response);
                    }
                }
            }
        }
    }

    /**
    Processes stateful session requests
     */
    protected void processStatefulRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            // first time get it form the dropdown
            String cityName = request.getParameter("cityName");



            // if its stateful set its city -since the first time u get it from the request
            if (null != cityName) {
                assignStateSBLocal.setCityName(cityName);
            }
            if (null == cityName) {

                cityName = (String) request.getSession().getAttribute("cityName");
                assignStateSBLocal.setCityName(cityName);

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

            request.getSession().setAttribute("cityName", assignStateSBLocal.getCityName());

            String tierAction = request.getParameter("tierAction");

            if (tierAction != null && tierAction.equals("viewCustomers")) {


                Collection customers = null, customers2 = null;
                if (cityName.equals("All")) {
                    // just a hack
                    assignStateSBLocal.setCityName("Zurich");
                    customers = assignStateSBLocal.fetchAllCustomers();
                    assignStateSBLocal.setCityName("Berne");
                    customers2 = assignStateSBLocal.fetchAllCustomers();
                } else {
                    customers = assignStateSBLocal.fetchAllCustomers();
                }

                request.setAttribute("customers", customers);
                request.setAttribute("customers2", customers2);
                



                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("updateCustomers")) {
                String changeCity = request.getParameter("changeCity");
                String newUsername = request.getParameter("username");
                String newPassword = request.getParameter("password");
                String newFirstname = request.getParameter("firstname");
                String newLastname = request.getParameter("lastname");
                String newEmail = request.getParameter("email");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                CustomerBean customerBean = new CustomerBean();
                customerBean.setCustomerId(customerId);
                customerBean.setEmail(newEmail);
                customerBean.setLastname(newLastname);
                customerBean.setFirstname(newFirstname);
                customerBean.setUsername(newUsername);
                customerBean.setPassword(newPassword);

                assignStateSBLocal.updateCustomer(customerBean);
                displayCustomers(cityName, request);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("deleteCustomer")) {
                //String changeCity = request.getParameter("changeCity");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignStateSBLocal.deleteCustomer(customerId);
                displayCustomers(cityName, request);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("addCustomer")) {
                String changeCity = request.getParameter("changeCity");
                String newUsername = request.getParameter("username");
                String newPassword = request.getParameter("password");
                String newFirstname = request.getParameter("firstname");
                String newLastname = request.getParameter("lastname");
                String newEmail = request.getParameter("email");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                CustomerBean customerBean = new CustomerBean();
                customerBean.setCustomerId(customerId);
                customerBean.setEmail(newEmail);
                customerBean.setLastname(newLastname);
                customerBean.setFirstname(newFirstname);
                customerBean.setUsername(newUsername);
                customerBean.setPassword(newPassword);

                //
                assignStateSBLocal.setCityName(changeCity);
                assignStateSBLocal.addCustomer(changeCity, customerBean);
                // displayCustomers(cityName, request);


                Collection customers = null, customers2 = null;
                if (cityName.equals("All")) {
                    // just a hack
                    assignStateSBLocal.setCityName("Zurich");
                    customers = assignStateSBLocal.fetchAllCustomers();
                    assignStateSBLocal.setCityName("Berne");
                    customers2 = assignStateSBLocal.fetchAllCustomers();
                } else {
                    customers = assignStateSBLocal.fetchAllCustomers();
                }

                request.setAttribute("customers", customers);
                request.setAttribute("customers2", customers2);


                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("displayAddresses")) {
                String changeCity = request.getParameter("changeCity");
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);

                request.setAttribute("changeCity", changeCity);
                request.setAttribute("addresses", addresses);
                request.getSession().setAttribute("customerId", customerId);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("addAddress")) {
                String changeCity = request.getParameter("changeCity");
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                String newStreet = request.getParameter("street");
                String newCity = request.getParameter("city");
                String newZipCode = request.getParameter("zipcode");
                String newCountry = request.getParameter("country");

                assignSBLocal.addAddress(changeCity, addressId, customerId, newStreet, newCity, newZipCode, newCountry);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);
                request.setAttribute("changeCity", changeCity);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("updateAddress")) {
                String changeCity = request.getParameter("changeCity");
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                String newStreet = request.getParameter("street");
                String newCity = request.getParameter("city");
                String newZipCode = request.getParameter("zipcode");
                String newCountry = request.getParameter("country");

                assignSBLocal.updateAddress(changeCity, addressId, customerId, newStreet, newCity, newZipCode, newCountry);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);
                request.setAttribute("changeCity", changeCity);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("deleteAddress")) {
                String changeCity = request.getParameter("changeCity");
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.deleteAddress(changeCity, addressId, customerId);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);
                request.setAttribute("changeCity", changeCity);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            }
        } finally {
            out.close();
        }
    }

    /**
     ** Processes stateless session requests
     */
    protected void processStatelessRequest(HttpServletRequest request, HttpServletResponse response)
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


            request.getSession().setAttribute("cityName", cityName);



            String tierAction = request.getParameter("tierAction");

            if (tierAction != null && tierAction.equals("viewCustomers")) {


                Collection customers = null, customers2 = null;
                if (cityName.equals("All")) {
                    customers = assignSBLocal.fetchAllCustomers("Zurich");
                    customers2 = assignSBLocal.fetchAllCustomers("Berne");
                } else {
                    customers = assignSBLocal.fetchAllCustomers(cityName);
                }

                request.setAttribute("customers", customers);
                request.setAttribute("customers2", customers2);



                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("updateCustomers")) {
                String changeCity = request.getParameter("changeCity");
                String newUsername = request.getParameter("username");
                String newPassword = request.getParameter("password");
                String newFirstname = request.getParameter("firstname");
                String newLastname = request.getParameter("lastname");
                String newEmail = request.getParameter("email");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.updateCustomer(changeCity, customerId, newUsername, newPassword, newFirstname, newLastname, newEmail);
                displayCustomers(cityName, request);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("deleteCustomer")) {
                String changeCity = request.getParameter("changeCity");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.deleteCustomer(changeCity, customerId);
                displayCustomers(cityName, request);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("addCustomer")) {
                String changeCity = request.getParameter("changeCity");
                String newUsername = request.getParameter("username");
                String newPassword = request.getParameter("password");
                String newFirstname = request.getParameter("firstname");
                String newLastname = request.getParameter("lastname");
                String newEmail = request.getParameter("email");
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.addCustomer(changeCity, customerId, newUsername, newPassword, newFirstname, newLastname, newEmail);
                displayCustomers(cityName, request);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayCustomers.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("displayAddresses")) {
                String changeCity = request.getParameter("changeCity");
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);

                request.setAttribute("changeCity", changeCity);
                request.setAttribute("addresses", addresses);
                request.getSession().setAttribute("customerId", customerId);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("addAddress")) {
                String changeCity = request.getParameter("changeCity");
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                String newStreet = request.getParameter("street");
                String newCity = request.getParameter("city");
                String newZipCode = request.getParameter("zipcode");
                String newCountry = request.getParameter("country");

                assignSBLocal.addAddress(changeCity, addressId, customerId, newStreet, newCity, newZipCode, newCountry);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);
                request.setAttribute("changeCity", changeCity);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("updateAddress")) {
                String changeCity = request.getParameter("changeCity");
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));
                String newStreet = request.getParameter("street");
                String newCity = request.getParameter("city");
                String newZipCode = request.getParameter("zipcode");
                String newCountry = request.getParameter("country");

                assignSBLocal.updateAddress(changeCity, addressId, customerId, newStreet, newCity, newZipCode, newCountry);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);
                request.setAttribute("changeCity", changeCity);
                request.setAttribute("customerId", customerId);
                request.setAttribute("addresses", addresses);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/displayAddresses.jsp");
                dispatcher.forward(request, response);
            } else if (tierAction != null && tierAction.equals("deleteAddress")) {
                String changeCity = request.getParameter("changeCity");
                int addressId = Integer.parseInt(request.getParameter("addressid"));
                int customerId = Integer.parseInt(request.getParameter("customerid"));

                assignSBLocal.deleteAddress(changeCity, addressId, customerId);
                Collection addresses = assignSBLocal.fetchAllAddressesForCustomer(changeCity, customerId);
                request.setAttribute("changeCity", changeCity);
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

    private AssignmentStatefulSessionBeanLocal lookupAssignmentStatefulSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AssignmentStatefulSessionBeanLocal) c.lookup("java:global/Assignment2-3Tier/AssignmentStatefulSessionBean!com.eai.session.AssignmentStatefulSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
