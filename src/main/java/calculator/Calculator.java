package calculator;

public class Calculator {

    DatabaseManager dbManager = null;

    public Calculator() {
        try {
            dbManager = new DatabaseManager();
        } catch (Exception e) {
            System.out.println("DB connection failed – history will not be saved: " + e.getMessage());
        }
    }

    // ── Arithmetic ────────────────────────────────────────────────────────────

    public double add(double a, double b) {
        double result = a + b;
        save(a, "+", b, result);
        return result;
    }

    public double subtract(double a, double b) {
        double result = a - b;
        save(a, "-", b, result);
        return result;
    }

    public double multiply(double a, double b) {
        double result = a * b;
        save(a, "*", b, result);
        return result;
    }

    public double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        double result = a / b;
        save(a, "/", b, result);
        return result;
    }

    public double modulus(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot modulus by zero");
        double result = a % b;
        save(a, "%", b, result);
        return result;
    }

    // ── Trigonometric (input in degrees) ─────────────────────────────────────

    public double sin(double a) {
        double result = Math.sin(Math.toRadians(a));
        save(a, "sin", 0, result);
        return result;
    }

    public double cos(double a) {
        double result = Math.cos(Math.toRadians(a));
        save(a, "cos", 0, result);
        return result;
    }

    public double tan(double a) {
        double result = Math.tan(Math.toRadians(a));
        save(a, "tan", 0, result);
        return result;
    }

    public double cosec(double a) {
        double sin = Math.sin(Math.toRadians(a));
        if (sin == 0) throw new ArithmeticException("Cosec undefined for this angle");
        double result = 1 / sin;
        save(a, "cosec", 0, result);
        return result;
    }

    public double sec(double a) {
        double cos = Math.cos(Math.toRadians(a));
        if (cos == 0) throw new ArithmeticException("Sec undefined for this angle");
        double result = 1 / cos;
        save(a, "sec", 0, result);
        return result;
    }

    public double cot(double a) {
        double tan = Math.tan(Math.toRadians(a));
        if (tan == 0) throw new ArithmeticException("Cot undefined for this angle");
        double result = 1 / tan;
        save(a, "cot", 0, result);
        return result;
    }

    // ── Logarithmic ───────────────────────────────────────────────────────────

    public double log(double a) {
        if (a <= 0) throw new ArithmeticException("Log undefined for non-positive values");
        double result = Math.log10(a);
        save(a, "log", 0, result);
        return result;
    }

    public double antilog(double a) {
        double result = Math.pow(10, a);
        save(a, "antilog", 0, result);
        return result;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void save(double op1, String operator, double op2, double result) {
        if (dbManager != null) {
            dbManager.saveCalculation(op1, operator, op2, result);
        }
    }
}
