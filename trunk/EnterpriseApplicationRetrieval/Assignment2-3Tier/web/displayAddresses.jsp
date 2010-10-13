<%-- 
    Document   : displayAddresses
    Created on : Oct 13, 2010, 2:29:20 PM
    Author     : Tim Church
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Addresses for Customer ${customerId} | EAI Assignment #2</title>
    </head>
    <body>
        <h1>Customer ${customerId}'s Addresses</h1>

        <table border="0">
            <tr>
                <th>Address ID</th>
                <th>Street</th>
                <th>City</th>
                <th>Zip Code</th>
		<th>Country</th>
                <th></th>
                <th></th>
            </tr>

            <c:set var="i" value="1" />
            <c:forEach items="${addresses}" var="address">
            <tr>
                <form method="post" action="AssignmentServlet">
                <td><${address.addressId}> - ${address}</td>
                <td><input type="text" name="street" value="${address.getStreet}" /></td>
                <td><input type="text" name="city" value="${address.getCity}" /></td>
                <td><input type="text" name="zipcode" value="${address.zipCode}" /></td>
                <td><input type="text" name="country" value="${address.country.countryName}" /></td>
                <td>
                        <input type="hidden" name="addressid" value="${address.addressId}" />
                        <input type="hidden" name="customerid" value="${customerId}" />
                        <input type="hidden" name="tierAction" value="updateAddress" />
                        <input type="submit" value="Update" />
                </td>
                </form>
                <form method="post" action="AssignmentServlet">
                <td>
                        <input type="hidden" name="addressid" value="${address.addressId}" />
                        <input type="hidden" name="customerid" value="${customerId}" />
                        <input type="hidden" name="tierAction" value="deleteAddress" />
                        <input type="submit" value="Delete" />
                </td>
                </form>
            </tr>
            <c:set var="i" value="${address.addressId+1}" />
            </c:forEach>

            <tr>
                <form method="post" action="AssignmentServlet">
                <td><input type="hidden" name="addressid" value="${i}" /></td>
                <td><input type="text" name="street" value="${address.street}" /></td>
                <td><input type="text" name="city" value="${address.city}" /></td>
                <td><input type="text" name="zipcode" value="${address.zipCode}" /></td>
                <td><input type="text" name="country" value="${address.Country.countryName}" /></td>
                <td>
                        <input type="hidden" name="customerid" value="${customerId}" />
                        <input type="hidden" name="tierAction" value="addAddress" />
                        <input type="submit" value="Add" />
                </td>
                <td></td>
                </form>
            </tr>
            <tr>
                <form method="post" action="AssignmentServlet">
                <td>
                        <input type="hidden" name="customerid" value="${customerId}" />
                        <input type="hidden" name="tierAction" value="viewCustomers" />
                        <input type="submit" value="Back To View Customers" />
                </td>
                </form>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        </table>
    </body>
</html>
