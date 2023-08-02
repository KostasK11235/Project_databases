import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteReservationOffers extends JFrame {
    private JComboBox<String> dropdownList1;
    private JButton deleteButton;
    private JButton helpButton;

    public DeleteReservationOffers()
    {
        setTitle("Delete data from table: reservation_offers");
        setSize(420, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        String[] reservations = getReservationOffers();

        JLabel offerCode = new JLabel("Reservation code:");
        panel.add(offerCode);

        dropdownList1 = new JComboBox<>(reservations);
        panel.add(dropdownList1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) dropdownList1.getSelectedItem();
                String resCode;

                if(!selected.equals(""))
                {
                    String[] parts = selected.split(",");
                    resCode = parts[0];
                }
                else
                {
                    resCode = "";
                }

                String deleteReservationOfferStatus = deleteReservationOfferFunction(resCode);
                JOptionPane.showMessageDialog(null, deleteReservationOfferStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Choose a reservation code to delete from the table.
                        2. Leave the field empty to delete all records of the table!""";
                JOptionPane.showMessageDialog(null, helpMessage);
            }
        });

    }

    private String deleteReservationOfferFunction(String resCode) {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        String deleteStatus = "";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "";

            if (resCode.equals("")) {
                sql = "DELETE FROM reservation_offers";
                PreparedStatement statement = connection.prepareStatement(sql);

                String message = "Are you sure you want to delete all records in the table?";
                int choice = JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                if (choice == 0) {
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0)
                        deleteStatus = "Reservation offers record(s) deleted successfully!";
                    else
                        deleteStatus = "Reservation offers table has no records to delete!";
                } else
                    deleteStatus = "Deletion aborted.";

                statement.close();
                connection.close();
            } else {
                sql = "DELETE FROM reservation_offers WHERE res_offer_code=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, resCode);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0)
                    deleteStatus = "Reservation offers record(s) deleted successfully!";

                statement.close();
                connection.close();
            }

        } catch (SQLException ex) {
            deleteStatus = ex.getMessage();
        }

        return deleteStatus;
    }

    private String[] getReservationOffers()
    {
        String url = "jdbc:mariadb://localhost:3306/project";
        String dbUsername = "root";
        String dbPassword = "";

        List<String> guideCodes = new ArrayList<>();
        guideCodes.add("");

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "SELECT res_offer_code,cust_name,cust_lname FROM reservation_offers";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                String currCode = resultSet.getString("res_offer_code");
                String name = resultSet.getString("cust_name");
                String lname = resultSet.getString("cust_lname");
                String info = currCode + ", Name-Lastname: " + name + "-" + lname;
                guideCodes.add(info);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        return guideCodes.toArray(new String[guideCodes.size()]);
    }
}
