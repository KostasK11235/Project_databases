import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InsertIntoTableWindow().setVisible(true);
            }
        });
    }
}