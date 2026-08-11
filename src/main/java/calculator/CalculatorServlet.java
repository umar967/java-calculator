package calculator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.*;

/**
 * REST endpoint – POST /api/calculate
 *
 * Request body (JSON):
 *   { "operand1": 5, "operand2": 3, "operator": "+" }
 *   For unary ops (sin, cos, log …) operand2 is ignored.
 *
 * Response (JSON):
 *   { "result": 8.0,  "error": null }
 *   { "result": null, "error": "Cannot divide by zero" }
 */
@WebServlet("/api/calculate")
public class CalculatorServlet extends HttpServlet {

    private Calculator calculator;

    @Override
    public void init() {
        calculator = new Calculator();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Read raw JSON body
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        String body = sb.toString();

        try {
            double  operand1 = parseDouble(body, "operand1");
            double  operand2 = parseDouble(body, "operand2");
            String  operator = parseString(body, "operator");

            double result;
            switch (operator) {
                case "+":       result = calculator.add(operand1, operand2);      break;
                case "-":       result = calculator.subtract(operand1, operand2); break;
                case "*":       result = calculator.multiply(operand1, operand2); break;
                case "/":       result = calculator.divide(operand1, operand2);   break;
                case "%":       result = calculator.modulus(operand1, operand2);  break;
                case "sin":     result = calculator.sin(operand1);                break;
                case "cos":     result = calculator.cos(operand1);                break;
                case "tan":     result = calculator.tan(operand1);                break;
                case "cosec":   result = calculator.cosec(operand1);              break;
                case "sec":     result = calculator.sec(operand1);                break;
                case "cot":     result = calculator.cot(operand1);                break;
                case "log":     result = calculator.log(operand1);                break;
                case "antilog": result = calculator.antilog(operand1);            break;
                default:        throw new IllegalArgumentException("Unknown operator: " + operator);
            }

            writeJson(resp, "{\"result\":" + result + ",\"error\":null}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, "{\"result\":null,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ── JSON helpers (no external lib needed) ─────────────────────────────────

    /** Extract a double from a JSON string by key name. Returns 0.0 if absent. */
    private double parseDouble(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        if (idx < 0) return 0.0;
        int colon = json.indexOf(':', idx);
        int end   = json.indexOf(',', colon);
        if (end < 0) end = json.indexOf('}', colon);
        String raw = json.substring(colon + 1, end).trim();
        return Double.parseDouble(raw);
    }

    /** Extract a string value from a JSON string by key name. */
    private String parseString(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx);
        int q1    = json.indexOf('"', colon + 1);
        int q2    = json.indexOf('"', q1 + 1);
        return json.substring(q1 + 1, q2);
    }

    private String escapeJson(String s) {
        if (s == null) return "Unknown error";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.getWriter().write(json);
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin",  "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
