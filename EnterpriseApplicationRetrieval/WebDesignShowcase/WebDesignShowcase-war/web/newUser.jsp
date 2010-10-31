<%-- 
    Document   : newUser
    Created on : Oct 30, 2010, 11:00:42 PM
    Author     : Tim Church
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create a New User Account | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1>Create a New User Account</h1>

                <form action="createUser" method="post">
                    <table class="newUserForm">
                        <tr>
                            <th><label for="username">Username</label></th>
                            <td><input type="text" name="username" size="30" maxlength="20" value="${username}" /></td>
                        </tr>
                        <tr>
                            <th><label for="password">Password</label></th>
                            <td><input type="password" name="password" size="30" maxlength="20" value="${password}" /></td>
                        </tr>
                        <tr>
                            <th><label for="firstName">First Name</label></th>
                            <td><input type="text" name="firstName" size="50" maxlength="50" value="${firstName}" /></td>
                        </tr>
                        <tr>
                            <th><label for="lastName">Last Name</label></th>
                            <td><input type="text" name="lastName" size="50" maxlength="50" value="${lastName}" /></td>
                        </tr>
                        <tr>
                            <th><label for="email">Email</label></th>
                            <td><input type="text" name="email" size="50" maxlength="100" value="${email}" /></td>
                        </tr>
                        <tr>
                            <th></th>
                            <td>
                                <input type="submit" value="Create Account" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>

