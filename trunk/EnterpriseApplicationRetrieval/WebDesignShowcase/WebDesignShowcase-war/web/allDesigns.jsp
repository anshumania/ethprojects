<%-- 
    Document   : allDesigns
    Created on : 16.11.2010, 20:10:00
    Author     : Max
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>All Web Designs | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1>All Web Designs</h1>

				<center>
                <table class="designs" cellpadding="5">
					<tr>
						<th>Design Name</th>
						<th>URL</th>
						<th>Designer</th>
					</tr>
					<c:forEach items="${allDesigns}" var="design">
						<tr>
							<td><a href="ShowDesign?designID=${design.id}">${design.title}</a></td>
							<td>${design.url}</td>
							<td>
								<c:forEach items="${users}" var="user">
									<c:if test="${user.id == design.userId}">
									<a href="UserDesigns?userID=${user.id}" title="View all designs by this user">
                                                                            ${user.username}
                                                                        </a>
									</c:if>
								</c:forEach>
							</td>
						</tr>
					</c:forEach>
                </table>
				</center>
				<p><a href="addDesign.jsp">[+] Add a New Design</a></p>
				<p><a href="UserDesigns?userID=${requestScope.userID}">Show My Designs</a></p>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>
