package calculator;

import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private Connection conn;

    /**
     * Reads DB credentials from environment variables (set in docker-compose.yml).
     * Falls back to localhost defaults for local development.
     */
    public DatabaseManager() throws Exception {
        String host     = getEnv("MYSQL_HOST",     "localhost");
        String port     = getEnv("MYSQL_PORT",     "3306");
        String dbName   = getEnv("MYSQL_DB",       "calculator");
        String user     = getEnv("MYSQL_USER",     "root");
        String password = getEnv("MYSQL_PASSWORD", "12345");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                   + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        conn = DriverManager.getConnection(url, user, password);
    }

    /** Save a calculation record to the history table. */
    public void saveCalculation(double operand1, String operator, double operand2, double result) {
        String sql = "INSERT INTO history (operand1, operator, operand2, result) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, operand1);
            stmt.setString(2, operator);
            stmt.setDouble(3, operand2);
            stmt.setDouble(4, result);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save calculation: " + e.getMessage());
        }
    }

    /**
     * Retrieve all history records (newest first, max 100).
     * Returns a list of maps so the caller never holds an open ResultSet.
     */
    public List<Map<String, Object>> getHistory() {
        List<Map<String, Object>> records = new ArrayList<>();
        String sql = "SELECT id, operand1, operator, operand2, result, created_at " +
                     "FROM history ORDER BY created_at DESC LIMIT 100";
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id",         rs.getInt("id"));
                row.put("operand1",   rs.getDouble("operand1"));
                row.put("operator",   rs.getString("operator"));
                row.put("operand2",   rs.getDouble("operand2"));
                row.put("result",     rs.getDouble("result"));
                row.put("created_at", rs.getString("created_at"));
                records.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch history: " + e.getMessage());
        }
        return records;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
