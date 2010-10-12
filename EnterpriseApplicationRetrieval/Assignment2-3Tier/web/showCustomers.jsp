<%-- 
    Document   : showCustomers
    Created on : 12.10.2010, 23:48:03
    Author     : Max
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <c:set var="i" value="0" />
		<c:forEach var="c" items="${customers}">
			<div>&bull; ${c.getCustomerId()} |
				${c.getUsername()} |
				${c.getPassword()} |
				${c.getFirstname()} |
				${c.getLastname()} |
				${c.getEmail()}</div>
			<c:forEach var="a" items="${addresses[i]}">
				<div>- ${a.getAddressId()} |
					${a.getStreet()} |
					${a.getCity()} |
					${a.getZipCode()}</div>
			</c:forEach>
			<br /><br />
			<c:set var="i" value="${i+1}" />
		</c:forEach>
    </body>
</html>
