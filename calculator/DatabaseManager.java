package calculator;

import java.sql.*;

public class DatabaseManager {
    private Connection conn;

    // Constructor: establishes the connection
    public DatabaseManager() throws Exception {
        String url = "jdbc:mysql://localhost:3306/calculator";
        String user = "root"; // Change if your MySQL username is different
        String pass = "12345";     // Change if your MySQL has a password
        conn = DriverManager.getConnection(url, user, pass);
        }
    

    // Save a calculation to the history table
    public void saveCalculation(Double operand1, String operator, Double operand2, Double result) {
        String sql = "INSERT INTO history (operand1, operator, operand2, result) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, operand1);
            stmt.setString(2, operator);
            stmt.setDouble(3, operand2);
            stmt.setDouble(4, result);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Retrieve all history entries
public ResultSet getAllHistory() {
    String sql = "SELECT * FROM history";
    try {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
}