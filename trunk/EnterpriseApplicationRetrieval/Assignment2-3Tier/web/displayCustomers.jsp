<%-- 
    Document   : displayCustomers
    Created on : 12 Oct, 2010, 10:36:28 PM
    Author     : ANSHUMAN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${cityName} Customers | EAI Assignment #2</title>
    </head>
    <body>
        <h1>${cityName} Customers</h1>


        <table border="0">
            <tr>
                <th>CustomerId</th>
                <th>FirstName</th>
                <th>LastName</th>
                <th>UserName</th>
		<th>Password</th>
                <th>Email</th>
                <th></th>
                <th></th>
                <th></th>
            </tr>

            <c:set var="i" value="1" />
            <c:forEach items="${customers}" var="customer">
            <tr>
                    <form method="post" action="AssignmentServlet">
                    <td>${customer.customerId}</td>
                    <td><input type="text" name="firstname" value="${customer.firstname}" /></td>
                    <td><input type="text" name="lastname" value="${customer.lastname}" /></td>
                    <td><input type="text" name="username" value="${customer.username}" /></td>
                    <td><input type="text" name="password" value="${customer.password}" /></td>
                    <td><input type="text" name="email" value="${customer.email}" /></td>
                    <td>
                            <input type="hidden" name="customerid" value="${customer.customerId}" />
                            <input type="hidden" name="tierAction" value="updateCustomers" />
                            <input type="submit" value="Update" />
                    </td>
                    </form>
                    <form method="post" action="AssignmentServlet">
                    <td>
                            <input type="hidden" name="customerid" value="${customer.customerId}" />
                            <input type="hidden" name="tierAction" value="deleteCustomer" />
                            <input type="submit" value="Delete" />
                    </td>
                    </form>
                    <form method="get" action="AssignmentServlet">
                    <td>
                            <input type="hidden" name="customerid" value="${customer.customerId}" />
                            <input type="hidden" name="tierAction" value="displayAddresses" />
                            <input type="submit" value="View Addresses" />
                    </td>
                    </form>
            </tr>
            <c:set var="i" value="${customer.customerId+1}" />
            </c:forEach>

            <tr>
                    <form method="post" action="AssignmentServlet">
                    <td><input type="hidden" name="customerid" value="${i}" /></td>
                    <td><input type="text" name="firstname" value="" /></td>
                    <td><input type="text" name="lastname" value="" /></td>
                    <td><input type="text" name="username" value="" /></td>
                    <td><input type="text" name="password" value="" /></td>
                    <td><input type="text" name="email" value="" /></td>
                    <td>
                            <input type="hidden" name="tierAction" value="addCustomer" />
                            <input type="submit" value="Add" />
                    </td>
                    </form>
                    <td></td>
                    <td></td>
            </tr>
        </table>
</body>
</html>
