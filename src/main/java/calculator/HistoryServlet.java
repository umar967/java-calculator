package calculator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;
import java.util.*;

/**
 * REST endpoint – GET /api/history
 *
 * Returns the last 100 calculations as a JSON array:
 * [
 *   { "id":1, "operand1":5.0, "operator":"+", "operand2":3.0,
 *     "result":8.0, "created_at":"2024-01-01 12:00:00" },
 *   ...
 * ]
 *
 * Returns an empty array [] when the database is unavailable.
 */
@WebServlet("/api/history")
public class HistoryServlet extends HttpServlet {

    private DatabaseManager dbManager;

    @Override
    public void init() {
        try {
            dbManager = new DatabaseManager();
        } catch (Exception e) {
            System.out.println("HistoryServlet: DB unavailable – " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (dbManager == null) {
            resp.getWriter().write("[]");
            return;
        }

        List<Map<String, Object>> records = dbManager.getHistory();
        resp.getWriter().write(toJsonArray(records));
    }

    // ── JSON serialisation (no external lib) ──────────────────────────────────

    private String toJsonArray(List<Map<String, Object>> records) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < records.size(); i++) {
            sb.append(toJsonObject(records.get(i)));
            if (i < records.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJsonObject(Map<String, Object> row) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object val = entry.getValue();
            if (val instanceof String) {
                sb.append("\"").append(escapeJson((String) val)).append("\"");
            } else {
                sb.append(val);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin",  "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
