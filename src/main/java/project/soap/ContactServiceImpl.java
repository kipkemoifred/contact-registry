package project.soap;

import jakarta.jws.WebService;
import project.DBConnection;
import project.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "project.soap.ContactService")
public class ContactServiceImpl implements ContactService {
    private static final String SELECT_USER_BY_ORGANIZATION_NAME = "SELECT id, full_name, email,phone_number FROM contacts WHERE organization = ?";

    @Override
    public List<User> getContactsByOrganization(String organizationName) {
        return selectUser(organizationName);
    }

    public List<User> selectUser(String organizationName) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ORGANIZATION_NAME)) {
            ps.setString(1, organizationName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone_number")));
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
