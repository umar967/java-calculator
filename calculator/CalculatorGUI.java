package calculator;
import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CalculatorGUI extends JFrame implements ActionListener {
    private JTextField display;
    private String currentInput = "";
    private double operand1 = 0;
    private String operator = "";
    private Calculator calculator;
    private DatabaseManager dbManager;
    private JButton historyButton;
    private JTable historyTable;
    private JScrollPane historyScrollPane;

    public CalculatorGUI() {
        calculator = new Calculator();
        setTitle("Scientific Calculator");
        setSize(420, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 4, 5, 5));

        try {
            dbManager = new DatabaseManager();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB connection error: " + e.getMessage());
        }

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "%", "sin", "cos", "tan",
            "cosec", "sec", "cot", "C",
            "log", "antilog", "", ""
        };

      historyButton = new JButton("Show History");
historyButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        showHistory();
    }
});
add(historyButton, BorderLayout.SOUTH);

        historyTable = new JTable();
        historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setPreferredSize(new Dimension(400, 200));
        historyScrollPane.setVisible(false);
        add(historyScrollPane, BorderLayout.EAST);

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.addActionListener(this);
            if (text.equals("")) btn.setEnabled(false);
            panel.add(btn);
        }

        getContentPane().setLayout(new BorderLayout(5,5));
        getContentPane().add(display, BorderLayout.NORTH);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.matches("[0-9]") || cmd.equals(".")) {
            currentInput += cmd;
            display.setText(currentInput);
        } else if (cmd.equals("C")) {
            currentInput = "";
            operand1 = 0;
            operator = "";
            display.setText("");
        } else if (cmd.equals("=")) {
            calculateResult();
        } else if (cmd.equals("sin") || cmd.equals("cos") || cmd.equals("tan") ||
                   cmd.equals("cosec") || cmd.equals("sec") || cmd.equals("cot") ||
                   cmd.equals("log") || cmd.equals("antilog")) {
            if (!currentInput.isEmpty()) {
                double value = Double.parseDouble(currentInput);
                double result = 0;
                try {
                    String opName = cmd;
                    switch (cmd) {
                        case "sin": result = calculator.sin(value); break;
                        case "cos": result = calculator.cos(value); break;
                        case "tan": result = calculator.tan(value); break;
                        case "cosec": result = calculator.cosec(value); break;
                        case "sec": result = calculator.sec(value); break;
                        case "cot": result = calculator.cot(value); break;
                        case "log": result = calculator.log(value); break;
                        case "antilog": result = calculator.antilog(value); break;
                    }
                    display.setText(String.valueOf(result));
                    currentInput = String.valueOf(result);

                    // Save history for single-operand operations: operand1 = value, operator = cmd, operand2 = 0
                    saveHistory(value, opName, 0, result);

                } catch (Exception ex) {
                    display.setText("Error");
                    currentInput = "";
                }
            }
        } else if (cmd.equals("%")) {
            if (!currentInput.isEmpty()) {
                operand1 = Double.parseDouble(currentInput);
                operator = "%";
                currentInput = "";
                display.setText("");
            }
        } else if (cmd.equals("+") || cmd.equals("-") || cmd.equals("*") || cmd.equals("/")) {
            if (!currentInput.isEmpty()) {
                operand1 = Double.parseDouble(currentInput);
                operator = cmd;
                currentInput = "";
                display.setText("");
            }
        }
    }

    private void calculateResult() {
        if (operator.isEmpty() || currentInput.isEmpty()) return;
        double operand2 = Double.parseDouble(currentInput);
        double result = 0;
        try {
            switch (operator) {
                case "+": result = calculator.add(operand1, operand2); break;
                case "-": result = calculator.subtract(operand1, operand2); break;
                case "*": result = calculator.multiply(operand1, operand2); break;
                case "/": result = calculator.divide(operand1, operand2); break;
                case "%": result = calculator.modulus(operand1, operand2); break;
            }
            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result);

            // Save history for binary operations
            saveHistory(operand1, operator, operand2, result);

            operator = "";
        } catch (Exception ex) {
            display.setText("Error");
            currentInput = "";
            operator = "";
        }
    }

    private void saveHistory(double op1, String operator, double op2, double result) {
        if (dbManager != null) {
            dbManager.saveCalculation(op1, operator, op2, result);
        }
    }

    private void showHistory() {
        if (dbManager != null) {
            try {
                ResultSet rs = dbManager.getAllHistory();
                // Use ResultSet to fill JTable
                historyTable.setModel(buildTableModel(rs));
                historyScrollPane.setVisible(true);
                this.pack();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error fetching history: " + e.getMessage());
            }
        }
    }

    // Helper to convert ResultSet to TableModel
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Columns
        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        // Data
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                vector.add(rs.getObject(i));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}