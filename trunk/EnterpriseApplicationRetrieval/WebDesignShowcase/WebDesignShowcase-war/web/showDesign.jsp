<%-- 
    Document   : showDesign
    Created on : Oct 23, 2010, 11:41:12 PM
    Author     : Tim Church
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DESIGN TITLE | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1><a href="">DESIGN TITLE</a></h1>
                <p>by <a href="">USERNAME</a></p>
                <img src="devcheatsheet.jpg" alt="DESIGN ALT TEXT" class="design" />

                <div id="comments">
                    <h2>Comments:</h2>
                    <ul class="comments">
                        <li class="comment">THIS IS A COMMENT</li>
                    </ul>

                    <h3>Add a comment:</h3>
                    <form action="AddComment" method="post">
                        <textarea name="comment" cols="60" rows="6"></textarea><br/>
                        <input type="submit" value="Submit Comment" />
                    </form>
                </div>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>
