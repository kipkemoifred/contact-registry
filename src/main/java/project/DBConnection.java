package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/contact_registry";
    private static final String USER = "javauser";
    private static final String PASSWORD = "password123";

 /*   public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }*/
 public static Connection getConnection() {
     Connection connection = null;
     try {
         // Load MySQL JDBC Driver
         Class.forName("com.mysql.cj.jdbc.Driver");

         // Get connection
         connection = DriverManager.getConnection(URL, USER, PASSWORD);
     } catch (ClassNotFoundException e) {
         System.err.println("MySQL JDBC Driver not found!");
         e.printStackTrace();
     } catch (SQLException e) {
         System.err.println("Database connection failed!");
         e.printStackTrace();
     }
     return connection;
 }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to MySQL successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

