package project;

import project.model.User;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    private static final String INSERT_USER_SQL = "INSERT INTO contacts (full_name, email,phone_number,id_number,dob,gender,organization,masked_name,masked_phone_number,hashed_phone_number) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String SELECT_USER_BY_ID = "SELECT id, full_name, email,phone_number FROM contacts WHERE id = ?";
    private static final String SELECT_USER_BY_HASHED_PHONENUMBER = "SELECT id, full_name, email,phone_number FROM contacts WHERE hashed_phone_number = ?";
    private static final String SELECT_USER_BY_MASKED_PHONENUMBER_AND_MASKED_NAME = "SELECT id, full_name, email,phone_number FROM contacts WHERE masked_phone_number = ? and  masked_name = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM contacts";
    private static final String DELETE_USER_SQL = "DELETE FROM contacts WHERE id = ?";
    private static final String UPDATE_USER_SQL = "UPDATE contacts SET full_name = ?, email = ?,phone_number = ?  WHERE id = ?";
    private static final String SELECT_CONTACTS_BY_ORG = "SELECT * FROM contacts WHERE organization = ?";

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
            // add masked name and phonenumber
            String maskedName=maskName(user.getName());
            String maskPhoneNumber=maskPhoneNumber(user.getPhoneNumber());
            // hashed phonenumber
            String hashedPhoneNumber=hashPhoneNumber(user.getPhoneNumber());
            ps.setString(8, maskedName);
            ps.setString(9, maskPhoneNumber);
            ps.setString(10, hashedPhoneNumber);
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

    public User getPhoneNumberByPhoneHashOrMaskedNameAndPhone(String enteredHash, String maskedName,String maskedPhoneNumber) {
        User user = null;
        if(enteredHash!=null){
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_HASHED_PHONENUMBER)) {
                ps.setString(1, enteredHash);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    user = new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone_number"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (maskedName!=null&&maskedPhoneNumber!=null) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_MASKED_PHONENUMBER_AND_MASKED_NAME)) {
                ps.setString(1, maskedPhoneNumber);
                ps.setString(2, maskedName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    user = new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone_number"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    public static String maskName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return fullName;
        }

        String[] parts = fullName.split("\\s+");
        int length = parts.length;

        if (length == 1) {
            return fullName; // Only one name, no masking needed
        }

        StringBuilder maskedName = new StringBuilder();
        maskedName.append(parts[0]); // Always keep the first name

        if (length == 2) {
            // Mask the second name if only two parts
            maskedName.append(" *****");
        } else if (length == 3) {
            // Mask the middle name if three parts
            maskedName.append(" ***** ").append(parts[2]);
        } else {
            // Mask all names except the first and last
            for (int i = 1; i < length - 1; i++) {
                maskedName.append(" *****");
            }
            maskedName.append(" ").append(parts[length - 1]); // Keep the last name
        }

        return maskedName.toString();
    }
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 6) {
            return phoneNumber; // Return as is if too short to mask
        }

        int length = phoneNumber.length();
        int middleStart = length / 2 - 1; // Start masking from the middle
        int middleEnd = middleStart + 3; // Mask 3 digits

        StringBuilder maskedNumber = new StringBuilder(phoneNumber);
        for (int i = middleStart; i < middleEnd; i++) {
            maskedNumber.setCharAt(i, '*'); // Replace middle digits with '*'
        }

        return maskedNumber.toString();
    }
    public static String hashPhoneNumber(String phoneNumber) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(phoneNumber.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hashBytes)); // Convert to hex
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone_number")));
            }
        }
        return users;
    }

    public boolean updateApiUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_SQL)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setInt(4, user.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteApiUser(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER_SQL)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }


    public List<User> getContactsByOrganization(String organization) {
        System.out.println("org " + organization);
        List<User> contacts = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTACTS_BY_ORG)) {

            stmt.setString(1, organization);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new User(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone_number")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger instead of printing stack trace
        }

        return contacts;
    }


}
