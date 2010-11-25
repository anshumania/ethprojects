<%-- 
    Document   : index
    Created on : 24 Nov, 2010, 6:32:54 PM
    Author     : ANSHUMAN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add Tasks</title>
    </head>
    <body>
        <h1>Enter the tasks</h1>

        <form action="CompanyServlet" method="post">
            <table>
            <tr>
                <td>
                    <label for="task1">Task1</label>
                    <input type="text" name="task1">
                </td>
                <td>
                    <select name="prioritytask1">
                        <option value="9">high</option>
                        <option value="7">med</option>
                        <option value="4">low</option>
                    </select>
                </td>
                <td>
                    Runtime <input type="text" name="runtimetask1" value="1000"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="task2">Task2</label>
                    <input type="text" name="task2">
                </td>
                <td>
                    <select name="prioritytask2">
                        <option value="7">med</option>
                        <option value="4">low</option>
                        <option value="9">high</option>
                    </select>
                </td>
                <td>
                    Runtime <input type="text" name="runtimetask2" value="1000"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="task3">Task3</label>
                    <input type="text" name="task1">
                </td>
                <td>
                    <select name="prioritytask3">
                        <option value="4">low</option>
                        <option value="9">high</option>
                        <option value="7">med</option>
                    </select>
                </td>
                <td>
                    Runtime <input type="text" name="runtimetask3" value="10"/>
                </td>
            </tr>

            <tr>
                <td>
                <input type="submit" value="Post Tasks" />
                </td>
            </tr>
            </table>

        </form>
    </body>
</html>
