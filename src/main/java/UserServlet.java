

import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        switch (action) {
            case "/new":
                request.getRequestDispatcher("user-form.jsp").forward(request, response);
                break;
            case "/insert":
                userDAO.insertUser(new User(request.getParameter("full_name"), request.getParameter("email"), request.getParameter("phone_number"),request.getParameter("id_number")));
                response.sendRedirect("list");
                break;
            case "/delete":
                userDAO.deleteUser(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("list");
                break;
            case "/edit":
                request.setAttribute("user", userDAO.selectUser(Integer.parseInt(request.getParameter("id"))));
                request.getRequestDispatcher("user-form.jsp").forward(request, response);
                break;
            case "/update":
                userDAO.updateUser(new User(Integer.parseInt(request.getParameter("id")), request.getParameter("name"), request.getParameter("email"), request.getParameter("country")));
                response.sendRedirect("list");
                break;
            default:
                request.setAttribute("users", userDAO.selectAllUsers());
                request.getRequestDispatcher("user-list.jsp").forward(request, response);
                break;
        }
    }
}
