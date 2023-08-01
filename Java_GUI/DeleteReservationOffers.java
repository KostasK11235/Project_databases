import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeleteReservationOffers extends JFrame {
    private JTextField field1;
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

        JLabel offerCode = new JLabel("Reservation code:");
        panel.add(offerCode);

        field1 = new JTextField(15);
        panel.add(field1);

        helpButton = new JButton("Help");
        panel.add(helpButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = field1.getText();

                String deleteReservationOfferStatus = deleteReservationOfferFunction(code);
                JOptionPane.showMessageDialog(null, deleteReservationOfferStatus);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = """
                        Delete options:
                        1. Insert a reservation code to delete from the table.
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
}
