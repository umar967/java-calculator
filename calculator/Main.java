package calculator;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            
        //    if (DBConn != null) {
        //         DBConn.saveCalculation(123.00000, "+", 456.00000, 57.0009);
        //     }
            CalculatorGUI calc = new CalculatorGUI();
            calc.setVisible(true);
        });
    }
}
