<%-- 
    Document   : AddDesign
    Created on : Oct 24, 2010, 2:19:59 PM
    Author     : Tim Church
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add a Design | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1>Add a New Web Design</h1>

                <form action="AddDesign" method="post">
                    <table class="addDesignForm">
                        <tr>
                            <th><label for="title">Title</label></th>
                            <td><input type="text" name="title" size="50" maxlength="20" /></td>
                        </tr>
                        <tr>
                            <th><label for="url">URL</label></th>
                            <td><input type="text" name="url" size="50" maxlength="200" /></td>
                        </tr>
                        <tr>
                            <th><label for="design">Design Screenshot</label></th>
							<td><input type="text" name="design" size="50" /></td>
                            <!--<td><input type="file" name="design" size="50" /></td>-->
                        </tr>
                        <tr>
                            <th></th>
                            <td>
                                <input type="submit" value="Submit" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>
