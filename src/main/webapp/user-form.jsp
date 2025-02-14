
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User user = (User) request.getAttribute("user");
    boolean isEdit = (user != null);
%>
<html>
<head>
    <title><%= isEdit ? "Edit model.User" : "New model.User" %></title>
</head>
<body>
<h2><%= isEdit ? "Edit model.User" : "New model.User" %></h2>
<form action="<%= isEdit ? "update" : "insert" %>" method="post">
    <% if (isEdit) { %>
    <input type="hidden" name="id" value="<%= user.getId() %>">
    <% } %>
    <label>Name:</label>
    <input type="text" name="full_name" value="<%= isEdit ? user.getName() : "" %>" required><br>

    <label>Email:</label>
    <input type="email" name="email" value="<%= isEdit ? user.getEmail() : "" %>" required><br>

    <label>Phone Number:</label>
    <input type="text" name="phone_number" value="<%= isEdit ? user.getPhoneNumber() : "" %>" required><br>

    <label>ID Number:</label>
    <input type="text" name="id_number" value="<%= isEdit ? user.getIdNumber() : "" %>" required><br>


    <input type="submit" value="<%= isEdit ? "Update" : "Save" %>">
</form>
<a href="list">Back to list</a>
</body>
</html>

