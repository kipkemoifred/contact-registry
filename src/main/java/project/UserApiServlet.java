package project;

import com.google.gson.Gson;
import project.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/users/*")
public class UserApiServlet extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson;

    public void init() {
        userDAO = new UserDAO();
        gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<User> users = userDAO.getAllUsers();
                response.getWriter().write(gson.toJson(users));
            } else {
                int userId = Integer.parseInt(pathInfo.substring(1));
                User user = userDAO.selectUser(userId);
                if (user != null) {
                    response.getWriter().write(gson.toJson(user));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\": \"User not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();
        int hashedPhoneNumber = Integer.parseInt(pathInfo.substring(1));
        int maskedPhoneNumber = Integer.parseInt(pathInfo.substring(1));
        int maskedName = Integer.parseInt(pathInfo.substring(1));

        try (BufferedReader reader = request.getReader()) {
            User user = gson.fromJson(reader, User.class);
            userDAO.insertUser(user);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\": \"User created successfully\"}");
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try (BufferedReader reader = request.getReader()) {
            User user = gson.fromJson(reader, User.class);
            boolean updated = userDAO.updateApiUser(user);
            if (updated) {
                response.getWriter().write("{\"message\": \"User updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"User not found\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                int userId = Integer.parseInt(pathInfo.substring(1));
                boolean deleted = userDAO.deleteApiUser(userId);
                if (deleted) {
                    response.getWriter().write("{\"message\": \"User deleted successfully\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\": \"User not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
