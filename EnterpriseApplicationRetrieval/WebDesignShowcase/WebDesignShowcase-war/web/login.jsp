<%-- 
    Document   : login
    Created on : Oct 30, 2010, 10:54:47 PM
    Author     : Tim Church
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1>Login</h1>

                <c:if test='${loginFailed == true}'><p class="error">The username and/or password was incorrect.  Please try again.</p></c:if>

                <form action="Login" method="post">
                    <table class="loginForm">
                        <tr>
                            <th><label for="username">Username</label></th>
                            <td><input type="text" name="username" size="30" maxlength="20" /></td>
                        </tr>
                        <tr>
                            <th><label for="password">Password</label></th>
                            <td><input type="password" name="password" size="30" maxlength="20" /></td>
                        </tr>
                        <tr>
                            <th></th>
                            <td>
                                <input type="submit" value="Submit" />
                            </td>
                        </tr>
                    </table>
                </form>
                <p>
                    Don't have a user account yet? <a href="newUser.jsp">Register now</a>
                </p>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>

