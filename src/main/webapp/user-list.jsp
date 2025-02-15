
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, project.model.User" %>
<%
  List<User> users = (List<User>) request.getAttribute("users");
%>
<html>
<head>
  <title>project.model.User List</title>
</head>
<body>
<h2>project.model.User List</h2>
<a href="new">Add New project.model.User</a>
<table border="1">
  <tr>
    <th>ID</th>
    <th>Name</th>
    <th>Email</th>
    <th>Phone Number</th>
    <th>Actions</th>
  </tr>
  <% for (User user : users) { %>
  <tr>
    <td><%= user.getId() %></td>
    <td><%= user.getName() %></td>
    <td><%= user.getEmail() %></td>
    <td><%= user.getPhoneNumber() %></td>
    <td>
      <a href="edit?id=<%= user.getId() %>">Edit</a> |
      <a href="delete?id=<%= user.getId() %>" onclick="return confirm('Are you sure?')">Delete</a>
    </td>
  </tr>
  <% } %>
</table>
</body>
</html>

