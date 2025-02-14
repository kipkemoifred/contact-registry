
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    private static final String INSERT_USER_SQL = "INSERT INTO contacts (full_name, email,phone_number,id_number,dob,gender,organization) VALUES (?,?,?,?,?,?,?)";
    private static final String SELECT_USER_BY_ID = "SELECT id, full_name, email,phone_number FROM contacts WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM contacts";
    private static final String DELETE_USER_SQL = "DELETE FROM contacts WHERE id = ?";
    private static final String UPDATE_USER_SQL = "UPDATE contacts SET full_name = ?, email = ?,phone_number = ?  WHERE id = ?";

    public void insertUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getIdNumber());
            ps.setString(5, user.getDob());
            ps.setString(6, user.getGender());
            ps.setString(7, user.getOrganization());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User selectUser(int id) {
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone_number")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER_SQL)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER_SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
