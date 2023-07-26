import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectTableWindow extends JFrame {
    private JComboBox<String> dropdownList;
    private JButton confirmButton;

    public SelectTableWindow() {
        setTitle("Choose two dates");
        setSize(400, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        setContentPane(panel);

        // Create the list of string
        String[] tables = {"Admin", "Branch", "Destination", "Driver", "Event", "Guide", "IT", "IT_logs", "Languages"
                , "Manages", "Offers", "Phones", "Reservation", "Reservation_Offers", "Travel_to", "Trip", "Worker"};

        // Create the dropdown list
        dropdownList = new JComboBox<>(tables);
        panel.add(dropdownList, BorderLayout.CENTER);

        confirmButton = new JButton("Confirm");
        confirmButton.setPreferredSize(new Dimension(100, 30));
        panel.add(confirmButton, BorderLayout.SOUTH);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) dropdownList.getSelectedItem();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new InsertIntoTableWindow(selectedTable).setVisible(true);
                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SelectTableWindow().setVisible(true);
            }
        });
    }
}