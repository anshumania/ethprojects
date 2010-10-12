<%-- 
    Document   : displayCustomers
    Created on : 12 Oct, 2010, 10:36:28 PM
    Author     : ANSHUMAN
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
          <script language="javascript">

            function FormSubmit(txt){
                alert("here");
                document.UserChoice.action = txt;
                document.UserChoice.method='POST' ;
                document.UserChoice.submit() ;
            }
        </script>
    </head>
    <body>
        <h1>${cityName} Customers</h1>

        <table border="1">
            <tr>
                <th>CustomerId</th>
                <th>FirstName</th>
                <th>LastName</th>
                <th>UserName</th>
                <th>Email</th>
                <th>Select</th>
            </tr>
            <form name="UserChoice" action="AssignmentServlet"  method="GET">
            <c:forEach items="${customers}" var="customer" varStatus="i">
                <tr>
                    <td>${customer.customerId}</td>
                    <td>${customer.firstname}</td>
                    <td>${customer.lastname}</td>
                    <td>${customer.username}</td>
                    <td>${customer.email}</td>
                    <td><input type="checkbox"  name="${customer.customerId}"/></td>
                </tr>
            </c:forEach>
                <input type="submit" value="Update"/>
                <input type="submit" value="Delete"/>
                <input type="submit" value="ADD"/>
            </form>
        </table>
</body>
</html>
