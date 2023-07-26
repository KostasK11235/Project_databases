import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertIntoTableWindow extends JFrame {
    private JComboBox<String> dropdownList;
    private JButton confirmButtom;

    public InsertIntoTableWindow() {
        setTitle("Choose two dates");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        setContentPane(panel);

        // Create the list of string
        String[] tables = {"Select table to insert...", "Admin", "Branch", "Destination", "Driver", "Event", "Guide", "IT", "IT logs", "Languages"
                , "Manages", "Offers", "Phones", "Reservation", "Reservation Offers", "Travel to", "Trip", "Worker"};

        // Create the dropdown list
        dropdownList = new JComboBox<>(tables);
        panel.add(dropdownList, BorderLayout.CENTER);

        confirmButtom = new JButton("Confirm");
        confirmButtom.setPreferredSize(new Dimension(100, 30));
        panel.add(confirmButtom, BorderLayout.SOUTH);

        confirmButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String secondDate = getDateAsString(yearComboBox2, monthComboBox2, dayComboBox2);
                String selectedTable = (String) dropdownList.getSelectedItem();
                JOptionPane.showMessageDialog(null, selectedTable + "SELECTED");

            }
        });
    }
    private String getDateAsString(JComboBox<Integer> yearComboBox, JComboBox<String> monthComboBox, JComboBox<Integer> dayComboBox) {
        int year = (int) yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1; // Add 1 to adjust for zero-based index
        int day = (int) dayComboBox.getSelectedItem();

        // Format the date as a string in the desired format
        String dateAsString = String.format("%04d-%02d-%02d", year, month, day);

        return dateAsString;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InsertIntoTableWindow().setVisible(true);
            }
        });
    }
}