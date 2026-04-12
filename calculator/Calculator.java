package calculator;
public class Calculator {
    DatabaseManager DBConn = null;
    public Calculator() {
            try {
                DBConn = new DatabaseManager();
            } catch (Exception e) {
                System.out.println("Error initializing database connection");
                e.printStackTrace();
            }
    }
    // Arithmetic
    public double add(double a, double b) {
        if (DBConn != null) {
            DBConn.saveCalculation(a, "+", b, a+b);
        }
        return a + b; 
    }
    public double subtract(double a, double b) { return a - b; }
    public double multiply(double a, double b) { return a * b; }
    public double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }
    public double modulus(double a, double b) {
        if (b == 0) throw new ArithmeticException("Cannot modulus by zero");
        return a % b;
    }

    public double calculate(double a, double b, String operator) {
        double result = 0;
        switch (operator) {
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "*":
                result = a * b;
                break;
            case "/":
                result = a / b;
                break;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
        if (DBConn != null) {
            DBConn.saveCalculation(a, operator, b, result);
        }
        return result;
    }
    // Trigonometric (input in degrees)
    public double sin(double a) { return Math.sin(Math.toRadians(a)); }
    public double cos(double a) { return Math.cos(Math.toRadians(a)); }
    public double tan(double a) { return Math.tan(Math.toRadians(a)); }
    public double cosec(double a) {
        double rad = Math.toRadians(a);
        double sin = Math.sin(rad);
        if (sin == 0) throw new ArithmeticException("Cosec undefined");
        return 1 / sin;
    }
    public double sec(double a) {
        double rad = Math.toRadians(a);
        double cos = Math.cos(rad);
        if (cos == 0) throw new ArithmeticException("Sec undefined");
        return 1 / cos;
    }
    public double cot(double a) {
        double rad = Math.toRadians(a);
        double tan = Math.tan(rad);
        if (tan == 0) throw new ArithmeticException("Cot undefined");
        return 1 / tan;
    }
      public double Trigonometric(double a,double b, String operator) {
        double result = 0;
        switch (operator) {
            case "sin":
                result = Math.sin(Math.toRadians(a));
                break;
            case "cos":
                result = Math.cos(Math.toRadians(a));
                break;
            case "tan":
                result = Math.tan(Math.toRadians(a));
                break;
            case "cosec":
                double rad = Math.toRadians(a);
                double sin = Math.sin(rad);
                if (sin == 0) throw new ArithmeticException("Cosec undefined");
                result = 1 / sin;
                break;
            case "sec":
                rad = Math.toRadians(a);
                double cos = Math.cos(rad);
                if (cos == 0) throw new ArithmeticException("Sec undefined");
                result = 1 / cos;
                break;
            case "cot":
                rad = Math.toRadians(a);
                double tan = Math.tan(rad);
                if (tan == 0) throw new ArithmeticException("Cot undefined");
                result = 1 / tan;
                break;
           
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
        if (DBConn != null) {
            DBConn.saveCalculation(a, operator, b, result);
        }
        return result;
    }

    // Logarithmic
    public double log(double a) {
        if (a <= 0) throw new ArithmeticException("Log undefined");
        return Math.log10(a);
    }
    public double antilog(double a) {
        return Math.pow(10, a);
    }
    public double calculateLogOperation(double value, String operator) {
    double result = 0;
    switch (operator) {
        case "log":
            if (value <= 0) throw new ArithmeticException("Log undefined");
            result = Math.log10(value);
            break;
        case "antilog":
            result = Math.pow(10, value);
            break;
        default:
            throw new IllegalArgumentException("Invalid logarithmic operator");
    }
    if (DBConn != null) {
        DBConn.saveCalculation(value, operator, 0.0, result); // 0.0 as dummy for unused operand
    }
    return result;
}

}
