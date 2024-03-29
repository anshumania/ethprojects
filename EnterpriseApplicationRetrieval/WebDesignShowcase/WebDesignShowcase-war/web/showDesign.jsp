<%-- 
    Document   : showDesign
    Created on : Oct 23, 2010, 11:41:12 PM
    Author     : Tim Church
--%>

<%@page import="com.eai.beans.CommentBean"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
	Collection<CommentBean> comments = (Collection<CommentBean>)request.getAttribute("comments");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${design.title} | Web Design Showcase</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="container">
            <%@ include file="header.jspf" %>
            <div id="main">
                <h1><a href="${design.url}">${design.title}</a></h1>
                <p>by <a href="UserDesigns?userID=${designUser.id}">${designUser.firstname} ${designUser.lastname}</a></p>
                <img src="${design.imageUrl}" alt="${design.title} Screenshot" class="design" />

                <div id="comments">
                    <h2>Comments:</h2>
                    <ul class="comments">
                        <%
                            if(comments.isEmpty()) {
                                out.print("No comments yet.");
                            } else {
                                for (CommentBean c : comments) {
                                        out.print("<li class='comment'>");
                                        out.print(c.getComment());
                                        out.print("</li>\n");
                                }
                            }
                        %>
                    </ul>

                    <h3>Add a comment:</h3>
                    <form action="AddComment" method="post">
                        <textarea name="comment" cols="60" rows="6"></textarea><br/>
                        <input type="hidden" name="designID" value="${design.id}" />
                        <input type="submit" value="Submit Comment" />
                    </form>
                </div>
            </div>
            <%@ include file="footer.jspf" %>
        </div>
    </body>
</html>
