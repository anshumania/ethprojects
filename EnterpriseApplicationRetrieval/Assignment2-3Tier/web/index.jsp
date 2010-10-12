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
        <form method="post" action="AssignmentServlet">
			<input type="hidden" name="action" value="view" />
			<select name="city">
				<option value="all">All</option>
				<option value="zurich">Zurich</option>
				<option value="bern">Bern</option>
			</select>
			<input type="submit" value="Show customers!" />
		</form>
    </body>
</html>
