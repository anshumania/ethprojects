<%-- 
    Document   : index
    Created on : 12 Oct, 2010, 1:27:19 AM
    Author     : ANSHUMAN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Assignment2 3 Tier Application</h1>
        <form action="AssignmentServlet"  method="GET">
            <input type="hidden" name="tierAction" value="viewCustomers"/>
            <select name="cityName">
                <option value="zurich">Zurich</option>
                <option value="berne">Berne</option>
            </select>
            <input type="submit" value="View Customers" />
        </form>
        <p>Once your city is chosen you can perform your operations</p>
    </body>
</html>
