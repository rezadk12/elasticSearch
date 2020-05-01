<%@ page import="java.util.List" %>
<%@ page import="basic.HtmlPage" %><%--
  Created by IntelliJ IDEA.
  User: reza_dk
  Date: 2020-03-06
  Time: 6:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<% List<HtmlPage> result=(List<HtmlPage>) request.getAttribute("result");%>
<body>
<% for (HtmlPage r:result){%>

<p style="color: blue"><%= r.getTitle()%></p>
<p><%= r.getBody().substring(0,r.getBody().length()>200?200:r.getBody().length())%></p>

<%}%>
<br>
total time: <%= request.getAttribute("time")%> ms
</body>
</html>
