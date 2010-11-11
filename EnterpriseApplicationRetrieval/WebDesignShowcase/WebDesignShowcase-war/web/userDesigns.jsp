<%-- 
    Document   : userDesigns
    Created on : Oct 31, 2010, 12:03:45 AM
    Author     : Tim Church
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web Designs by ${user.username} | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1>Web Designs by ${user.username}</h1>

				<center>
                <table class="designs" cellpadding="5">
					<tr>
						<th>Design Name</th>
						<th>URL</th>
					</tr>
					<c:forEach items="${userDesigns}" var="design">
						<tr>
							<td><a href="ShowDesign?designID=${design.id}">${design.title}</a></td>
							<td>${design.url}</td>
						</tr>
					</c:forEach>
                </table>
				</center>
				<p><a href="addDesign.jsp">[+] Add a New Design</a></p>
                <p><a href="">Next</a></p>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>

